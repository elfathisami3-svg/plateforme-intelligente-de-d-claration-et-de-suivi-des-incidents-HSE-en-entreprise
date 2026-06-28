package hse.repository;

import hse.entity.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    List<Commentaire> findByIncidentIdOrderByDateCommentaireDesc(Long incidentId);
}

