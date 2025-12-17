package ynu.edu.pims.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ynu.edu.pims.entity.BaseData;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AccountResetRO implements BaseData {
    Long id;
    String username;
    Integer sex;
    LocalDate birthday;
    String phone;
    String address;
    String avatar;
}
