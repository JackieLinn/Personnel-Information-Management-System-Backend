package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.pims.entity.RestBean;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试相关接口")
public class TestController {

    @GetMapping("/superadmin/test1")
    public RestBean<String> test1() {
        return RestBean.success("test1 success");
    }
}
