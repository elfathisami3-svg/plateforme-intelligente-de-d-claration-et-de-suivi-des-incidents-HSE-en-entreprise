package hse.service;

import hse.entity.Incident;
import hse.entity.Utilisateur;
import hse.model.Gravite;
import hse.model.Role;
import hse.model.StatutIncident;
import hse.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final IntelligenceService intelligenceService;
    private final HistoriqueService historiqueService;
    private final NotificationService notificationService;

    public IncidentService(IncidentRepository incidentRepository,
                           IntelligenceService intelligenceService,
                           HistoriqueService historiqueService,
                           NotificationService notificationService) {
        this.incidentRepository = incidentRepository;
        this.intelligenceService = intelligenceService;
        this.historiqueService = historiqueService;
        this.notificationService = notificationService;
    }

    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    public List<Incident> findVisibleFor(Utilisateur utilisateur) {
        return switch (utilisateur.getRole()) {
            case DECLARANT -> incidentRepository.findByDeclarantId(utilisateur.getId());
            case TECHNICIEN -> incidentRepository.findByResponsableTraitementId(utilisateur.getId());
            case ADMIN, RESPONSABLE_HSE, MANAGER -> incidentRepository.findAll();
        };
    }

    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident introuvable: " + id));
    }

    public Incident findVisibleById(Long id, Utilisateur utilisateur) {
        Incident incident = findById(id);
        if (!canAccess(incident, utilisateur)) {
            throw new IllegalStateException("Acces refuse a cet incident.");
        }
        return incident;
    }

    @Transactional
    public Incident declarer(Incident incident, Utilisateur declarant) {
        incident.setReference(nextReference());
        incident.setDeclarant(declarant);
        incident.setDateDeclaration(LocalDateTime.now());
        incident.setStatut(StatutIncident.DECLARE);
        incident.setResponsableHse(null);
        incident.setResponsableTraitement(null);
        prepareSmartFields(incident);
        if (incident.getDateEcheance() == null) {
            incident.setDateEcheance(LocalDate.now().plusDays(7));
        }

        Incident saved = incidentRepository.save(incident);
        historiqueService.trace(saved, declarant, "Declaration incident", "", saved.getReference());
        notificationService.notifierRole(Role.RESPONSABLE_HSE, saved, "DECLARATION", "Nouvel incident a qualifier: " + saved.getReference());
        if (saved.getGravite() == Gravite.CRITIQUE) {
            notificationService.notifierRole(Role.MANAGER, saved, "ALERTE_CRITIQUE", "Incident critique declare: " + saved.getTitre());
        }
        return saved;
    }

    @Transactional
    public Incident modifier(Long id, Incident form, Utilisateur utilisateur) {
        Incident incident = findVisibleById(id, utilisateur);
        if (incident.getStatut() == StatutIncident.CLOTURE && utilisateur.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Incident cloture en lecture seule.");
        }
        if (utilisateur.getRole() == Role.DECLARANT) {
            if (!incident.getDeclarant().getId().equals(utilisateur.getId()) || incident.getStatut() != StatutIncident.DECLARE) {
                throw new IllegalStateException("Le declarant peut modifier uniquement ses declarations non validees.");
            }
            incident.setTitre(form.getTitre());
            incident.setDescription(form.getDescription());
            incident.setDateIncident(form.getDateIncident());
            incident.setLieu(form.getLieu());
            incident.setTypeIncident(form.getTypeIncident());
            incident.setSite(form.getSite());
            incident.setService(form.getService());
            incident.setCategorie(form.getCategorie());
            historiqueService.trace(incident, utilisateur, "Complement declaration", "", form.getDescription());
            return incidentRepository.save(incident);
        }
        if (utilisateur.getRole() == Role.MANAGER) {
            throw new IllegalStateException("Le manager consulte les indicateurs sans modifier les incidents.");
        }

        String avant = incident.getStatut() + " / " + incident.getGravite() + " / " + incident.getNiveauRisque();
        incident.setTitre(form.getTitre());
        incident.setDescription(form.getDescription());
        incident.setDateIncident(form.getDateIncident());
        incident.setLieu(form.getLieu());
        incident.setTypeIncident(form.getTypeIncident());
        incident.setGravite(form.getGravite());
        incident.setCausePresumee(form.getCausePresumee());
        incident.setDateEcheance(form.getDateEcheance());
        incident.setSite(form.getSite());
        incident.setService(form.getService());
        incident.setCategorie(form.getCategorie());
        incident.setResponsableHse(form.getResponsableHse());
        incident.setResponsableTraitement(form.getResponsableTraitement());
        incident.setStatut(form.getStatut());
        prepareSmartFields(incident);

        Incident saved = incidentRepository.save(incident);
        historiqueService.trace(saved, utilisateur, "Modification incident", avant,
                saved.getStatut() + " / " + saved.getGravite() + " / " + saved.getNiveauRisque());
        if (saved.getResponsableTraitement() != null) {
            notificationService.envoyer(saved.getResponsableTraitement(), saved, "AFFECTATION", "Incident affecte: " + saved.getReference());
        }
        return saved;
    }

    @Transactional
    public Incident cloturer(Long id, Utilisateur utilisateur) {
        Incident incident = findById(id);
        if (utilisateur.getRole() != Role.RESPONSABLE_HSE && utilisateur.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Seul le responsable HSE peut valider la cloture.");
        }
        if (incident.getStatut() == StatutIncident.CLOTURE) {
            return incident;
        }
        String ancienStatut = incident.getStatut().name();
        incident.setStatut(StatutIncident.CLOTURE);
        historiqueService.trace(incident, utilisateur, "Cloture incident", ancienStatut, "CLOTURE");
        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident valider(Long id, Utilisateur utilisateur) {
        Incident incident = findById(id);
        if (utilisateur.getRole() != Role.RESPONSABLE_HSE && utilisateur.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Seul le responsable HSE peut valider une declaration.");
        }
        String ancienStatut = incident.getStatut().name();
        incident.setResponsableHse(utilisateur);
        incident.setStatut(StatutIncident.EN_ANALYSE);
        historiqueService.trace(incident, utilisateur, "Validation declaration", ancienStatut, "EN_ANALYSE");
        notificationService.envoyer(incident.getDeclarant(), incident, "VALIDATION", "Declaration validee: " + incident.getReference());
        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident prendreEnCharge(Long id, Utilisateur utilisateur) {
        Incident incident = findVisibleById(id, utilisateur);
        if (utilisateur.getRole() != Role.TECHNICIEN && utilisateur.getRole() != Role.RESPONSABLE_HSE && utilisateur.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Role non autorise pour la prise en charge.");
        }
        if (incident.getResponsableTraitement() == null && utilisateur.getRole() == Role.TECHNICIEN) {
            incident.setResponsableTraitement(utilisateur);
        }
        String ancienStatut = incident.getStatut().name();
        incident.setStatut(StatutIncident.EN_TRAITEMENT);
        historiqueService.trace(incident, utilisateur, "Prise en charge", ancienStatut, "EN_TRAITEMENT");
        if (incident.getResponsableHse() != null) {
            notificationService.envoyer(incident.getResponsableHse(), incident, "PRISE_EN_CHARGE", "Incident pris en charge: " + incident.getReference());
        }
        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident resoudre(Long id, Utilisateur utilisateur) {
        Incident incident = findVisibleById(id, utilisateur);
        if (utilisateur.getRole() != Role.TECHNICIEN && utilisateur.getRole() != Role.RESPONSABLE_HSE && utilisateur.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Role non autorise pour declarer la resolution.");
        }
        String ancienStatut = incident.getStatut().name();
        incident.setStatut(StatutIncident.RESOLU);
        historiqueService.trace(incident, utilisateur, "Resolution incident", ancienStatut, "RESOLU");
        if (incident.getResponsableHse() != null) {
            notificationService.envoyer(incident.getResponsableHse(), incident, "RESOLUTION", "Resolution a valider: " + incident.getReference());
        }
        if (incident.getDeclarant() != null) {
            notificationService.envoyer(incident.getDeclarant(), incident, "RESOLUTION", "Incident resolu: " + incident.getReference());
        }
        return incidentRepository.save(incident);
    }

    public boolean canAccess(Incident incident, Utilisateur utilisateur) {
        return switch (utilisateur.getRole()) {
            case ADMIN, RESPONSABLE_HSE, MANAGER -> true;
            case DECLARANT -> incident.getDeclarant() != null && incident.getDeclarant().getId().equals(utilisateur.getId());
            case TECHNICIEN -> incident.getResponsableTraitement() != null && incident.getResponsableTraitement().getId().equals(utilisateur.getId());
        };
    }

    private void prepareSmartFields(Incident incident) {
        int priorite = intelligenceService.priorite(incident);
        incident.setPriorite(priorite);
        incident.setNiveauRisque(intelligenceService.niveauRisque(priorite));
    }

    private String nextReference() {
        return "HSE-" + LocalDate.now().getYear() + "-" + String.format("%04d", incidentRepository.count() + 1);
    }
}

