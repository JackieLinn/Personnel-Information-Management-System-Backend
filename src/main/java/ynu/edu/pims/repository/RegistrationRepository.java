package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Registration;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    Optional<Registration> findByAccountIdAndOrganizationId(Long aid, Long oid);
    
    Optional<Registration> findByAccountIdAndOrganizationIdAndState(Long aid, Long oid, Integer state);
    
    // 检查是否存在待审核或已通过的记录
    boolean existsByAccountIdAndOrganizationIdAndStateIn(Long aid, Long oid, List<Integer> states);
    
    // 查询组织内所有已通过的成员
    List<Registration> findByOrganizationIdAndState(Long oid, Integer state);
    
    // 按用户名模糊搜索
    List<Registration> findByOrganizationIdAndStateAndAccountUsernameContaining(Long oid, Integer state, String username);
    
    // 按职位模糊搜索
    List<Registration> findByOrganizationIdAndStateAndPositionContaining(Long oid, Integer state, String position);
    
    // 按用户名和职位模糊搜索
    List<Registration> findByOrganizationIdAndStateAndAccountUsernameContainingAndPositionContaining(
            Long oid, Integer state, String username, String position);
}
