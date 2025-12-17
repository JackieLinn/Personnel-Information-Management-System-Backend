package ynu.edu.pims.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "organization")
public class Organization implements BaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "description")
    private String description;

    @Column(nullable = false, name = "logo")
    private String logo;

    @Column(nullable = false, name = "banner")
    private String banner;

    @Column(nullable = false, name = "reason")
    private String reason;

    @Column(nullable = false, name = "state")
    private Integer state;  // 0: 待审核; 1: 通过; 2: 拒绝

    // 组织创建者（一个组织只有一个创建者）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aid", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;

    // 组织成员（通过 registration 表关联）
    @ManyToMany(mappedBy = "organizations")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Account> members = new HashSet<>();
}
