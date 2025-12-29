package ynu.edu.pims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.pims.pojo.entity.Team;

import java.util.List;

public interface TeamService extends IService<Team> {
    public List<Team> getTeamListByStatus(Integer status);
}
