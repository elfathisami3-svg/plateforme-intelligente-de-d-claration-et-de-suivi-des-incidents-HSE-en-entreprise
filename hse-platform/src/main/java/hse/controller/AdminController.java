package hse.controller;

import hse.entity.CategorieIncident;
import hse.entity.ServiceDepartement;
import hse.entity.Site;
import hse.entity.Utilisateur;
import hse.model.Gravite;
import hse.model.Role;
import hse.model.StatutIncident;
import hse.repository.*;
import hse.service.CurrentUserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UtilisateurRepository utilisateurRepository;
    private final SiteRepository siteRepository;
    private final ServiceDepartementRepository serviceRepository;
    private final CategorieIncidentRepository categorieRepository;
    private final IncidentRepository incidentRepository;
    private final HistoriqueActionRepository historiqueRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UtilisateurRepository utilisateurRepository,
                           SiteRepository siteRepository,
                           ServiceDepartementRepository serviceRepository,
                           CategorieIncidentRepository categorieRepository,
                           IncidentRepository incidentRepository,
                           HistoriqueActionRepository historiqueRepository,
                           CurrentUserService currentUserService,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.siteRepository = siteRepository;
        this.serviceRepository = serviceRepository;
        this.categorieRepository = categorieRepository;
        this.incidentRepository = incidentRepository;
        this.historiqueRepository = historiqueRepository;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("users", utilisateurRepository.findAll());
        model.addAttribute("sites", siteRepository.findAll());
        model.addAttribute("services", serviceRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        model.addAttribute("roles", Role.values());
        model.addAttribute("gravites", Gravite.values());
        model.addAttribute("statuts", StatutIncident.values());
        model.addAttribute("historique", historiqueRepository.findTop50ByOrderByDateActionDesc());
        model.addAttribute("newUser", new Utilisateur());
        model.addAttribute("newSite", new Site());
        model.addAttribute("newService", new ServiceDepartement());
        model.addAttribute("newCategory", new CategorieIncident());
        return "admin/index";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute Utilisateur utilisateur, RedirectAttributes redirectAttributes) {
        utilisateur.setActif(true);
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateurRepository.save(utilisateur);
        redirectAttributes.addFlashAttribute("message", "Utilisateur cree.");
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String nom,
                             @RequestParam String prenom,
                             @RequestParam String email,
                             @RequestParam Role role,
                             @RequestParam(required = false) String motDePasse,
                             RedirectAttributes redirectAttributes) {
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setRole(role);
        if (motDePasse != null && !motDePasse.isBlank()) {
            user.setMotDePasse(passwordEncoder.encode(motDePasse));
        }
        utilisateurRepository.save(user);
        redirectAttributes.addFlashAttribute("message", "Utilisateur modifie.");
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Utilisateur current = currentUserService.get();
        if (current.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "Impossible de desactiver le compte actuellement connecte.");
            return "redirect:/admin";
        }
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        user.setActif(!user.isActif());
        utilisateurRepository.save(user);
        redirectAttributes.addFlashAttribute("message", user.isActif() ? "Compte active." : "Compte desactive.");
        return "redirect:/admin";
    }

    @PostMapping("/sites")
    public String createSite(@ModelAttribute Site site) {
        siteRepository.save(site);
        return "redirect:/admin";
    }

    @PostMapping("/services")
    public String createService(@ModelAttribute ServiceDepartement service) {
        serviceRepository.save(service);
        return "redirect:/admin";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute CategorieIncident category) {
        categorieRepository.save(category);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Utilisateur current = currentUserService.get();
        if (current.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer le compte actuellement connecte.");
            return "redirect:/admin";
        }
        Utilisateur user = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
        try {
            utilisateurRepository.delete(user);
            redirectAttributes.addFlashAttribute("message", "Utilisateur supprime.");
        } catch (DataIntegrityViolationException ex) {
            user.setActif(false);
            utilisateurRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Utilisateur lie a des donnees existantes: compte desactive.");
        }
        return "redirect:/admin";
    }

    @PostMapping("/sites/{id}/delete")
    @Transactional
    public String deleteSite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Site introuvable: " + id));
        serviceRepository.findBySiteId(id).forEach(service -> service.setSite(null));
        incidentRepository.findBySiteId(id).forEach(incident -> incident.setSite(null));
        siteRepository.delete(site);
        redirectAttributes.addFlashAttribute("message", "Site supprime.");
        return "redirect:/admin";
    }

    @PostMapping("/services/{id}/delete")
    @Transactional
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ServiceDepartement service = serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service introuvable: " + id));
        utilisateurRepository.findByServiceId(id).forEach(user -> user.setService(null));
        incidentRepository.findByServiceId(id).forEach(incident -> incident.setService(null));
        serviceRepository.delete(service);
        redirectAttributes.addFlashAttribute("message", "Service supprime.");
        return "redirect:/admin";
    }

    @PostMapping("/categories/{id}/delete")
    @Transactional
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        CategorieIncident category = categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categorie introuvable: " + id));
        incidentRepository.findByCategorieId(id).forEach(incident -> incident.setCategorie(null));
        categorieRepository.delete(category);
        redirectAttributes.addFlashAttribute("message", "Categorie supprimee.");
        return "redirect:/admin";
    }
}

