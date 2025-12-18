package ynu.edu.pims.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ynu.edu.pims.entity.BaseData;

@Data
@AllArgsConstructor
public class ApplyRO implements BaseData {
    Long oid;
    Long aid;
}
