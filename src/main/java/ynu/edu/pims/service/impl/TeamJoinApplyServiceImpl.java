package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import ynu.edu.pims.mapper.TeamJoinApplyMapper;
import ynu.edu.pims.pojo.DTO.TeamJoinApplyVO;
import ynu.edu.pims.pojo.entity.TeamJoinApply;
import ynu.edu.pims.service.TeamJoinApplyService;

import java.util.List;

@Service
public class TeamJoinApplyServiceImpl extends ServiceImpl<TeamJoinApplyMapper, TeamJoinApply> implements TeamJoinApplyService {

    @Override
    public TeamJoinApply getTeamJoinApply(Long teamId, Long applicantId) {
        if (teamId == null || applicantId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<TeamJoinApply>()
                .eq(TeamJoinApply::getTeamId, teamId)
                .eq(TeamJoinApply::getApplicantId, applicantId)
                .eq(TeamJoinApply::getStatus, 0)
        );
    }

    @Override
    public List<TeamJoinApplyVO> getTeamJoinApplyList(Long teamId) {
        return baseMapper.selectTeamJoinApplies(teamId);
    }
}
