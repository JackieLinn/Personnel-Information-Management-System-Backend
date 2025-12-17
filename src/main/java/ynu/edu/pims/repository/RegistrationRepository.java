package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}

