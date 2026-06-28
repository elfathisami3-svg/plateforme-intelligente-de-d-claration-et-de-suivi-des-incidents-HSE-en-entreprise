package hse.repository;

import hse.entity.ServiceDepartement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceDepartementRepository extends JpaRepository<ServiceDepartement, Long> {
    List<ServiceDepartement> findBySiteId(Long siteId);
}

