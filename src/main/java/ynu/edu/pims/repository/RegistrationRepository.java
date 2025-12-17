package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Registration;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    boolean existsByAccountIdAndOrganizationIdAndState(Long aid, Long oid, Integer state);
    
    Optional<Registration> findByAccountIdAndOrganizationId(Long aid, Long oid);
}

