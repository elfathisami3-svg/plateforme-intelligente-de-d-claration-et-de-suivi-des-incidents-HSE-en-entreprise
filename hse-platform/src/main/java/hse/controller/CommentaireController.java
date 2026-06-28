package hse.controller;

import hse.entity.Commentaire;
import hse.repository.CommentaireRepository;
import hse.repository.IncidentRepository;
import hse.service.CurrentUserService;
import hse.service.HistoriqueService;
import hse.service.IncidentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/commentaires")
public class CommentaireController {
    private final CommentaireRepository commentaireRepository;
    private final IncidentRepository incidentRepository;
    private final CurrentUserService currentUserService;
    private final HistoriqueService historiqueService;
    private final IncidentService incidentService;

    public CommentaireController(CommentaireRepository commentaireRepository,
                                 IncidentRepository incidentRepository,
                                 CurrentUserService currentUserService,
                                 HistoriqueService historiqueService,
                                 IncidentService incidentService) {
        this.commentaireRepository = commentaireRepository;
        this.incidentRepository = incidentRepository;
        this.currentUserService = currentUserService;
        this.historiqueService = historiqueService;
        this.incidentService = incidentService;
    }

    @PostMapping("/incident/{incidentId}")
    public String add(@PathVariable Long incidentId, @RequestParam String contenu) {
        var user = currentUserService.get();
        var incident = incidentService.findVisibleById(incidentId, user);
        Commentaire commentaire = new Commentaire();
        commentaire.setIncident(incident);
        commentaire.setAuteur(user);
        commentaire.setContenu(contenu);
        commentaire.setDateCommentaire(LocalDateTime.now());
        commentaireRepository.save(commentaire);
        historiqueService.trace(incident, user, "Ajout commentaire", "", contenu);
        return "redirect:/incidents/" + incidentId;
    }
}

