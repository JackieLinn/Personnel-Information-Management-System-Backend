package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import ynu.edu.pims.dto.request.AccountRO;
import ynu.edu.pims.dto.request.AccountResetRO;
import ynu.edu.pims.dto.request.OutRO;
import ynu.edu.pims.dto.request.PositionResetRO;
import ynu.edu.pims.dto.response.AccountVO;
import ynu.edu.pims.entity.RestBean;
import ynu.edu.pims.service.AccountService;

import java.util.List;

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
    public RestBean<String> removeAccountOutOrganization(OutRO ro) {
        return RestBean.success(accountService.removeAccountOutOrganization(ro));
    }

    @Operation(summary = "老板查询组织所有用户", description = "老板查询组织所有用户")
    @GetMapping("/admin/get-all-accounts")
    public RestBean<List<AccountVO>> getAllAccounts(@RequestParam Long oid) {
        return RestBean.success(accountService.getAllAccounts(oid));
    }

    @Operation(summary = "老板根据名字查询用户", description = "老板根据名字查询用户")
    @GetMapping("/admin/get-accounts-by-username")
    public RestBean<List<AccountVO>> getAccountsByUsername(@RequestParam Long oid, @RequestParam String username) {
        return RestBean.success(accountService.getAccountsByUsername(oid, username));
    }

    @Operation(summary = "老板根据职位查询用户", description = "老板根据职位查询用户")
    @GetMapping("/admin/get-accounts-by-position")
    public RestBean<List<AccountVO>> getAccountsByPosition(@RequestParam Long oid, @RequestParam String position) {
        return RestBean.success(accountService.getAccountsByPosition(oid, position));
    }

    @Operation(summary = "老板根据名字和职位查询用户", description = "老板根据名字和职位查询用户")
    @GetMapping("/admin/get-accounts-by-username-and-position")
    public RestBean<List<AccountVO>> getAccountsByUsernameAndPosition(@RequestParam Long oid, @RequestParam String username, @RequestParam String position) {
        return RestBean.success(accountService.getAccountsByUsernameAndPosition(oid, username, position));
    }
}
