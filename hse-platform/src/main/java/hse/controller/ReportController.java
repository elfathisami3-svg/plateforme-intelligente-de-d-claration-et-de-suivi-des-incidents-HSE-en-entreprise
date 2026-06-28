package hse.controller;

import hse.model.Gravite;
import hse.model.StatutIncident;
import hse.repository.IncidentRepository;
import hse.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE_HSE','MANAGER')")
public class ReportController {
    private final DashboardService dashboardService;
    private final IncidentRepository incidentRepository;

    public ReportController(DashboardService dashboardService, IncidentRepository incidentRepository) {
        this.dashboardService = dashboardService;
        this.incidentRepository = incidentRepository;
    }

    @GetMapping
    public String reports(Model model) {
        model.addAttribute("stats", dashboardService.stats());
        model.addAttribute("statuts", Arrays.stream(StatutIncident.values())
                .map(statut -> new ReportLine(statut.name(), incidentRepository.countByStatut(statut)))
                .toList());
        model.addAttribute("gravites", Arrays.stream(Gravite.values())
                .map(gravite -> new ReportLine(gravite.name(), incidentRepository.countByGravite(gravite)))
                .toList());
        model.addAttribute("critiques", incidentRepository.findAll().stream()
                .filter(incident -> incident.getGravite() == Gravite.CRITIQUE)
                .toList());
        return "reports/index";
    }

    public record ReportLine(String label, long count) {
    }
}
