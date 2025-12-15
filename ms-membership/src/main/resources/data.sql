-- Données initiales pour la base de données H2
-- Ce script est exécuté automatiquement au démarrage de l'application
-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

INSERT INTO users (first_name, last_name, email, created_at, updated_at)
VALUES
    ('Jean', 'Dupont', 'jean.dupont@esipen.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Pierre', 'Henry', 'pierre.henry@esipen.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Marie', 'Lefebvre', 'marie.lefebvre@esipen.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
