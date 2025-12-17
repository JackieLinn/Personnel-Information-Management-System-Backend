package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.pims.dto.request.AccountRO;
import ynu.edu.pims.dto.request.AccountResetRO;
import ynu.edu.pims.dto.request.OutRO;
import ynu.edu.pims.dto.request.PositionResetRO;
import ynu.edu.pims.entity.RestBean;
import ynu.edu.pims.service.AccountService;

@RestController
@RequestMapping("/api/account")
@Tag(name = "用户操作相关接口", description = "用于用户操作相关接口")
public class AccountController {

    @Resource
    AccountService accountService;

    @Operation(summary = "用户修改个人信息", description = "用户修改个人信息")
    @PostMapping("/modify-information")
    public RestBean<String> modifyAccountInformation(AccountResetRO ro) {
        return RestBean.success(accountService.modifyAccountInformation(ro));
    }

    @Operation(summary = "老板修改用户职位信息", description = "老板修改用户职位信息")
    @PostMapping("/admin/modify-position")
    public RestBean<String> modifyAccountPosition(PositionResetRO ro) {
        return RestBean.success(accountService.modifyAccountPosition(ro));
    }

    @Operation(summary = "老板注册并添加用户进组织", description = "老板注册并添加用户进组织")
    @PostMapping("/admin/add-account-in-organization")
    public RestBean<String> addAccountInOrganization(AccountRO ro) {
        return RestBean.success(accountService.addAccountInOrganization(ro));
    }

    @Operation(summary = "老板将用户移出组织", description = "老板将用户移出组织")
    @PostMapping("/admin/remove-account-out-organization")
    public RestBean<String> addAccountOutOrganization(OutRO ro) {
        return RestBean.success(accountService.addAccountOutOrganization(ro));
    }
}
