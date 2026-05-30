# Application de Securite Sociale

Ce projet est le boilerplate d'une application REST Spring Boot de gestion de securite sociale pour l'ENSPY (CSI), gérant les assurés, les médecins (généralistes et spécialistes), les consultations, les prescriptions et les remboursements.

## Installation et configuration

### 1. Clonage du depot
Clonez le projet en local :
```bash
git clone https://github.com/PIO-VIA/securite_sociale.git
cd securite_sociale
```

### 2. Configuration de la base de donnees
Créez une base de données PostgreSQL nommée `securite_sociale_db`.

Configurez vos identifiants PostgreSQL locaux dans le fichier `.env` à la racine du projet :
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=securite_sociale_db
DB_USERNAME=votre_utilisateur
DB_PASSWORD=votre_mot_de_passe
```

### 3. Compilation et Packaging
Pour installer les dépendances et compiler le projet :
```bash
mvn clean compile
```

Si vous souhaitez builder et packager l'application en ignorant les tests (recommandé si la base de données locale n'est pas connectée pendant le build) :
```bash
mvn clean install -DskipTests
```

### 4. Lancement de l'application
Démarrez l'application Spring Boot :
```bash
mvn spring-boot:run
```

L'application démarre sur le port 8080.

## Documentation de l'API (Swagger UI)
Une fois l'application démarrée, vous pouvez visualiser et tester l'ensemble des endpoints sur la console Swagger :
- URL de la documentation : http://localhost:8080/docs
- Spécification OpenAPI JSON : http://localhost:8080/api-docs
