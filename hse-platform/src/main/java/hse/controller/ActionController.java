package hse.controller;

import hse.entity.ActionCorrective;
import hse.model.EtatAction;
import hse.model.Role;
import hse.repository.IncidentRepository;
import hse.repository.UtilisateurRepository;
import hse.service.ActionCorrectiveService;
import hse.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/actions")
public class ActionController {
    private final ActionCorrectiveService actionService;
    private final CurrentUserService currentUserService;
    private final IncidentRepository incidentRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ActionController(ActionCorrectiveService actionService,
                            CurrentUserService currentUserService,
                            IncidentRepository incidentRepository,
                            UtilisateurRepository utilisateurRepository) {
        this.actionService = actionService;
        this.currentUserService = currentUserService;
        this.incidentRepository = incidentRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("actions", actionService.findVisibleFor(currentUserService.get()));
        model.addAttribute("etats", EtatAction.values());
        return "actions/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("action", new ActionCorrective());
        addFormData(model);
        return "actions/form";
    }

    @PostMapping
    public String save(@ModelAttribute ActionCorrective action, RedirectAttributes redirectAttributes) {
        try {
            actionService.save(action, currentUserService.get());
            redirectAttributes.addFlashAttribute("message", "Action corrective enregistree.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/actions";
    }

    @PostMapping("/{id}/etat")
    public String updateEtat(@PathVariable Long id,
                             @RequestParam EtatAction etat,
                             @RequestParam(required = false) String resultat,
                             RedirectAttributes redirectAttributes) {
        try {
            actionService.updateEtat(id, etat, resultat, currentUserService.get());
            redirectAttributes.addFlashAttribute("message", "Avancement mis a jour.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/actions";
    }

    private void addFormData(Model model) {
        model.addAttribute("incidents", incidentRepository.findAll());
        model.addAttribute("utilisateurs", utilisateurRepository.findByRoleAndActifTrue(Role.TECHNICIEN));
        model.addAttribute("etats", EtatAction.values());
    }
}

