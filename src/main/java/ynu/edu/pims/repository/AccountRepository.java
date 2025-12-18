package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    Optional<Account> findByEmail(String email);
    
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.username = :text OR a.email = :text")
    Optional<Account> findByUsernameOrEmailWithRoles(@Param("text") String text);
    
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.id = :id")
    Optional<Account> findByIdWithRoles(@Param("id") Long id);
    
    boolean existsByPhoneAndIdNot(String phone, Long id);
    
    boolean existsByPhone(String phone);
}
