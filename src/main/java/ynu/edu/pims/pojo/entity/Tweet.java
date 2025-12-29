package ynu.edu.pims.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tweet")
public class Tweet {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "team_id")
    private Long teamId;

    @TableField(value = "sender_id")
    private Long senderId;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "title")
    private String title;

    @TableField(value = "content")
    private String content;
}
