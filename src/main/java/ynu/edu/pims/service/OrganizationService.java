package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ynu.edu.pims.dto.request.ApplyRO;
import ynu.edu.pims.dto.request.CreateRO;
import ynu.edu.pims.dto.request.ReplyRO;
import ynu.edu.pims.entity.Account;
import ynu.edu.pims.entity.Organization;
import ynu.edu.pims.entity.Registration;
import ynu.edu.pims.entity.Role;
import ynu.edu.pims.repository.AccountRepository;
import ynu.edu.pims.repository.OrganizationRepository;
import ynu.edu.pims.repository.RegistrationRepository;
import ynu.edu.pims.repository.RoleRepository;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@DependsOn("accountService")
public class OrganizationService {

    @Resource
    private OrganizationRepository organizationRepository;

    @Resource
    private AccountRepository accountRepository;

    @Resource
    private RegistrationRepository registrationRepository;

    @Resource
    private RoleRepository roleRepository;

    /**
     * 用户申请创建组织
     *
     * @param ro 包含组织信息和申请者id的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String applyToCreateOrganization(CreateRO ro) {
        // 检查用户是否存在
        Account account = accountRepository.findById(ro.getAid()).orElse(null);
        if (account == null) {
            return "用户不存在";
        }
        // 创建组织申请记录（反射复制同名字段：name, description, logo, banner, reason）
        Organization org = ro.asViewObject(Organization.class, o -> {
            o.setState(0);  // 待审核
            o.setAccount(account);
        });
        organizationRepository.save(org);
        return null;
    }

    /**
     * 超级管理员同意创建组织申请
     *
     * @param oid 组织id
     * @return 操作结果，null表示成功，否则为失败原因
     */
    @Transactional
    public String agreeToCreateOrganization(Long oid) {
        // 1. 查询组织并更新状态
        Organization org = organizationRepository.findById(oid).orElse(null);
        if (org == null) {
            return "组织不存在";
        }
        if (org.getState() != 0) {
            return "该申请已被处理";
        }
        org.setState(1);  // 通过
        organizationRepository.save(org);

        // 2. 获取创建者并添加 Registration 记录
        Account account = org.getAccount();
        Registration registration = new Registration();
        registration.setAccount(account);
        registration.setOrganization(org);
        registration.setState(1);  // 已通过
        registration.setPosition("boss");
        registrationRepository.save(registration);

        // 3. 修改用户角色：从 user 改为 admin
        Role userRole = roleRepository.findByRolename("user").orElse(null);
        Role adminRole = roleRepository.findByRolename("admin").orElse(null);
        if (userRole != null && adminRole != null) {
            account.getRoles().remove(userRole);
            account.getRoles().add(adminRole);
            accountRepository.save(account);
        }
        return null;
    }

    /**
     * 超级管理员拒绝创建组织申请
     *
     * @param oid 组织id
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String rejectToCreateOrganization(Long oid) {
        Organization org = organizationRepository.findById(oid).orElse(null);
        if (org == null) {
            return "组织不存在";
        }
        if (org.getState() != 0) {
            return "该申请已被处理";
        }
        org.setState(2);  // 拒绝
        organizationRepository.save(org);
        return null;
    }

    /**
     * 老板同意用户加入组织
     *
     * @param ro 包含老板id和申请记录id的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String agreeToJoinOrganization(ReplyRO ro) {
        return handleJoinApplication(ro, 1);
    }

    /**
     * 老板拒绝用户加入组织
     *
     * @param ro 包含老板id和申请记录id的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String rejectToJoinOrganization(ReplyRO ro) {
        return handleJoinApplication(ro, 2);
    }

    /**
     * 处理加入组织的申请
     *
     * @param ro    请求对象
     * @param state 目标状态（1=同意，2=拒绝）
     * @return 操作结果
     */
    private String handleJoinApplication(ReplyRO ro, Integer state) {
        // 查询申请记录
        Registration registration = registrationRepository.findById(ro.getRegId()).orElse(null);
        if (registration == null) {
            return "申请记录不存在";
        }
        // 检查申请是否为待审核状态
        if (registration.getState() != 0) {
            return "该申请已被处理";
        }
        // 验证操作者是否是该组织的创建者（老板）
        Long oid = registration.getOrganization().getId();
        if (!organizationRepository.existsByIdAndAccountId(oid, ro.getBossId())) {
            return "您不是该组织的管理员，无权审核";
        }
        // 更新状态
        registration.setState(state);
        registrationRepository.save(registration);
        return null;
    }

