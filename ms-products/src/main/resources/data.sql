CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL,
    category VARCHAR(20) NOT NULL,
    imageUrl VARCHAR(255) DEFAULT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO products (name, description, price, stock, category, active, created_at, updated_at) VALUES
     ('Laptop Gamer X', 'Ordinateur portable haut de gamme pour gamers avec carte graphique RTX 4070.', 1499.99, 10, 'ELECTRONICS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Smartphone Pro Max', 'Smartphone dernier cri avec écran OLED et 512Go de stockage.', 999.99, 15, 'ELECTRONICS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Livre Java Spring', 'Guide complet pour apprendre Spring Boot et construire des applications REST.', 39.90, 50, 'BOOKS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Roman Policier', 'Un roman captivant qui vous tiendra en haleine jusqu’à la dernière page.', 14.50, 30, 'BOOKS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Pizza Margherita', 'Pizza italienne traditionnelle avec sauce tomate, mozzarella et basilic.', 8.99, 100, 'FOOD', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Pack Fruits Bio', 'Panier de fruits bio frais de saison, idéal pour une alimentation saine.', 19.99, 25, 'FOOD', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('T-shirt Homme', 'T-shirt 100% coton confortable disponible en plusieurs tailles.', 12.50, 40, 'OTHER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Sac à Dos', 'Sac à dos résistant avec plusieurs compartiments pour ordinateur et livres.', 49.90, 20, 'OTHER', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Casque Audio', 'Casque audio Bluetooth avec réduction de bruit active et longue autonomie.', 89.99, 15, 'ELECTRONICS', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
     ('Chocolats Assortis', 'Boîte de chocolats fins assortis pour toutes les occasions.', 24.99, 60, 'FOOD', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
