package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zxylearn.bcnlserver.mapper.TweetImageMapper;
import zxylearn.bcnlserver.pojo.entity.TweetImage;
import zxylearn.bcnlserver.service.TweetImageService;

@Service
public class TweetImageServiceImpl extends ServiceImpl<TweetImageMapper, TweetImage> implements TweetImageService {

}
