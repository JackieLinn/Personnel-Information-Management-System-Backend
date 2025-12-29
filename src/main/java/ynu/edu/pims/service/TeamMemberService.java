package ynu.edu.pims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.pims.pojo.entity.TeamMember;

import java.util.List;

public interface TeamMemberService extends IService<TeamMember> {
    public TeamMember getTeamMember(Long teamId, Long memberId);
    public List<TeamMemberVO> getTeamMemberList(Long teamId);
    public List<Long> getTeamIdsByMemberId(Long memberId);
}
