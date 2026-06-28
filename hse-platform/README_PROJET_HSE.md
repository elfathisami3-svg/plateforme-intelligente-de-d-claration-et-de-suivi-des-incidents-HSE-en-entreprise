# Plateforme HSE - Spring Boot Web App

Projet reconstruit selon le cahier des charges en application web Java Spring Boot.

## Technologies

- Java 17
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL via XAMPP

## Lancement

Avant de lancer l'application, demarrer MySQL dans XAMPP. La base `plateforme_hse` est creee automatiquement si elle n'existe pas.

Par defaut, l'application utilise:

```text
jdbc:mysql://localhost:3306/plateforme_hse?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
utilisateur: root
mot de passe: vide par defaut avec XAMPP
```

Pour changer les identifiants:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="ton_mot_de_passe"
```

Verifier que MySQL/XAMPP ecoute sur le port 3306 avant de lancer l'application:

```powershell
.\check-db.bat
```

Generer un log complet si le lancement echoue:

```powershell
.\run-debug.bat
```

```powershell
cd C:\Users\Taha\Desktop\PFA_sami\hse-platform
.\run.bat
```

Le projet utilise le JDK integre a IntelliJ dans les scripts Windows:

```text
C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr
```

Puis ouvrir:

```text
http://localhost:8080
```

## Comptes de demonstration

| Email | Mot de passe | Role |
| --- | --- | --- |
| admin@hse.local | admin | ADMIN |
| hse@hse.local | hse | RESPONSABLE_HSE |
| tech@hse.local | tech | TECHNICIEN |
| manager@hse.local | manager | MANAGER |
| user@hse.local | user | DECLARANT |

## Fonctionnalites couvertes

- Authentification et roles.
- Declaration d'incident.
- Qualification: type, categorie, gravite, priorite et risque.
- Affectation a un responsable HSE et a un responsable de traitement.
- Cycle de statut: declare, analyse, affecte, traitement, resolu, cloture.
- Actions correctives avec responsable, echeance, retard et resultat.
- Commentaires.
- Pieces jointes.
- Notifications internes.
- Historique et tracabilite.
- Tableau de bord.
- Module intelligent simple: priorite, recommandations et incidents similaires.

## Structure

- `src/main/java/hse`: code Java principal.
- `src/main/java/hse/entity`: entites JPA.
- `src/main/java/hse/repository`: interfaces Spring Data.
- `src/main/java/hse/service`: logique metier.
- `src/main/java/hse/controller`: controleurs MVC.
- `src/main/resources/templates`: pages Thymeleaf.
- `src/main/resources/static/css`: style web.
