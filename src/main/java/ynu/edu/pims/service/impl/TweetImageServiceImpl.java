package ynu.edu.pims.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import ynu.edu.pims.mapper.TweetImageMapper;
import ynu.edu.pims.pojo.entity.TweetImage;
import ynu.edu.pims.service.TweetImageService;

@Service
public class TweetImageServiceImpl extends ServiceImpl<TweetImageMapper, TweetImage> implements TweetImageService {

}
