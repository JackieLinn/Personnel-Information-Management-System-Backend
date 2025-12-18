package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
