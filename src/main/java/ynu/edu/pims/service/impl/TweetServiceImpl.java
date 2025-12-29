package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import ynu.edu.pims.mapper.TweetMapper;
import ynu.edu.pims.pojo.entity.Tweet;
import ynu.edu.pims.service.TweetService;

@Service
public class TweetServiceImpl extends ServiceImpl<TweetMapper, Tweet> implements TweetService {

}
