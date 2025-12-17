package ynu.edu.pims.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailResetRO {
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;
    @Length(min = 6, max = 20)
    String password;
}
