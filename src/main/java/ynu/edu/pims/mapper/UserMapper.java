package ynu.edu.pims.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import ynu.edu.pims.pojo.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
