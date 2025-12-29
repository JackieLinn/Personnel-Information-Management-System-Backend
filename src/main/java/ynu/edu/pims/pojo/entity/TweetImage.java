package ynu.edu.pims.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tweet_image")
public class TweetImage {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "tweet_id")
    private Long tweetId;

    @TableField(value = "image")
    private String image;
}
