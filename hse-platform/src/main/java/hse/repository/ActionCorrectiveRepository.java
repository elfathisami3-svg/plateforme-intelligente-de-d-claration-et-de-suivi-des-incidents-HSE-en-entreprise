package hse.repository;

import hse.entity.ActionCorrective;
import hse.model.EtatAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActionCorrectiveRepository extends JpaRepository<ActionCorrective, Long> {
    List<ActionCorrective> findByIncidentId(Long incidentId);
    List<ActionCorrective> findByResponsableId(Long responsableId);
    long countByEtat(EtatAction etat);
    List<ActionCorrective> findByEtatNotAndDateEcheanceBefore(EtatAction etat, LocalDate date);
}

