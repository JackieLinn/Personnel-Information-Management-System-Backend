package ynu.edu.pims.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password_hash")
    private String passwordHash;

    @TableField(value = "email")
    private String email;

    @TableField(value = "name")
    private String name;

    @TableField(value = "avatar")
    private String avatar;

    @TableField(value = "birthday")
    private LocalDate birthday;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "gender")
    private Integer gender; // 0 女 1 男

    @TableField(value = "address")
    private String address;

    @TableField(value = "admin")
    private Integer admin;
}
