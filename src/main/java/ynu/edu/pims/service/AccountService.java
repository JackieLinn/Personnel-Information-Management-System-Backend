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
import ynu.edu.pims.dto.request.ConfirmResetRO;
import ynu.edu.pims.dto.request.EmailRegisterRO;
import ynu.edu.pims.dto.request.EmailResetRO;
import ynu.edu.pims.entity.Account;
import ynu.edu.pims.entity.Role;
import ynu.edu.pims.repository.AccountRepository;
import ynu.edu.pims.repository.RoleRepository;
import ynu.edu.pims.utils.Const;
import ynu.edu.pims.utils.FlowUtils;

import java.time.LocalDate;
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

    /**
     * 应用启动后初始化超级管理员账户（Order=2 确保在角色初始化之后执行）
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void initAccount() {
        if (accountRepository.count() == 0) {
            Account superAdmin = new Account();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(encoder.encode("123456"));
            superAdmin.setEmail("123456@qq.com");
            superAdmin.setSex(0);
            superAdmin.setPhone("13888888888");
            superAdmin.setPosition("admin");
            superAdmin.setAddress("中国大陆");
            superAdmin.setBirthday(LocalDate.of(1900, 1, 1));
            superAdmin.setAvatar("https://avatars.githubusercontent.com/u/136216354?s=400&u=e6ec94f374b9c070a0119045c89c88df141edcf1&v=4");
            // 关联 superadmin 角色
            roleRepository.findByRolename("superadmin").ifPresent(role -> superAdmin.getRoles().add(role));
            accountRepository.save(superAdmin);
        }
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
