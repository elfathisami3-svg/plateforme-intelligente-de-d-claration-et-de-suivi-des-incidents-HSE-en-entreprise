package hse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import hse.model.Role;

@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String motDePasse;

    private String telephone;
    private boolean actif = true;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    private ServiceDepartement service;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public ServiceDepartement getService() { return service; }
    public void setService(ServiceDepartement service) { this.service = service; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}

