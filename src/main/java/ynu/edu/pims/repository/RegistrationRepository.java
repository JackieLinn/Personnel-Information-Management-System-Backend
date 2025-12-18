package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Registration;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    Optional<Registration> findByAccountIdAndOrganizationId(Long aid, Long oid);
    
    Optional<Registration> findByAccountIdAndOrganizationIdAndState(Long aid, Long oid, Integer state);
}

