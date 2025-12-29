package ynu.edu.pims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import ynu.edu.pims.pojo.DTO.TeamMemberVO;
import ynu.edu.pims.pojo.entity.TeamMember;

import java.util.List;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
    List<TeamMemberVO> selectTeamMembers(@Param("teamId") Long teamId);
}
