package ynu.edu.pims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ynu.edu.pims.pojo.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    public boolean isExistUsername(String username);
    public boolean isExistEmail(String email);
    public User getUserByUsernameOrEmail(String username);
    public List<User> searchUserList(UserSearchRequestDTO userSearchRequestDTO);
}