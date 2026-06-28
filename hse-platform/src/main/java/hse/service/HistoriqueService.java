package hse.service;

import hse.entity.HistoriqueAction;
import hse.entity.Incident;
import hse.entity.Utilisateur;
import hse.repository.HistoriqueActionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoriqueService {
    private final HistoriqueActionRepository historiqueRepository;

    public HistoriqueService(HistoriqueActionRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    public void trace(Incident incident, Utilisateur utilisateur, String action, String ancienneValeur, String nouvelleValeur) {
        HistoriqueAction historique = new HistoriqueAction();
        historique.setIncident(incident);
        historique.setUtilisateur(utilisateur);
        historique.setActionEffectuee(action);
        historique.setAncienneValeur(ancienneValeur);
        historique.setNouvelleValeur(nouvelleValeur);
        historique.setDateAction(LocalDateTime.now());
        historiqueRepository.save(historique);
    }
}

