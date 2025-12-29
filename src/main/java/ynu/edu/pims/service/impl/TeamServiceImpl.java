package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.bcnlserver.mapper.TeamMapper;
import zxylearn.bcnlserver.pojo.entity.Team;
import zxylearn.bcnlserver.service.TeamService;

import java.util.List;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Override
    public List<Team> getTeamListByStatus(Integer status) {
        return lambdaQuery()
                .eq(status != null, Team::getStatus, status)
                .list();
    }
}
