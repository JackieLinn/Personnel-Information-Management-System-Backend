package ynu.edu.pims.pojo.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ynu.edu.pims.pojo.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamJoinApplyVO {
    private Long id;
    private User user;
    private LocalDateTime applyTime;
    private Integer status; // 0 待审核 1 已通过 2 已拒绝
}
