package hse.service;

import hse.entity.Incident;
import hse.entity.Notification;
import hse.entity.Utilisateur;
import hse.model.Role;
import hse.repository.NotificationRepository;
import hse.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationService(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public void envoyer(Utilisateur utilisateur, Incident incident, String type, String message) {
        Notification notification = new Notification();
        notification.setUtilisateur(utilisateur);
        notification.setIncident(incident);
        notification.setTypeNotification(type);
        notification.setMessage(message);
        notification.setDateEnvoi(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void notifierRole(Role role, Incident incident, String type, String message) {
        utilisateurRepository.findByRoleAndActifTrue(role)
                .forEach(utilisateur -> envoyer(utilisateur, incident, type, message));
    }
}