    /**
     * 用户申请加入组织
     *
     * @param ro 包含用户id和组织id的请求对象
     * @return 操作结果，null表示成功，否则为失败原因
     */
    public String applyToJoinOrganization(ApplyRO ro) {
        // 检查组织是否存在
        if (!organizationRepository.existsById(ro.getOid())) {
            return "组织不存在";
        }
        // 检查是否已经有申请记录
        if (registrationRepository.existsByAccountIdAndOrganizationIdAndStateIn(ro.getAid(), ro.getOid(), List.of(0, 1))) {
            return "您已申请或已在该组织中";
        }
        // 创建申请记录
        Account account = accountRepository.findById(ro.getAid()).orElse(null);
        Organization org = organizationRepository.findById(ro.getOid()).orElse(null);
        if (account == null || org == null) {
            return "用户或组织不存在";
        }
        Registration registration = ro.asViewObject(Registration.class, reg -> {
            reg.setAccount(account);
            reg.setOrganization(org);
            reg.setState(0);  // 待审核
            reg.setPosition("pending");  // 待定职位
        });
        registrationRepository.save(registration);
        return null;
    }

    /**
     * 根据用户id查询其创建的组织id（即判断是否为boss）
     *
     * @param aid 用户id
     * @return 如果用户角色是admin且是某个组织的创建者（boss），返回组织id；否则返回-1
     */
    public Long getOrganizationId(Long aid) {
        // 先检查用户角色是否为 admin
        Account account = accountRepository.findByIdWithRoles(aid).orElse(null);
        if (account == null) {
            return -1L;
        }
        boolean isAdmin = account.getRoles().stream()
                .map(Role::getRolename)
                .anyMatch("admin"::equals);
        if (!isAdmin) {
            return -1L;
        }
        // 是 admin 则查找其创建的组织
        return organizationRepository.findByAccountId(aid)
                .map(Organization::getId)
                .orElse(-1L);
    }

    /**
     * 应用启动后初始化组织（Order=3 确保在账户初始化之后执行）
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(3)
    public void initOrganization() {
        if (organizationRepository.count() == 0) {
            createOrganization(2L, "Apple ML", "Apple ML is a machine learning company.",
                    "https://th.bing.com/th/id/OIP.cX1fKk3vsARCeW2en8W8_QHaE8?w=232&h=180&c=7&r=0&o=7&cb=ucfimg2&dpr=1.3&pid=1.7&rm=3&ucfimg=1",
                    "https://photos5.appleinsider.com/gallery/33813-59969-000-lead-ML-xl.jpg");
            createOrganization(3L, "Google Research", "Google Research is a artificial intelligence company.",
                    "https://www.blog.google/static/blogv2/images/google-200x200.png",
                    "https://research.google/static/images/blog/google-ai-meta.png");
            createOrganization(4L, "OpenAI", "OpenAI is a artificial intelligence research laboratory.",
                    "https://logos-world.net/wp-content/uploads/2024/08/OpenAI-Logo.png",
                    "https://img.lancdn.com/landian/2024/09/105878.png");
        }
    }

    private void createOrganization(Long adminId, String name, String description, String logo, String banner) {
        Account admin = accountRepository.findById(adminId).orElse(null);
        if (admin == null) return;

        // 创建组织
        Organization org = new Organization();
        org.setName(name);
        org.setDescription(description);
        org.setLogo(logo);
        org.setBanner(banner);
        org.setState(1);
        org.setReason("init");
        org.setAccount(admin);
        organizationRepository.save(org);

        // 创建者加入组织（通过 Registration 表）
        Registration registration = new Registration();
        registration.setAccount(admin);
        registration.setOrganization(org);
        registration.setState(1);  // 创建者直接通过
        registration.setPosition("boss");  // 创建者默认职位为 boss
        registrationRepository.save(registration);
    }
}
