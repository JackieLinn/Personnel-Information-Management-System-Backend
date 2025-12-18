package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ynu.edu.pims.repository.TweetRepository;

@RestController
@RequestMapping("/api/tweet")
@Tag(name = "推文相关接口", description = "用于推文操作相关接口")
public class TweetController {

    @Resource
    private TweetRepository tweetRepository;
}
