package ynu.edu.pims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account implements BaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false, name = "sex")
    private Integer sex;    // 0: 男; 1: 女

    @Column(nullable = false, name = "birthday")
    private LocalDate birthday;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "phone")
    private String phone;

    @Column(name = "position")
    private String position;

    @Column(name = "address")
    private String address;

    @Column(nullable = false, name = "avatar")
    private String avatar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "aid"),
            inverseJoinColumns = @JoinColumn(name = "rid")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();
}
