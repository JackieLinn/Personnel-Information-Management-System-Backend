package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.bcnlserver.mapper.TweetMapper;
import zxylearn.bcnlserver.pojo.entity.Tweet;
import zxylearn.bcnlserver.service.TweetService;

@Service
public class TweetServiceImpl extends ServiceImpl<TweetMapper, Tweet> implements TweetService {

}
