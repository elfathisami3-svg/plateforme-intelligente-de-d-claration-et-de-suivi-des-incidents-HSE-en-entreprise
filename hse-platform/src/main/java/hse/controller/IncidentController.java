package hse.controller;

import hse.entity.Incident;
import hse.model.Gravite;
import hse.model.Role;
import hse.model.StatutIncident;
import hse.model.TypeIncident;
import hse.repository.*;
import hse.service.CurrentUserService;
import hse.service.IncidentService;
import hse.service.IntelligenceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/incidents")
public class IncidentController {
    private final IncidentService incidentService;
    private final CurrentUserService currentUserService;
    private final IntelligenceService intelligenceService;
    private final SiteRepository siteRepository;
    private final ServiceDepartementRepository serviceRepository;
    private final CategorieIncidentRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CommentaireRepository commentaireRepository;
    private final PieceJointeRepository pieceJointeRepository;
    private final ActionCorrectiveRepository actionRepository;
    private final HistoriqueActionRepository historiqueRepository;

    public IncidentController(IncidentService incidentService,
                              CurrentUserService currentUserService,
                              IntelligenceService intelligenceService,
                              SiteRepository siteRepository,
                              ServiceDepartementRepository serviceRepository,
                              CategorieIncidentRepository categorieRepository,
                              UtilisateurRepository utilisateurRepository,
                              CommentaireRepository commentaireRepository,
                              PieceJointeRepository pieceJointeRepository,
                              ActionCorrectiveRepository actionRepository,
                              HistoriqueActionRepository historiqueRepository) {
        this.incidentService = incidentService;
        this.currentUserService = currentUserService;
        this.intelligenceService = intelligenceService;
        this.siteRepository = siteRepository;
        this.serviceRepository = serviceRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.commentaireRepository = commentaireRepository;
        this.pieceJointeRepository = pieceJointeRepository;
        this.actionRepository = actionRepository;
        this.historiqueRepository = historiqueRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("incidents", incidentService.findVisibleFor(currentUserService.get()));
        return "incidents/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("incident", new Incident());
        addFormData(model);
        return "incidents/form";
    }

    @PostMapping
    public String create(@ModelAttribute Incident incident, RedirectAttributes redirectAttributes) {
        incidentService.declarer(incident, currentUserService.get());
        redirectAttributes.addFlashAttribute("message", "Incident declare avec succes.");
        return "redirect:/incidents";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Incident incident = incidentService.findVisibleById(id, currentUserService.get());
        model.addAttribute("incident", incident);
        model.addAttribute("commentaires", commentaireRepository.findByIncidentIdOrderByDateCommentaireDesc(id));
        model.addAttribute("pieces", pieceJointeRepository.findByIncidentId(id));
        model.addAttribute("actions", actionRepository.findByIncidentId(id));
        model.addAttribute("historique", historiqueRepository.findByIncidentIdOrderByDateActionDesc(id));
        model.addAttribute("recommandations", intelligenceService.recommandations(incident));
        model.addAttribute("similaires", intelligenceService.incidentsSimilaires(incident));
        return "incidents/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("incident", incidentService.findVisibleById(id, currentUserService.get()));
        addFormData(model);
        return "incidents/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Incident incident, RedirectAttributes redirectAttributes) {
        incidentService.modifier(id, incident, currentUserService.get());
        redirectAttributes.addFlashAttribute("message", "Incident mis a jour.");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/close")
    public String close(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidentService.cloturer(id, currentUserService.get());
            redirectAttributes.addFlashAttribute("message", "Incident cloture.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/validate")
    public String validate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidentService.valider(id, currentUserService.get());
            redirectAttributes.addFlashAttribute("message", "Declaration validee.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/take")
    public String take(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidentService.prendreEnCharge(id, currentUserService.get());
            redirectAttributes.addFlashAttribute("message", "Incident pris en charge.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/resolve")
    public String resolve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidentService.resoudre(id, currentUserService.get());
            redirectAttributes.addFlashAttribute("message", "Incident marque comme resolu.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/incidents/" + id;
    }

    private void addFormData(Model model) {
        model.addAttribute("sites", siteRepository.findAll());
        model.addAttribute("services", serviceRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        model.addAttribute("techniciens", utilisateurRepository.findByRoleAndActifTrue(Role.TECHNICIEN));
        model.addAttribute("responsablesHse", utilisateurRepository.findByRoleAndActifTrue(Role.RESPONSABLE_HSE));
        model.addAttribute("gravites", Gravite.values());
        model.addAttribute("statuts", StatutIncident.values());
        model.addAttribute("types", TypeIncident.values());
    }
}

