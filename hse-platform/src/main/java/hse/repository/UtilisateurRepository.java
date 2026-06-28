package hse.repository;

import hse.entity.Utilisateur;
import hse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRoleAndActifTrue(Role role);
    List<Utilisateur> findByServiceId(Long serviceId);
}

