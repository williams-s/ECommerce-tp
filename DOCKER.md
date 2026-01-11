# Dockerisation des microservices e-commerce

## Monitoring avec Prometheus & Grafana

### Accès aux interfaces

- **Prometheus** : http://localhost:9090
- **Grafana** : http://localhost:3000 (admin/admin)

### Configuration Grafana

1. Ajouter Prometheus comme Data Source :
    - URL : `http://prometheus:9090`
    - Save & Test

2. Importer un dashboard JVM :
    - Dashboard ID : `4701`
    - Data source : Prometheus

### Métriques disponibles

Les 3 microservices exposent leurs métriques via `/actuator/prometheus` :
- Requêtes HTTP
- Utilisation JVM
- Etat des services

## 1. Génération des clés RSA

```
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

---

## 2. Commandes pour build 

```
docker build -t kabraass/ecommerce-membership:1.0 .
docker build -t kabraass/ecommerce-product:1.0 .
docker build -t kabraass/ecommerce-order:1.0 .
```
---

## 3. Commandes pour push

```
docker push kabraass/ecommerce-membership:1.0
docker push kabraass/ecommerce-product:1.0
docker push kabraass/ecommerce-order:1.0
```
---

## 4. Commandes pour pull

```
docker pull kabraass/ecommerce-membership:1.0
docker pull kabraass/ecommerce-product:1.0
docker pull kabraass/ecommerce-order:1.0
```
---

## 5. Commandes pour exécuter

```
docker compose up -d 
```
---
## 6. Commandes pour recréer la plateforme de zéro 

```
docker compose down
docker compose up --build
```

## 7. Configuration Docker Hub (repository privé)

Etant donné que dans le plan gratuit de docker hub nous ne pouvons créer qu'un seul repository privé, tous les repository du projet sont en public.


