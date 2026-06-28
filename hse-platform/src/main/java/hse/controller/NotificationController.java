package hse.controller;

import hse.repository.NotificationRepository;
import hse.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationRepository notificationRepository, CurrentUserService currentUserService) {
        this.notificationRepository = notificationRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String list(Model model) {
        var user = currentUserService.get();
        model.addAttribute("notifications", notificationRepository.findByUtilisateurIdOrderByDateEnvoiDesc(user.getId()));
        return "notifications/list";
    }
}
