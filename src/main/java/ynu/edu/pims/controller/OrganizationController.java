package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import ynu.edu.pims.dto.request.ReplyRO;
import ynu.edu.pims.dto.request.ApplyRO;
import ynu.edu.pims.entity.RestBean;
import ynu.edu.pims.service.OrganizationService;

@RestController
@RequestMapping("/api/organization")
@Tag(name = "社群组织相关接口", description = "用于社群组织操作相关接口")
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;

    @Operation(summary = "查询该admin用户的组织ID", description = "查询该admin用户的组织ID")
    @GetMapping("/get-organization-id")
    public RestBean<Long> getOrganizationId(@RequestParam Long aid) {
        return RestBean.success(organizationService.getOrganizationId(aid));
    }

    @Operation(summary = "根据组织ID查询组织信息", description = "根据组织ID查询组织信息")
    @GetMapping("/get-organization-information")
    public RestBean<Long> getOrganizationInformation(@RequestParam Long oid) {
        // TODO:
        return RestBean.success();
    }

    @Operation(summary = "用户申请加入组织", description = "用户申请加入组织")
    @PostMapping("/user/apply-to-joint-organization")
    public RestBean<String> applyToJoinOrganization(ApplyRO ro) {
        return RestBean.success(organizationService.applyToJoinOrganization(ro));
    }

    @Operation(summary = "同意用户加入组织", description = "同意用户加入组织")
    @PostMapping("/admin/agree-to-joint-organization")
    public RestBean<String> agreeToJoinOrganization(ReplyRO ro) {
        return RestBean.success(organizationService.agreeToJoinOrganization(ro));
    }

    @Operation(summary = "拒绝用户加入组织", description = "拒绝用户加入组织")
    @PostMapping("/admin/reject-to-joint-organization")
    public RestBean<String> rejectToJoinOrganization(ReplyRO ro) {
        return RestBean.success(organizationService.rejectToJoinOrganization(ro));
    }
}
