package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zxylearn.bcnlserver.common.UserContext;
import zxylearn.bcnlserver.pojo.DTO.UserSearchRequestDTO;
import zxylearn.bcnlserver.pojo.DTO.UserUpdateRequestDTO;
import zxylearn.bcnlserver.pojo.entity.User;
import zxylearn.bcnlserver.service.UserService;
import zxylearn.bcnlserver.utils.JwtUtil;

import java.util.Map;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "更新用户信息")
    @PutMapping("update-info")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {

        Long userId = Long.parseLong(UserContext.getUserId());
        String role = UserContext.getUserRole();

        if(role.equals(JwtUtil.USER) && !userId.equals(userUpdateRequestDTO.getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "无权限操作"));
        }

        // 获取用户
        User user = userService.getById(userId);
        if(user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "用户不存在"));
        }

        // 验证信息合法性
        if(userUpdateRequestDTO.getGender() < 0 || userUpdateRequestDTO.getGender() > 2) {
            return ResponseEntity.badRequest().body(Map.of("error", "性别信息不合法"));
        }

        // 更新用户信息
        user.setName(userUpdateRequestDTO.getName() != null && !userUpdateRequestDTO.getName().isEmpty() ? userUpdateRequestDTO.getName() : user.getName());
        user.setAvatar(userUpdateRequestDTO.getAvatar() != null && !userUpdateRequestDTO.getAvatar().isEmpty() ? userUpdateRequestDTO.getAvatar() : user.getAvatar());
        user.setBirthday(userUpdateRequestDTO.getBirthday() != null ? userUpdateRequestDTO.getBirthday() : user.getBirthday());
        user.setPhone(userUpdateRequestDTO.getPhone() != null && !userUpdateRequestDTO.getPhone().isEmpty() ? userUpdateRequestDTO.getPhone() : user.getPhone());
        user.setGender(userUpdateRequestDTO.getGender() != null ? userUpdateRequestDTO.getGender() : user.getGender());
        user.setAddress(userUpdateRequestDTO.getAddress() != null && !userUpdateRequestDTO.getAddress().isEmpty() ? userUpdateRequestDTO.getAddress() : user.getAddress());
        if(!userService.updateById(user)) {
            return ResponseEntity.status(500).body(Map.of("error", "更新用户信息失败"));
        }

        return ResponseEntity.ok(Map.of("user", user));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("get-info")
    public ResponseEntity<?> getUserInfo(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if(user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "用户不存在"));
        }
        return ResponseEntity.ok(Map.of("user", user));
    }

    @Operation(summary = "查询用户")
    @PostMapping("/search")
    public ResponseEntity<?> searchUser(@RequestBody @Valid UserSearchRequestDTO userSearchRequestDTO) {
        return ResponseEntity.ok(Map.of("userList", userService.searchUserList(userSearchRequestDTO)));
    }
}
