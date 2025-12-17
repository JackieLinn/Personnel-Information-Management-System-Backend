package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ynu.edu.pims.entity.Role;
import ynu.edu.pims.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {

    @Resource
    private RoleRepository roleRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void initRoles() {
        if (roleRepository.count() == 0) {
            Role superAdmin = new Role();
            superAdmin.setRolename("superadmin");

            Role admin = new Role();
            admin.setRolename("admin");

            Role user = new Role();
            user.setRolename("user");

            roleRepository.saveAll(List.of(superAdmin, admin, user));
        }
    }
}
