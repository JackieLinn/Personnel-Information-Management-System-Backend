package ynu.edu.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ynu.edu.pims.entity.Images;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {
}
