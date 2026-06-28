package hse.service;

import hse.entity.Incident;
import hse.model.Gravite;
import hse.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class IntelligenceService {
    private final IncidentRepository incidentRepository;

    public IntelligenceService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public int priorite(Incident incident) {
        int score = incident.getGravite() == null ? 1 : incident.getGravite().getScore();
        String texte = ((incident.getTitre() == null ? "" : incident.getTitre()) + " " +
                (incident.getDescription() == null ? "" : incident.getDescription()) + " " +
                (incident.getCausePresumee() == null ? "" : incident.getCausePresumee())).toLowerCase(Locale.ROOT);

        if (texte.contains("incendie") || texte.contains("toxique") || texte.contains("bless") || texte.contains("fuite")) {
            score++;
        }
        if (incident.getCategorie() != null) {
            long similaires = incidentsSimilaires(incident).size();
            if (similaires >= 2) {
                score++;
            }
        }
        return Math.min(score, 5);
    }

    public String niveauRisque(int priorite) {
        if (priorite >= 5) return "CRITIQUE";
        if (priorite >= 4) return "ELEVE";
        if (priorite >= 2) return "MOYEN";
        return "FAIBLE";
    }

    public List<Incident> incidentsSimilaires(Incident incident) {
        return incidentRepository.findAll().stream()
                .filter(item -> !item.getId().equals(incident.getId()))
                .filter(item -> (item.getCategorie() != null && incident.getCategorie() != null
                        && item.getCategorie().getId().equals(incident.getCategorie().getId()))
                        || (item.getService() != null && incident.getService() != null
                        && item.getService().getId().equals(incident.getService().getId())))
                .limit(8)
                .toList();
    }

    public List<String> recommandations(Incident incident) {
        List<String> recommandations = new ArrayList<>();
        String texte = ((incident.getTitre() == null ? "" : incident.getTitre()) + " " +
                (incident.getDescription() == null ? "" : incident.getDescription()) + " " +
                (incident.getCategorie() == null ? "" : incident.getCategorie().getLibelle())).toLowerCase(Locale.ROOT);

        if (texte.contains("fuite")) {
            recommandations.add("Isoler la zone et contenir la fuite.");
            recommandations.add("Identifier la source et planifier une maintenance corrective.");
        }
        if (texte.contains("chute") || texte.contains("glissade")) {
            recommandations.add("Baliser la zone et supprimer le risque de chute.");
            recommandations.add("Verifier l'etat du sol, les EPI et les consignes de circulation.");
        }
        if (incident.getGravite() == Gravite.CRITIQUE) {
            recommandations.add("Declencher une alerte immediate au responsable HSE et a la direction.");
            recommandations.add("Realiser une analyse des causes avant reprise d'activite.");
        }
        if (recommandations.isEmpty()) {
            recommandations.add("Analyser la cause racine avec le responsable du service.");
            recommandations.add("Definir une action corrective avec responsable et echeance.");
        }
        return recommandations;
    }
}

