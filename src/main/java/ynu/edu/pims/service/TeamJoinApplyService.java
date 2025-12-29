package ynu.edu.pims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.pims.pojo.DTO.TeamJoinApplyVO;
import ynu.edu.pims.pojo.entity.TeamJoinApply;

import java.util.List;

public interface TeamJoinApplyService extends IService<TeamJoinApply> {
    public TeamJoinApply getTeamJoinApply(Long teamId, Long applicantId);
    public List<TeamJoinApplyVO> getTeamJoinApplyList(Long teamId);
}
