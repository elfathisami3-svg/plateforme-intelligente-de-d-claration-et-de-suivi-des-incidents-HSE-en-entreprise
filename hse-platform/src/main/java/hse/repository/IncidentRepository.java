package hse.repository;

import hse.entity.Incident;
import hse.model.Gravite;
import hse.model.StatutIncident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    long countByStatut(StatutIncident statut);
    long countByGravite(Gravite gravite);
    List<Incident> findByDeclarantId(Long declarantId);
    List<Incident> findByResponsableTraitementId(Long responsableId);
    List<Incident> findBySiteId(Long siteId);
    List<Incident> findByServiceId(Long serviceId);
    List<Incident> findByCategorieId(Long categorieId);
}

