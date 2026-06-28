package hse.repository;

import hse.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    List<HistoriqueAction> findByIncidentIdOrderByDateActionDesc(Long incidentId);
    List<HistoriqueAction> findTop50ByOrderByDateActionDesc();
}

