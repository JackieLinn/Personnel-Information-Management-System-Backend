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
public class TeamMemberVO {
    private User user;
    private String position;
    private LocalDateTime joinTime;
}
