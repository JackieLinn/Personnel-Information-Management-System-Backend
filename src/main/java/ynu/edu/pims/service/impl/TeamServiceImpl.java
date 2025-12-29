package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import ynu.edu.pims.mapper.TeamMapper;
import ynu.edu.pims.pojo.entity.Team;
import ynu.edu.pims.service.TeamService;

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
