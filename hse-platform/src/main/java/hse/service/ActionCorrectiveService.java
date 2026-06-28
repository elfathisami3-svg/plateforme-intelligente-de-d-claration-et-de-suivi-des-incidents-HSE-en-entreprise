package hse.service;

import hse.entity.ActionCorrective;
import hse.entity.Utilisateur;
import hse.model.EtatAction;
import hse.model.Role;
import hse.repository.ActionCorrectiveRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActionCorrectiveService {
    private final ActionCorrectiveRepository actionRepository;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;

    public ActionCorrectiveService(ActionCorrectiveRepository actionRepository,
                                   HistoriqueService historiqueService,
                                   NotificationService notificationService) {
        this.actionRepository = actionRepository;
        this.historiqueService = historiqueService;
        this.notificationService = notificationService;
    }

    public List<ActionCorrective> findAll() {
        return actionRepository.findAll();
    }

    public List<ActionCorrective> findVisibleFor(Utilisateur utilisateur) {
        return switch (utilisateur.getRole()) {
            case TECHNICIEN -> actionRepository.findByResponsableId(utilisateur.getId());
            case ADMIN, RESPONSABLE_HSE, MANAGER -> actionRepository.findAll();
            case DECLARANT -> List.of();
        };
    }

    public List<ActionCorrective> findByIncident(Long incidentId) {
        return actionRepository.findByIncidentId(incidentId);
    }

    @Transactional
    public ActionCorrective save(ActionCorrective action, Utilisateur utilisateur) {
        if (utilisateur.getRole() == Role.DECLARANT || utilisateur.getRole() == Role.MANAGER) {
            throw new IllegalStateException("Role non autorise pour gerer les actions correctives.");
        }
        boolean nouvelle = action.getId() == null;
        if (nouvelle) {
            action.setDateCreation(LocalDateTime.now());
            if (action.getEtat() == null) {
                action.setEtat(EtatAction.A_FAIRE);
            }
        }
        ActionCorrective saved = actionRepository.save(action);
        if (saved.getIncident() != null) {
            historiqueService.trace(saved.getIncident(), utilisateur,
                    nouvelle ? "Creation action corrective" : "Modification action corrective",
                    "", saved.getDescription());
        }
        if (saved.getResponsable() != null) {
            notificationService.envoyer(saved.getResponsable(), saved.getIncident(), "ACTION", "Action corrective affectee.");
        }
        return saved;
    }

    @Transactional
    public ActionCorrective updateEtat(Long id, EtatAction etat, String resultat, Utilisateur utilisateur) {
        ActionCorrective action = actionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Action introuvable: " + id));
        if (utilisateur.getRole() == Role.TECHNICIEN &&
                (action.getResponsable() == null || !action.getResponsable().getId().equals(utilisateur.getId()))) {
            throw new IllegalStateException("Action non affectee a ce technicien.");
        }
        if (utilisateur.getRole() == Role.DECLARANT || utilisateur.getRole() == Role.MANAGER) {
            throw new IllegalStateException("Role non autorise pour modifier une action corrective.");
        }
        EtatAction ancienEtat = action.getEtat();
        action.setEtat(etat);
        action.setResultat(resultat);
        if (etat == EtatAction.TERMINEE) {
            action.setDateRealisation(LocalDate.now());
        }
        ActionCorrective saved = actionRepository.save(action);
        if (saved.getIncident() != null) {
            historiqueService.trace(saved.getIncident(), utilisateur, "Compte rendu action corrective",
                    ancienEtat != null ? ancienEtat.name() : "", etat.name() + " - " + (resultat != null ? resultat : ""));
            if (saved.getIncident().getResponsableHse() != null) {
                notificationService.envoyer(saved.getIncident().getResponsableHse(), saved.getIncident(),
                        "COMPTE_RENDU", "Compte rendu action corrective recu.");
            }
        }
        return saved;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void marquerRetards() {
        actionRepository.findByEtatNotAndDateEcheanceBefore(EtatAction.TERMINEE, LocalDate.now())
                .forEach(action -> action.setEtat(EtatAction.EN_RETARD));
    }
}

