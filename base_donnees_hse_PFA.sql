CREATE DATABASE IF NOT EXISTS plateforme_hse
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE plateforme_hse;

-- =========================
-- TABLE DES ROLES
-- =========================
CREATE TABLE roles (
    id_role BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_role VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES SITES
-- =========================
CREATE TABLE sites (
    id_site BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_site VARCHAR(150) NOT NULL,
    adresse VARCHAR(255),
    ville VARCHAR(100)
) ENGINE=InnoDB;

-- =========================
-- TABLE DES SERVICES
-- =========================
CREATE TABLE services (
    id_service BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_service VARCHAR(150) NOT NULL,
    description TEXT,
    id_site BIGINT,
    CONSTRAINT fk_service_site
        FOREIGN KEY (id_site) REFERENCES sites(id_site)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES UTILISATEURS
-- =========================
CREATE TABLE utilisateurs (
    id_utilisateur BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    telephone VARCHAR(30),
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    id_role BIGINT NOT NULL,
    id_service BIGINT,
    CONSTRAINT fk_utilisateur_role
        FOREIGN KEY (id_role) REFERENCES roles(id_role)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_utilisateur_service
        FOREIGN KEY (id_service) REFERENCES services(id_service)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES CATEGORIES D'INCIDENT
-- =========================
CREATE TABLE categories_incident (
    id_categorie BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(150) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB;

-- =========================
-- TABLE DES GRAVITES
-- =========================
CREATE TABLE gravites (
    id_gravite BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES STATUTS D'INCIDENT
-- =========================
CREATE TABLE statuts_incident (
    id_statut BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- TABLE PRINCIPALE DES INCIDENTS
-- =========================
CREATE TABLE incidents (
    id_incident BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_incident VARCHAR(50) NOT NULL UNIQUE,
    titre VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    date_declaration DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_incident DATETIME NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    niveau_risque VARCHAR(100),
    cause_presumee VARCHAR(255),
    id_declarant BIGINT NOT NULL,
    id_categorie BIGINT NOT NULL,
    id_gravite BIGINT NOT NULL,
    id_statut BIGINT NOT NULL,
    id_site BIGINT,
    id_responsable_hse BIGINT,
    id_responsable_traitement BIGINT,
    CONSTRAINT fk_incident_declarant
        FOREIGN KEY (id_declarant) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_incident_categorie
        FOREIGN KEY (id_categorie) REFERENCES categories_incident(id_categorie)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_incident_gravite
        FOREIGN KEY (id_gravite) REFERENCES gravites(id_gravite)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_incident_statut
        FOREIGN KEY (id_statut) REFERENCES statuts_incident(id_statut)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_incident_site
        FOREIGN KEY (id_site) REFERENCES sites(id_site)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT fk_incident_responsable_hse
        FOREIGN KEY (id_responsable_hse) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT fk_incident_responsable_traitement
        FOREIGN KEY (id_responsable_traitement) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES ACTIONS CORRECTIVES
-- =========================
CREATE TABLE actions_correctives (
    id_action BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_echeance DATETIME,
    date_realisation DATETIME,
    etat VARCHAR(100) NOT NULL,
    resultat TEXT,
    id_incident BIGINT NOT NULL,
    id_responsable BIGINT,
    CONSTRAINT fk_action_incident
        FOREIGN KEY (id_incident) REFERENCES incidents(id_incident)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_action_responsable
        FOREIGN KEY (id_responsable) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES COMMENTAIRES
-- =========================
CREATE TABLE commentaires (
    id_commentaire BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenu TEXT NOT NULL,
    date_commentaire DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_incident BIGINT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    CONSTRAINT fk_commentaire_incident
        FOREIGN KEY (id_incident) REFERENCES incidents(id_incident)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_commentaire_utilisateur
        FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES PIECES JOINTES
-- =========================
CREATE TABLE pieces_jointes (
    id_piece BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_fichier VARCHAR(255) NOT NULL,
    type_fichier VARCHAR(100),
    chemin_fichier VARCHAR(255) NOT NULL,
    date_ajout DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_incident BIGINT NOT NULL,
    CONSTRAINT fk_piece_incident
        FOREIGN KEY (id_incident) REFERENCES incidents(id_incident)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DES NOTIFICATIONS
-- =========================
CREATE TABLE notifications (
    id_notification BIGINT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    date_envoi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN NOT NULL DEFAULT FALSE,
    type_notification VARCHAR(100),
    id_utilisateur BIGINT NOT NULL,
    id_incident BIGINT,
    CONSTRAINT fk_notification_utilisateur
        FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_notification_incident
        FOREIGN KEY (id_incident) REFERENCES incidents(id_incident)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABLE DE L'HISTORIQUE DES ACTIONS
-- =========================
CREATE TABLE historique_actions (
    id_historique BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_effectuee VARCHAR(255) NOT NULL,
    date_action DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ancienne_valeur TEXT,
    nouvelle_valeur TEXT,
    id_incident BIGINT NOT NULL,
    id_utilisateur BIGINT NOT NULL,
    CONSTRAINT fk_historique_incident
        FOREIGN KEY (id_incident) REFERENCES incidents(id_incident)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_historique_utilisateur
        FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs(id_utilisateur)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- INSERTIONS DE BASE
-- =========================
INSERT INTO roles (nom_role) VALUES
('Administrateur'),
('Déclarant'),
('Responsable HSE'),
('Technicien'),
('Manager');

INSERT INTO gravites (libelle) VALUES
('Faible'),
('Moyenne'),
('Élevée'),
('Critique');

INSERT INTO statuts_incident (libelle) VALUES
('Déclaré'),
('En analyse'),
('Affecté'),
('En traitement'),
('Résolu'),
('Clôturé');

INSERT INTO categories_incident (libelle, description) VALUES
('Accident de travail', 'Incident ayant causé une blessure ou un dommage corporel'),
('Quasi-incident', 'Situation à risque n’ayant pas causé de dommage direct'),
('Incident environnemental', 'Incident ayant un impact sur l’environnement'),
('Situation dangereuse', 'Condition ou comportement pouvant générer un accident'),
('Non-conformité', 'Non-respect d’une règle ou procédure HSE');
