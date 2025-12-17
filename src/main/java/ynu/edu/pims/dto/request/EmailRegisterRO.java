package ynu.edu.pims.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ynu.edu.pims.entity.BaseData;

@Data
public class EmailRegisterRO implements BaseData {
    @Email
    String email;
    @Length(min = 11, max = 11)
    String phone;
    @Length(min = 6, max = 6)
    String code;
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 30)
    String username;
    @Length(min = 6, max = 20)
    String password;
}
