package hse;

import hse.entity.*;
import hse.model.*;
import hse.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private final SiteRepository siteRepository;
    private final ServiceDepartementRepository serviceRepository;
    private final CategorieIncidentRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final IncidentRepository incidentRepository;
    private final ActionCorrectiveRepository actionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SiteRepository siteRepository,
                           ServiceDepartementRepository serviceRepository,
                           CategorieIncidentRepository categorieRepository,
                           UtilisateurRepository utilisateurRepository,
                           IncidentRepository incidentRepository,
                           ActionCorrectiveRepository actionRepository,
                           PasswordEncoder passwordEncoder) {
        this.siteRepository = siteRepository;
        this.serviceRepository = serviceRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.incidentRepository = incidentRepository;
        this.actionRepository = actionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        encodeExistingPlainPasswords();
        if (siteRepository.count() > 0) {
            return;
        }

        Site casa = site("Usine Casablanca", "Zone industrielle Ain Sebaa", "Casablanca");
        Site tanger = site("Depot Tanger", "Zone logistique", "Tanger");

        ServiceDepartement production = service("Production", "Lignes de fabrication", casa);
        ServiceDepartement maintenance = service("Maintenance", "Maintenance industrielle", casa);
        ServiceDepartement hse = service("HSE", "Hygiene securite environnement", casa);
        service("Logistique", "Entrepot et expedition", tanger);

        CategorieIncident accident = categorie("Accident de travail", "Blessure ou dommage corporel.");
        CategorieIncident quasi = categorie("Quasi-incident", "Evenement sans dommage mais a potentiel risque.");
        categorie("Incident environnemental", "Impact environnemental.");
        CategorieIncident situation = categorie("Situation dangereuse", "Condition ou comportement dangereux.");
        categorie("Non-conformite", "Non-respect d'une regle HSE.");

        Utilisateur admin = user("Admin", "Systeme", "admin@hse.local", "admin", Role.ADMIN, hse);
        Utilisateur rhse = user("Responsable", "HSE", "hse@hse.local", "hse", Role.RESPONSABLE_HSE, hse);
        Utilisateur tech = user("Technicien", "Maintenance", "tech@hse.local", "tech", Role.TECHNICIEN, maintenance);
        user("Manager", "Direction", "manager@hse.local", "manager", Role.MANAGER, production);
        Utilisateur employe = user("Employe", "Production", "user@hse.local", "user", Role.DECLARANT, production);

        Incident incident = new Incident();
        incident.setReference("HSE-" + LocalDate.now().getYear() + "-0001");
        incident.setTitre("Fuite d'huile pres de la ligne 2");
        incident.setDescription("Presence d'huile au sol avec risque de glissade pour les operateurs.");
        incident.setDateDeclaration(LocalDateTime.now().minusDays(2));
        incident.setDateIncident(LocalDate.now().minusDays(2));
        incident.setLieu("Atelier production");
        incident.setTypeIncident(TypeIncident.SITUATION_DANGEREUSE);
        incident.setGravite(Gravite.ELEVEE);
        incident.setStatut(StatutIncident.EN_TRAITEMENT);
        incident.setPriorite(4);
        incident.setNiveauRisque("ELEVE");
        incident.setCausePresumee("Joint defectueux");
        incident.setDateEcheance(LocalDate.now().plusDays(3));
        incident.setSite(casa);
        incident.setService(production);
        incident.setCategorie(situation);
        incident.setDeclarant(employe);
        incident.setResponsableHse(rhse);
        incident.setResponsableTraitement(tech);
        incidentRepository.save(incident);

        ActionCorrective action = new ActionCorrective();
        action.setIncident(incident);
        action.setResponsable(tech);
        action.setDescription("Nettoyer la zone, remplacer le joint et verifier l'absence de fuite.");
        action.setDateCreation(LocalDateTime.now().minusDays(1));
        action.setDateEcheance(LocalDate.now().plusDays(1));
        action.setEtat(EtatAction.EN_COURS);
        actionRepository.save(action);

        Incident incident2 = new Incident();
        incident2.setReference("HSE-" + LocalDate.now().getYear() + "-0002");
        incident2.setTitre("Quasi-incident lors d'une manutention");
        incident2.setDescription("Charge mal positionnee detectee avant levage.");
        incident2.setDateDeclaration(LocalDateTime.now().minusDays(1));
        incident2.setDateIncident(LocalDate.now().minusDays(1));
        incident2.setLieu("Zone expedition");
        incident2.setTypeIncident(TypeIncident.QUASI_INCIDENT);
        incident2.setGravite(Gravite.MOYENNE);
        incident2.setStatut(StatutIncident.DECLARE);
        incident2.setPriorite(2);
        incident2.setNiveauRisque("MOYEN");
        incident2.setDateEcheance(LocalDate.now().plusDays(7));
        incident2.setSite(tanger);
        incident2.setService(maintenance);
        incident2.setCategorie(quasi);
        incident2.setDeclarant(employe);
        incidentRepository.save(incident2);
    }

    private Site site(String nom, String adresse, String ville) {
        Site site = new Site();
        site.setNom(nom);
        site.setAdresse(adresse);
        site.setVille(ville);
        return siteRepository.save(site);
    }

    private ServiceDepartement service(String nom, String description, Site site) {
        ServiceDepartement service = new ServiceDepartement();
        service.setNom(nom);
        service.setDescription(description);
        service.setSite(site);
        return serviceRepository.save(service);
    }

    private CategorieIncident categorie(String libelle, String description) {
        CategorieIncident categorie = new CategorieIncident();
        categorie.setLibelle(libelle);
        categorie.setDescription(description);
        return categorieRepository.save(categorie);
    }

    private Utilisateur user(String nom, String prenom, String email, String password, Role role, ServiceDepartement service) {
        Utilisateur user = new Utilisateur();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(passwordEncoder.encode(password));
        user.setRole(role);
        user.setService(service);
        user.setActif(true);
        return utilisateurRepository.save(user);
    }

    private void encodeExistingPlainPasswords() {
        utilisateurRepository.findAll().forEach(user -> {
            String password = user.getMotDePasse();
            if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                user.setMotDePasse(passwordEncoder.encode(password));
                utilisateurRepository.save(user);
            }
        });
    }
}

