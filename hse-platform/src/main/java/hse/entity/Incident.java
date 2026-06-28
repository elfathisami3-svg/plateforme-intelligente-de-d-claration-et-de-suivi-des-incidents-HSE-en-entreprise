package hse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import hse.model.Gravite;
import hse.model.StatutIncident;
import hse.model.TypeIncident;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @NotBlank
    private String titre;

    @Lob
    @NotBlank
    private String description;

    private LocalDateTime dateDeclaration;

    @NotNull
    private LocalDate dateIncident;

    @NotBlank
    private String lieu;

    @Enumerated(EnumType.STRING)
    private TypeIncident typeIncident;

    @Enumerated(EnumType.STRING)
    private Gravite gravite;

    @Enumerated(EnumType.STRING)
    private StatutIncident statut;

    private Integer priorite;
    private String niveauRisque;
    private String causePresumee;
    private LocalDate dateEcheance;

    @ManyToOne
    private Utilisateur declarant;

    @ManyToOne
    private Utilisateur responsableHse;

    @ManyToOne
    private Utilisateur responsableTraitement;

    @ManyToOne
    private Site site;

    @ManyToOne
    private ServiceDepartement service;

    @ManyToOne
    private CategorieIncident categorie;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateDeclaration() { return dateDeclaration; }
    public void setDateDeclaration(LocalDateTime dateDeclaration) { this.dateDeclaration = dateDeclaration; }
    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public TypeIncident getTypeIncident() { return typeIncident; }
    public void setTypeIncident(TypeIncident typeIncident) { this.typeIncident = typeIncident; }
    public Gravite getGravite() { return gravite; }
    public void setGravite(Gravite gravite) { this.gravite = gravite; }
    public StatutIncident getStatut() { return statut; }
    public void setStatut(StatutIncident statut) { this.statut = statut; }
    public Integer getPriorite() { return priorite; }
    public void setPriorite(Integer priorite) { this.priorite = priorite; }
    public String getNiveauRisque() { return niveauRisque; }
    public void setNiveauRisque(String niveauRisque) { this.niveauRisque = niveauRisque; }
    public String getCausePresumee() { return causePresumee; }
    public void setCausePresumee(String causePresumee) { this.causePresumee = causePresumee; }
    public LocalDate getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }
    public Utilisateur getDeclarant() { return declarant; }
    public void setDeclarant(Utilisateur declarant) { this.declarant = declarant; }
    public Utilisateur getResponsableHse() { return responsableHse; }
    public void setResponsableHse(Utilisateur responsableHse) { this.responsableHse = responsableHse; }
    public Utilisateur getResponsableTraitement() { return responsableTraitement; }
    public void setResponsableTraitement(Utilisateur responsableTraitement) { this.responsableTraitement = responsableTraitement; }
    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
    public ServiceDepartement getService() { return service; }
    public void setService(ServiceDepartement service) { this.service = service; }
    public CategorieIncident getCategorie() { return categorie; }
    public void setCategorie(CategorieIncident categorie) { this.categorie = categorie; }
}

