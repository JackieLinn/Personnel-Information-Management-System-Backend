package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Organization;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    boolean existsByIdAndAccountId(Long oid, Long aid);
    
    Optional<Organization> findByAccountId(Long aid);
}
