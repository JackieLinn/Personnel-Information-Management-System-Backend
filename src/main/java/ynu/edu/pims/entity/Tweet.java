package ynu.edu.pims.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tweet")
public class Tweet implements BaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "title")
    private String title;

    @Lob    // 长文本
    @Column(nullable = false, name = "content")
    private String content;
}
