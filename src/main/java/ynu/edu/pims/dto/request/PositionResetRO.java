package ynu.edu.pims.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ynu.edu.pims.entity.BaseData;

@Data
@AllArgsConstructor
public class PositionResetRO implements BaseData {
    Long oid;        // 组织 id
    Long bossId;     // 老板 id
    Long employeeId; // 被修改员工 id
    String position; // 新职位
}
