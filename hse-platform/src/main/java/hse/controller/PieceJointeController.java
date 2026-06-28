package hse.controller;

import hse.entity.PieceJointe;
import hse.repository.IncidentRepository;
import hse.repository.PieceJointeRepository;
import hse.service.CurrentUserService;
import hse.service.HistoriqueService;
import hse.service.IncidentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/pieces")
public class PieceJointeController {
    private final PieceJointeRepository pieceJointeRepository;
    private final IncidentRepository incidentRepository;
    private final CurrentUserService currentUserService;
    private final HistoriqueService historiqueService;
    private final IncidentService incidentService;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public PieceJointeController(PieceJointeRepository pieceJointeRepository,
                                 IncidentRepository incidentRepository,
                                 CurrentUserService currentUserService,
                                 HistoriqueService historiqueService,
                                 IncidentService incidentService) {
        this.pieceJointeRepository = pieceJointeRepository;
        this.incidentRepository = incidentRepository;
        this.currentUserService = currentUserService;
        this.historiqueService = historiqueService;
        this.incidentService = incidentService;
    }

    @PostMapping("/incident/{incidentId}")
    public String upload(@PathVariable Long incidentId, @RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "redirect:/incidents/" + incidentId;
        }
        var incident = incidentService.findVisibleById(incidentId, currentUserService.get());
        Files.createDirectories(Path.of(uploadDir));
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "-" + original;
        Path target = Path.of(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), target);

        PieceJointe piece = new PieceJointe();
        piece.setIncident(incident);
        piece.setNomFichier(original);
        piece.setTypeFichier(file.getContentType());
        piece.setCheminFichier(target.toString());
        piece.setDateAjout(LocalDateTime.now());
        pieceJointeRepository.save(piece);
        historiqueService.trace(incident, currentUserService.get(), "Ajout piece jointe", "", original);
        return "redirect:/incidents/" + incidentId;
    }
}

