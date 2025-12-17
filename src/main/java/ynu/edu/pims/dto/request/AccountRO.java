package ynu.edu.pims.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ynu.edu.pims.entity.BaseData;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AccountRO implements BaseData {
    Long oid;        // 组织 id
    Long bossId;     // 老板 id
    String username;
    Integer sex;
    LocalDate birthday;
    String email;
    String phone;
    String position;
}
