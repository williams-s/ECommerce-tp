## 1. Architecture de sécurité

### Microservices

* **Membership Service**

    * Authentifie l’utilisateur
    * Génère un JWT signé avec une **clé privée RSA**

* **Product Service**

    * Vérifie les JWT avec la **clé publique RSA**

* **Order Service**

    * Vérifie les JWT avec la **clé publique RSA**

---

## 2. Diagramme de séquence de l'authentification

![JWT Authentication](./SequenceSecurity.png)
---

## 3. JWT

### Algorithme

* **RS256 (RSA + SHA‑256)**

### Contenu du token (claims)

```json
{
  "sub": "jean.dupont@esipen.com",
  "userId": 1,
  "roles": ["USER"],
  "iat": 1710000000,
  "exp": 1710003600
}
```

* `userId` : identifiant utilisateur
* `roles` : rôles de l’utilisateur
* `exp` : date d’expiration

---

## 4. Génération des clés RSA

### Commandes de génération
```
openssl genrsa -out private_key.pem 2048

openssl rsa -in private_key.pem -pubout -out public_key.pem
```

### Distribution des clés

* **Clé privée** (`private_key.pem`) :
  - Stockée uniquement dans le service Membership
  - Placée dans `src/main/resources/`
  - Utilisée pour signer les JWT

* **Clé publique** (`public_key.pem`) :
  - Copiée dans Product et Order
  - Placée dans `src/main/resources/`
  - Utilisée pour valider les JWT
---

## 5. Gestion des erreurs

| Cas                 | Code HTTP        |
| ------------------- | ---------------- |
| Token absent        | 401 Unauthorized |
| Token invalide      | 401 Unauthorized |
| Token expiré        | 403 Forbidden    |
| Accès refusé (rôle) | 403 Forbidden    |
