package hse.service;

import hse.model.Gravite;
import hse.model.StatutIncident;
import hse.repository.ActionCorrectiveRepository;
import hse.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final IncidentRepository incidentRepository;
    private final ActionCorrectiveRepository actionRepository;

    public DashboardService(IncidentRepository incidentRepository, ActionCorrectiveRepository actionRepository) {
        this.incidentRepository = incidentRepository;
        this.actionRepository = actionRepository;
    }

    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", incidentRepository.count());
        stats.put("ouverts", incidentRepository.findAll().stream()
                .filter(i -> i.getStatut() != StatutIncident.RESOLU && i.getStatut() != StatutIncident.CLOTURE)
                .count());
        stats.put("critiques", incidentRepository.countByGravite(Gravite.CRITIQUE));
        stats.put("clotures", incidentRepository.countByStatut(StatutIncident.CLOTURE));
        stats.put("actionsRetard", actionRepository.countByEtat(hse.model.EtatAction.EN_RETARD));
        return stats;
    }
}

