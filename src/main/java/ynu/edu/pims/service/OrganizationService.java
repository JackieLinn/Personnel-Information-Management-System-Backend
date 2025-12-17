package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ynu.edu.pims.entity.Account;
import ynu.edu.pims.entity.Organization;
import ynu.edu.pims.repository.AccountRepository;
import ynu.edu.pims.repository.OrganizationRepository;

@Service
@DependsOn("accountService")
public class OrganizationService {

    @Resource
    private OrganizationRepository organizationRepository;

    @Resource
    private AccountRepository accountRepository;

    /**
     * 应用启动后初始化组织（Order=3 确保在账户初始化之后执行）
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(3)
    @Transactional
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

        Organization org = new Organization();
        org.setName(name);
        org.setDescription(description);
        org.setLogo(logo);
        org.setBanner(banner);
        org.setState(1);
        org.setReason("init");
        org.setAccount(admin);
        organizationRepository.save(org);

        // 创建者加入组织
        admin.getOrganizations().add(org);
        accountRepository.save(admin);
    }
}
