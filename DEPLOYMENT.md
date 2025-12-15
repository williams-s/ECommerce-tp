Guide de Déploiement
Auteur: Younes BEN BOUBAKER et Williams ANTHONY

0. Prérequis

Avant de déployer la plateforme, les outils suivants doivent être installés sur votre machine :

- Java JDK 21
- Maven 3.8+
- Git
- Un outil de test d’API


1. Récupération du projet

Cloner le dépôt contenant l’ensemble des microservices :

git clone https://github.com/williams-s/ECommerce-tp
cd ECommerce-tp

L’arborescence du projet doit contenir:

- ms-membership/
- ms-products/
- ms-orders/
- architecture/
- postman/
- Deployment.md

2. Compilation des microservices

Chaque microservice doit être compilé séparément.

2.1 Service Membership

cd ms-membership
mvn clean compile

2.2 Service Product

cd ../ms-products
mvn clean compile

2.3 Service Order

cd ../ms-orders
mvn clean compile

3. Démarrage des microservices

3.1 Lancer le service Membership

cd ms-membership
mvn spring-boot:run


Service disponible sur :
http://localhost:8081

3.2 Lancer le service Product
Lancer un nouveau terminal

cd ms-products
mvn spring-boot:run


Service disponible sur :
http://localhost:8082

3.3 Lancer le service Order
Lancer un nouveau terminal

cd ms-orders
mvn spring-boot:run


Service disponible sur :
http://localhost:8083