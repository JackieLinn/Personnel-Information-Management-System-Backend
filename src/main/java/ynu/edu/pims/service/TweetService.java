package ynu.edu.pims.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import ynu.edu.pims.repository.TweetRepository;

@Service
public class TweetService {

    @Resource
    private TweetRepository tweetRepository;
}
