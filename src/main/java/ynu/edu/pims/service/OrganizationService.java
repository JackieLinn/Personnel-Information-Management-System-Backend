package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ynu.edu.pims.entity.Account;
import ynu.edu.pims.entity.Organization;
import ynu.edu.pims.entity.Registration;
import ynu.edu.pims.entity.Role;
import ynu.edu.pims.repository.AccountRepository;
import ynu.edu.pims.repository.OrganizationRepository;
import ynu.edu.pims.repository.RegistrationRepository;

@Service
@DependsOn("accountService")
public class OrganizationService {

    @Resource
    private OrganizationRepository organizationRepository;

    @Resource
    private AccountRepository accountRepository;

    @Resource
    private RegistrationRepository registrationRepository;

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
