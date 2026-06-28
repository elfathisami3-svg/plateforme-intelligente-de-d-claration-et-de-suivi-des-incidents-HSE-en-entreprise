package hse.controller;

import hse.repository.HistoriqueActionRepository;
import hse.service.CurrentUserService;
import hse.service.DashboardService;
import hse.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final DashboardService dashboardService;
    private final IncidentService incidentService;
    private final HistoriqueActionRepository historiqueRepository;
    private final CurrentUserService currentUserService;

    public HomeController(DashboardService dashboardService,
                          IncidentService incidentService,
                          HistoriqueActionRepository historiqueRepository,
                          CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.incidentService = incidentService;
        this.historiqueRepository = historiqueRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        var user = currentUserService.get();
        model.addAttribute("stats", dashboardService.stats());
        model.addAttribute("incidents", incidentService.findVisibleFor(user));
        model.addAttribute("historique", historiqueRepository.findTop50ByOrderByDateActionDesc());
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

