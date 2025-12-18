package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ynu.edu.pims.dto.request.*;
import ynu.edu.pims.dto.response.AccountVO;
import ynu.edu.pims.entity.Account;
import ynu.edu.pims.entity.Organization;
import ynu.edu.pims.entity.Registration;
import ynu.edu.pims.entity.Role;
import ynu.edu.pims.repository.AccountRepository;
import ynu.edu.pims.repository.OrganizationRepository;
import ynu.edu.pims.repository.RegistrationRepository;
import ynu.edu.pims.repository.RoleRepository;
import ynu.edu.pims.utils.Const;
import ynu.edu.pims.utils.FlowUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountService implements UserDetailsService {

    @Resource
    private FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder encoder;

    @Resource
    private AccountRepository accountRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private OrganizationRepository organizationRepository;

    @Resource
    private RegistrationRepository registrationRepository;

    /**
     * 用户修改个人信息
     *
     * @param ro 包含用户修改信息的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String modifyAccountInformation(AccountResetRO ro) {
        // 检查用户是否存在
        Optional<Account> accountOpt = accountRepository.findById(ro.getId());
        if (accountOpt.isEmpty()) {
            return "用户不存在";
        }
        // 检查手机号是否被其他用户使用
        if (accountRepository.existsByPhoneAndIdNot(ro.getPhone(), ro.getId())) {
            return "该手机号已被其他用户使用";
        }
        Account account = ro.asViewObject(Account.class, acc -> {
            Account original = accountOpt.get();
            acc.setPassword(original.getPassword());
            acc.setEmail(original.getEmail());
            acc.setRoles(original.getRoles());
        });
        accountRepository.save(account);
        return null;
    }

    /**
     * 老板修改组织内员工的职位信息
     *
     * @param ro 包含组织id、操作者id、员工id和新职位的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String modifyAccountPosition(PositionResetRO ro) {
        // 检查操作者是否是该组织的创建者（老板）
        if (!organizationRepository.existsByIdAndAccountId(ro.getOid(), ro.getBossId())) {
            return "您不是该组织的管理员，无权修改";
        }
        // 查找员工在该组织中的注册记录
        Optional<Registration> regOpt = registrationRepository.findByAccountIdAndOrganizationIdAndState(ro.getEmployeeId(), ro.getOid(), 1);
        if (regOpt.isEmpty()) {
            return "该员工不在您的组织中";
        }
        // 修改员工在该组织的职位
        Registration registration = regOpt.get();
        registration.setPosition(ro.getPosition());
        registrationRepository.save(registration);
        return null;
    }

    /**
     * 老板添加用户并加入组织
     *
     * @param ro 包含组织id、老板id和用户信息的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String addAccountInOrganization(AccountRO ro) {
        // 检查操作者是否是该组织的创建者（老板）
        if (!organizationRepository.existsByIdAndAccountId(ro.getOid(), ro.getBossId())) {
            return "您不是该组织的管理员，无权添加用户";
        }
        // 检查邮箱是否已存在
        if (accountRepository.existsByEmail(ro.getEmail())) {
            return "该邮箱已被注册";
        }
        // 检查手机号是否已存在
        if (accountRepository.existsByPhone(ro.getPhone())) {
            return "该手机号已被注册";
        }
        // 创建用户
        Account account = ro.asViewObject(Account.class, acc -> {
            acc.setPassword(encoder.encode("123456"));
            acc.setAvatar("https://avatars.githubusercontent.com/u/181219839?v=4");
            acc.setAddress("China");
        });
        // 分配 user 角色
        roleRepository.findByRolename("user").ifPresent(role -> account.getRoles().add(role));
        accountRepository.save(account);
        // 加入组织
        Organization org = organizationRepository.findById(ro.getOid()).orElse(null);
        if (org != null) {
            Registration registration = new Registration();
            registration.setAccount(account);
            registration.setOrganization(org);
            registration.setState(1);
            registration.setPosition(ro.getPosition());
            registrationRepository.save(registration);
        }
        return null;
    }

    /**
     * 老板将用户移出组织
     *
     * @param ro 包含组织id、老板id和用户id的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String removeAccountOutOrganization(OutRO ro) {
        // 检查操作者是否是该组织的老板
        if (!organizationRepository.existsByIdAndAccountId(ro.getOid(), ro.getBossId())) {
            return "您不是该组织的管理员，无权操作";
        }
        // 查找 registration 记录
        Optional<Registration> regOpt = registrationRepository.findByAccountIdAndOrganizationId(ro.getAid(), ro.getOid());
        if (regOpt.isEmpty()) {
            return "该用户不在组织中";
        }
        // 修改 registration 状态为 3（删除）
        Registration registration = regOpt.get();
        registration.setState(3);
        registration.setPosition("OUT");
        registrationRepository.save(registration);
        return null;
    }

    /**
     * 获取组织内所有已通过的成员
     *
     * @param oid 组织id
     * @return 成员列表
     */
    public List<AccountVO> getAllAccounts(Long oid) {
        return registrationRepository.findByOrganizationIdAndState(oid, 1).stream()
                .map(this::toAccountVO)
                .toList();
    }

    /**
     * 根据用户名模糊搜索组织内成员
     *
     * @param oid      组织id
     * @param username 用户名关键字
     * @return 匹配的成员列表
     */
    public List<AccountVO> getAccountsByUsername(Long oid, String username) {
        return registrationRepository.findByOrganizationIdAndStateAndAccountUsernameContaining(oid, 1, username).stream()
                .map(this::toAccountVO)
                .toList();
    }

    /**
     * 根据职位模糊搜索组织内成员
     *
     * @param oid      组织id
     * @param position 职位关键字
     * @return 匹配的成员列表
     */
    public List<AccountVO> getAccountsByPosition(Long oid, String position) {
        return registrationRepository.findByOrganizationIdAndStateAndPositionContaining(oid, 1, position).stream()
                .map(this::toAccountVO)
                .toList();
    }

    /**
     * 根据用户名和职位模糊搜索组织内成员
     *
     * @param oid      组织id
     * @param username 用户名关键字
     * @param position 职位关键字
     * @return 匹配的成员列表
     */
    public List<AccountVO> getAccountsByUsernameAndPosition(Long oid, String username, String position) {
        return registrationRepository.findByOrganizationIdAndStateAndAccountUsernameContainingAndPositionContaining(oid, 1, username, position).stream()
                .map(this::toAccountVO)
                .toList();
    }

    /**
     * 将 Registration 转换为 AccountVO
     */
    private AccountVO toAccountVO(Registration reg) {
        Account account = reg.getAccount();
        AccountVO vo = account.asViewObject(AccountVO.class);
        vo.setPosition(reg.getPosition());  // position 从 Registration 获取
        return vo;
    }

    /**
     * 应用启动后初始化超级管理员账户（Order=2 确保在角色初始化之后执行）
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void initAccount() {
        if (accountRepository.count() == 0) {
            createAccount("superadmin", "123456@qq.com", "13888888888",
                    LocalDate.of(1900, 1, 1), "https://avatars.githubusercontent.com/u/136216354?s=96&v=4", "superadmin");
            createAccount("JackieLinn", "23456@qq.com", "13111111111",
                    LocalDate.of(2004, 1, 1), "https://avatars.githubusercontent.com/u/136216354?s=96&v=4", "admin");
            createAccount("KrowFeather", "345678@qq.com", "13222222222",
                    LocalDate.of(2004, 2, 2), "https://avatars.githubusercontent.com/u/38802245?v=4", "admin");
            createAccount("01-Dreamer", "456789@qq.com", "13333333333",
                    LocalDate.of(2005, 1, 1), "https://avatars.githubusercontent.com/u/148927117?v=4", "admin");
            createAccount("test1", "1234567@qq.com", "13444444444",
                    LocalDate.of(1995, 1, 1), "https://avatars.githubusercontent.com/u/181219839?v=4", "user");
        }
    }

    private void createAccount(String username, String email, String phone,
                               LocalDate birthday, String avatar, String roleName) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(encoder.encode("123456"));
        account.setEmail(email);
        account.setSex(0);
        account.setPhone(phone);
        account.setAddress("China");
        account.setBirthday(birthday);
        account.setAvatar(avatar);
        roleRepository.findByRolename(roleName).ifPresent(role -> account.getRoles().add(role));
        accountRepository.save(account);
    }

    /**
     * 根据用户名或邮箱查询账户
     *
     * @param text 用户名或邮箱
     * @return 账户信息，未找到返回null
     */
    public Account findAccountByUsernameOrEmail(String text) {
        return accountRepository.findByUsernameOrEmailWithRoles(text).orElse(null);
    }

    /**
     * 发送邮箱验证码，用于注册或重置密码
     *
     * @param type  验证码类型（register/reset）
     * @param email 目标邮箱地址
     * @param ip    请求的IP地址，用于限流
     * @return 操作结果，null表示发送成功，否则为错误原因
     */
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if (!this.verifyLimit(ip)) {
                return "请求频繁，请稍后再试";
            }
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("PIMSMail", data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 邮件验证码注册账号操作，需要检查验证码是否正确以及邮箱、用户名是否存在重名
     *
     * @param ro 注册基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String registerEmailAccount(EmailRegisterRO ro) {
        String email = ro.getEmail();
        String username = ro.getUsername();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) return "请先获取验证码";
        if (!code.equals(ro.getCode())) return "验证码错误，请重新输入";
        if (this.existsAccountByEmail(email)) return "此电子邮件已被其他用户注册";
        if (this.existsAccountByUsername(username)) return "此用户名已被其他人注册，请更换一个新的用户名";
        // 反射复制同名字段，再设置默认值和加密密码
        Account account = ro.asViewObject(Account.class, acc -> {
            acc.setPassword(encoder.encode(ro.getPassword()));
            acc.setSex(0);
            acc.setBirthday(LocalDate.of(1900, 1, 1));
            acc.setAvatar("https://avatars.githubusercontent.com/u/136216354?s=400&u=e6ec94f374b9c070a0119045c89c88df141edcf1&v=4");
        });
        // 设置默认角色为普通用户
        roleRepository.findByRolename("user").ifPresent(role -> account.getRoles().add(role));
        accountRepository.save(account);
        stringRedisTemplate.delete(key);
        return null;
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     *
     * @param ro 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String resetEmailAccountPassword(EmailResetRO ro) {
        String verify = resetConfirm(new ConfirmResetRO(ro.getEmail(), ro.getCode()));
        if (verify != null) return verify;
        String email = ro.getEmail();
        String password = encoder.encode(ro.getPassword());
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return "更新失败，请联系管理员";
        }
        Account account = accountOpt.get();
        account.setPassword(password);
        accountRepository.save(account);
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
        return null;
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     *
     * @param ro 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    public String resetConfirm(ConfirmResetRO ro) {
        String email = ro.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) return "请先获取验证码";
        if (!code.equals(ro.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     *
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsernameOrEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户名或密码错误"));
        String[] roles = account.getRoles().stream()
                .map(Role::getRolename)
                .toArray(String[]::new);
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(roles)
                .build();
    }

    /**
     * 验证邮件发送频率限制，60秒内只能发送一次
     *
     * @param ip 请求的IP地址
     * @return true-允许发送，false-请求过于频繁
     */
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, 60);
    }

    /**
     * 查询指定邮箱的用户是否已经存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    private boolean existsAccountByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    /**
     * 查询指定用户名的用户是否已经存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    private boolean existsAccountByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }
}
