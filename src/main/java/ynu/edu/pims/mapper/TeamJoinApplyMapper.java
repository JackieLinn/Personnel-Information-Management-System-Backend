package ynu.edu.pims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import ynu.edu.pims.pojo.DTO.TeamJoinApplyVO;
import ynu.edu.pims.pojo.entity.TeamJoinApply;

import java.util.List;

@Mapper
public interface TeamJoinApplyMapper extends BaseMapper<TeamJoinApply> {
    List<TeamJoinApplyVO> selectTeamJoinApplies(@Param("teamId") Long teamId);
}
