package hse.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HistoriqueAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actionEffectuee;
    private LocalDateTime dateAction;

    @Lob
    private String ancienneValeur;

    @Lob
    private String nouvelleValeur;

    @ManyToOne
    private Incident incident;

    @ManyToOne
    private Utilisateur utilisateur;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getActionEffectuee() { return actionEffectuee; }
    public void setActionEffectuee(String actionEffectuee) { this.actionEffectuee = actionEffectuee; }
    public LocalDateTime getDateAction() { return dateAction; }
    public void setDateAction(LocalDateTime dateAction) { this.dateAction = dateAction; }
    public String getAncienneValeur() { return ancienneValeur; }
    public void setAncienneValeur(String ancienneValeur) { this.ancienneValeur = ancienneValeur; }
    public String getNouvelleValeur() { return nouvelleValeur; }
    public void setNouvelleValeur(String nouvelleValeur) { this.nouvelleValeur = nouvelleValeur; }
    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}

