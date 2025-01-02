# Gestion de Population - Région de l'Extrême-Nord

Application de bureau Java permettant la gestion et l'analyse des données démographiques des localités de la région de l'Extrême-Nord.

## Fonctionnalités

- Ajout et gestion des localités avec leurs informations démographiques
- Visualisation des données sous forme de tableau avec tri et filtrage
- Statistiques en temps réel avec graphiques (densité et répartition)
- Export de rapports personnalisables (TXT, CSV, HTML)
- Interface utilisateur intuitive avec raccourcis clavier
- Persistance des données via base de données MySQL

## Prérequis

- Java JDK 17 ou supérieur
- Maven 3.8 ou supérieur
- MySQL 8.0 ou supérieur
- JFreeChart (géré par Maven)
- Swing (inclus dans le JDK)

## Installation

1. Clonez le dépôt :
```bash
git clone https://github.com/bounyamine/gestion_population
cd gestion-population
```

2. Configurez la base de données :
```sql
CREATE DATABASE population_en;
USE population_en;
```

3. Configurez la connexion à la base de données :
   - Ouvrez `resources/database.properties`
   - Modifiez les paramètres suivants :
```properties
db.url=jdbc:mysql://localhost:3306/population_en
db.user=votre_utilisateur
db.password=votre_mot_de_passe
```

4. Compilez le projet :
```bash
mvn install
```

## Lancement

Double-cliquez sur le fichier JAR généré ou exécutez :
```bash
java -jar target/Population-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Utilisation

### Interface principale

![Main App Screenshot](https://github.com/bounyamine/gestion_population/blob/main/resources/main.png)

- **Panneau gauche** : Formulaire d'ajout de localité
- **Centre** : Table des localités avec barre de recherche
- **Panneau droit** : Graphiques statistiques
- **Barre d'outils** : Actions principales et export

### Raccourcis clavier

- `Ctrl + N` : Nouveau
- `Ctrl + E` : Exporter
- `Ctrl + F` : Rechercher
- `F5` : Rafraîchir
- `F1` : Aide

### Export de rapports

1. Cliquez sur "Rapport de densité" dans la barre d'outils
2. Configurez les options :
   - Format de sortie (TXT, CSV, HTML)
   - Nombre de résultats
   - Options d'inclusion des statistiques
   - Groupement par type
3. Sélectionnez l'emplacement de sauvegarde

## Structure du projet

```
src/
├── main/
│   ├── java/
│   │   └── org/population/
│   │       ├── gestion/
│   │       ├── modele/
│   │       └── ui/
│   │           └── components/
│   │           └── utils/
│       └── application.properties
resources/
```

## Contribution

1. Fork du projet
2. Création d'une branche (`git checkout -b feature/amelioration`)
3. Commit des changements (`git commit -m 'Ajout de fonctionnalité'`)
4. Push vers la branche (`git push origin feature/amelioration`)
5. Création d'une Pull Request

## Tests

Exécutez les tests unitaires :
```bash
mvn test
```

## Dépendances principales

- JFreeChart : Création des graphiques statistiques
- MySQL Connector/J : Connexion à la base de données
- JUnit : Tests unitaires

## Auteur

BOUNYAMINE OUSMANOU

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.
