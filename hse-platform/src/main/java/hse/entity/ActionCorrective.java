package hse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import hse.model.EtatAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class ActionCorrective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @NotBlank
    private String description;

    private LocalDateTime dateCreation;
    private LocalDate dateEcheance;
    private LocalDate dateRealisation;

    @Enumerated(EnumType.STRING)
    private EtatAction etat;

    @Lob
    private String resultat;

    @ManyToOne
    private Incident incident;

    @ManyToOne
    private Utilisateur responsable;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public LocalDate getDateRealisation() { return dateRealisation; }
    public void setDateRealisation(LocalDate dateRealisation) { this.dateRealisation = dateRealisation; }
    public EtatAction getEtat() { return etat; }
    public void setEtat(EtatAction etat) { this.etat = etat; }
    public String getResultat() { return resultat; }
    public void setResultat(String resultat) { this.resultat = resultat; }
    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }
    public Utilisateur getResponsable() { return responsable; }
    public void setResponsable(Utilisateur responsable) { this.responsable = responsable; }
}

