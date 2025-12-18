package ynu.edu.pims.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountVO {
    Long id;
    String username;
    Integer sex;
    LocalDate birthday;
    String email;
    String phone;
    String address;
    String avatar;
    String position;
}
