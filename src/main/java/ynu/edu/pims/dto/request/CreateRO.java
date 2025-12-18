package ynu.edu.pims.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ynu.edu.pims.entity.BaseData;

@Data
@AllArgsConstructor
public class CreateRO implements BaseData {
    String name;
    String description;
    String logo;
    String banner;
    String reason;
    Long aid;
}
