package org.population.gestion;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import org.population.modele.Localite;
import org.population.modele.Localite.TypePopulation;
import org.population.modele.LocaliteException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.*;

public class GestionPopulation implements AutoCloseable {
    private List<Localite> localites;
    private Connection connexionDB;
    private static final Logger logger = Logger.getLogger(GestionPopulation.class.getName());

    public GestionPopulation() throws SQLException {
        localites = new ArrayList<>();
        initializeDatabase();
        chargerDonnees();
    }

    // Initialisation de la base de données sql
    private void initializeDatabase() throws SQLException {
        try {
            Properties props = new Properties();
            // Chargement du fichier depuis le chemin absolu
            String path = "src/main/resources/database.properties";
            FileInputStream fis = new FileInputStream(path);
            props.load(fis);

            connexionDB = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );
            createTable();
        } catch (IOException e) {
            logger.severe("Erreur lors du chargement des propriétés de la base de données: " + e.getMessage());
            throw new SQLException("Impossible de se connecter à la base de données", e);
        }
    }

    // Création de la table des localités si elle n'existe pas encore
    private void createTable() throws SQLException {
        try (Statement stmt = connexionDB.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS localites (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    nom VARCHAR(100) NOT NULL UNIQUE,
                    population INT NOT NULL,
                    superficie DOUBLE NOT NULL,
                    type VARCHAR(20) NOT NULL,
                    date_enregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.execute(sql);
        }
    }

    // Chargement des données des localités
    private void chargerDonnees() throws SQLException {
        String sql = "SELECT * FROM localites";
        try (PreparedStatement pstmt = connexionDB.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Localite localite = new Localite(
                            rs.getString("nom"),
                            rs.getInt("population"),
                            rs.getDouble("superficie"),
                            TypePopulation.valueOf(rs.getString("type"))
                    );
                    localites.add(localite);
                } catch (LocaliteException e) {
                    logger.warning("Erreur lors du chargement de la localité: " + e.getMessage());
                }
            }
        }
    }

    // Ajouter une localité
    public void ajouterLocalite(Localite localite) throws SQLException {
        // Vérification des doublons
        if (localites.stream().anyMatch(l -> l.getNom().equalsIgnoreCase(localite.getNom()))) {
            throw new SQLException("Une localité avec ce nom existe déjà");
        }

        String sql = """
            INSERT INTO localites (nom, population, superficie, type, date_enregistrement)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connexionDB.prepareStatement(sql)) {
            pstmt.setString(1, localite.getNom());
            pstmt.setInt(2, localite.getPopulation());
            pstmt.setDouble(3, localite.getSuperficie());
            pstmt.setString(4, localite.getType().toString());
            pstmt.setTimestamp(5, Timestamp.valueOf(localite.getDateEnregistrement()));

            pstmt.executeUpdate();
            localites.add(localite);
        }
    }

    // Réchercher les localités par leurs noms
    public List<Localite> rechercherParNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return new ArrayList<>(localites); // Retourne toutes les localités si aucun nom n'est spécifié
        }

        return localites.stream()
                .filter(l -> l.getNom().toLowerCase().contains(nom.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    // Format de sortie pour le rapport de densité
    public enum FormatRapport {
        TXT, CSV, HTML
    }

    // Configuration pour la génération du rapport
    public static class ConfigurationRapport {
        private final int limiteResultats;
        private final FormatRapport format;
        private final boolean inclureStatistiques;
        private final boolean grouperParType;
        private final String fichierSortie;

        public ConfigurationRapport(Builder builder) {
            limiteResultats = builder.limiteResultats;
            format = builder.format;
            inclureStatistiques = builder.inclureStatistiques;
            grouperParType = builder.grouperParType;
            fichierSortie = builder.fichierSortie;
        }

        public static class Builder {
            private int limiteResultats = Integer.MAX_VALUE;
            private FormatRapport format = FormatRapport.TXT;
            private boolean inclureStatistiques = true;
            private boolean grouperParType = false;
            private String fichierSortie;

            public Builder(String fichierSortie) {
                this.fichierSortie = fichierSortie;
            }

            public Builder limiteResultats(int limite) {
                this.limiteResultats = limite;
                return this;
            }

            public Builder format(FormatRapport format) {
                this.format = format;
                return this;
            }

            public Builder inclureStatistiques(boolean inclure) {
                this.inclureStatistiques = inclure;
                return this;
            }

            public Builder grouperParType(boolean grouper) {
                this.grouperParType = grouper;
                return this;
            }

            public ConfigurationRapport build() {
                return new ConfigurationRapport(this);
            }
        }
    }

    // Génerer un rapport détaillé des localités selon leur densité de population et selon format de sorti.
    public void genererRapportDensite(ConfigurationRapport config) throws IOException {
        // Préparer les données
        Map<TypePopulation, List<Localite>> localitesParType = localites.stream()
                .sorted((l1, l2) -> Double.compare(l2.calculerDensite(), l1.calculerDensite()))
                .limit(config.limiteResultats)
                .collect(Collectors.groupingBy(
                        Localite::getType,
                        Collectors.toList()
                ));

        switch (config.format) {
            case TXT -> genererRapportTXT(config, localitesParType);
            case CSV -> genererRapportCSV(config, localitesParType);
            case HTML -> genererRapportHTML(config, localitesParType);
        }
    }

    // Génerer un rapport txt
    private void genererRapportTXT(ConfigurationRapport config, Map<TypePopulation, List<Localite>> localitesParType)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(config.fichierSortie))) {
            // En-tête
            writer.println("RAPPORT DES DENSITÉS DE POPULATION");
            writer.println("=================================");
            writer.printf("Date du rapport: %s%n%n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            if (config.inclureStatistiques) {
                ajouterStatistiquesTXT(writer, localitesParType);
            }

            // Détails des localités
            if (config.grouperParType) {
                for (TypePopulation type : TypePopulation.values()) {
                    List<Localite> localites = localitesParType.getOrDefault(type, new ArrayList<>());
                    writer.printf("%n%s%n", type);
                    writer.println("-".repeat(type.toString().length()));
                    afficherLocalitesTXT(writer, localites);
                }
            } else {
                List<Localite> toutesLocalites = localitesParType.values().stream()
                        .flatMap(List::stream)
                        .sorted((l1, l2) -> Double.compare(l2.calculerDensite(), l1.calculerDensite()))
                        .collect(Collectors.toList());
                afficherLocalitesTXT(writer, toutesLocalites);
            }
        }
    }

    private void genererRapportCSV(ConfigurationRapport config, Map<TypePopulation, List<Localite>> localitesParType)
            throws IOException {
        // Utiliser un BufferedWriter avec l'encodage UTF-8 et BOM pour Excel
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(config.fichierSortie),
                        StandardCharsets.UTF_8))) {

            // Ajouter le BOM pour Excel
            writer.write('\ufeff');

            // Fonction pour échapper les champs CSV
            Function<String, String> escapeCSV = (String field) -> {
                if (field == null) {
                    return "";
                }
                // Échapper les guillemets et entourer de guillemets si nécessaire
                if (field.contains("\"") || field.contains(",") || field.contains("\n")) {
                    return "\"" + field.replace("\"", "\"\"") + "\"";
                }
                return field;
            };

            // En-tête CSV
            String[] headers = {"Type", "Nom", "Population", "Superficie (km²)", "Densité (hab/km²)", "Date d'enregistrement"};
            writer.write(String.join(";", headers));
            writer.newLine();

            // Fonction pour formater les nombres
            DecimalFormat df = new DecimalFormat("#,##0.00");
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.FRANCE));

            // Trier et écrire les données
            localitesParType.values().stream()
                    .flatMap(List::stream)
                    .sorted((l1, l2) -> Double.compare(l2.calculerDensite(), l1.calculerDensite()))
                    .forEach(l -> {
                        try {
                            String[] fields = {
                                    escapeCSV.apply(l.getType().toString()),
                                    escapeCSV.apply(l.getNom()),
                                    String.valueOf(l.getPopulation()),
                                    df.format(l.getSuperficie()),
                                    df.format(l.calculerDensite()),
                                    l.getDateEnregistrement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            };
                            writer.write(String.join(";", fields));
                            writer.newLine();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    private void genererRapportHTML(ConfigurationRapport config, Map<TypePopulation, List<Localite>> localitesParType)
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(config.fichierSortie))) {
            writer.println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Rapport des densités de population</title>
                    <style>
                        :root {
                            --primary-color: #2563eb;
                            --secondary-color: #1e40af;
                            --background-color: #f8fafc;
                            --text-color: #1e293b;
                        }
                        
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        
                        body { 
                            font-family: 'Segoe UI', system-ui, sans-serif;
                            line-height: 1.6;
                            color: var(--text-color);
                            background-color: var(--background-color);
                            margin: 0;
                            padding: 2rem;
                        }
                        
                        .container {
                            max-width: 1200px;
                            margin: 0 auto;
                            padding: 2rem;
                            background-color: white;
                            border-radius: 1rem;
                            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
                        }
                        
                        h1 {
                            color: var(--primary-color);
                            font-size: 2.5rem;
                            margin-bottom: 2rem;
                            text-align: center;
                        }
                        
                        table { 
                            width: 100%;
                            border-collapse: collapse;
                            margin: 2rem 0;
                            background-color: white;
                            border-radius: 0.5rem;
                            overflow: hidden;
                            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
                        }
                        
                        th { 
                            background-color: var(--primary-color);
                            color: white;
                            font-weight: 600;
                            padding: 1rem;
                            text-align: left;
                        }
                        
                        td {
                            padding: 1rem;
                            border-bottom: 1px solid #e2e8f0;
                        }
                        
                        tr:hover {
                            background-color: #f1f5f9;
                        }
                        
                        .stats {
                            background-color: var(--primary-color);
                            color: white;
                            padding: 2rem;
                            border-radius: 0.5rem;
                            margin: 2rem 0;
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                            gap: 2rem;
                        }
                        
                        .stat-card {
                            background-color: rgba(255, 255, 255, 0.1);
                            padding: 1.5rem;
                            border-radius: 0.5rem;
                            text-align: center;
                        }
                        
                        .stat-number {
                            font-size: 2rem;
                            font-weight: bold;
                            margin-bottom: 0.5rem;
                        }
                        
                        .type-header {
                            color: var(--secondary-color);
                            font-size: 1.5rem;
                            margin: 2rem 0 1rem 0;
                            padding-bottom: 0.5rem;
                            border-bottom: 2px solid var(--primary-color);
                        }
                        
                        @media (max-width: 768px) {
                            body {
                                padding: 1rem;
                            }
                            
                            .container {
                                padding: 1rem;
                            }
                            
                            table {
                                display: block;
                                overflow-x: auto;
                            }
                            
                            .stats {
                                grid-template-columns: 1fr;
                            }
                        }
                    </style>
                </head>
                <body>
                """);

            writer.println("<h1>Rapport des densités de population</h1>");
            writer.printf("<p>Généré le %s</p>%n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            if (config.inclureStatistiques) {
                ajouterStatistiquesHTML(writer, localitesParType);
            }

            if (config.grouperParType) {
                for (TypePopulation type : TypePopulation.values()) {
                    List<Localite> localites = localitesParType.getOrDefault(type, new ArrayList<>());
                    writer.printf("<h2 class=\"type-header\">%s</h2>%n", type);
                    afficherLocalitesHTML(writer, localites);
                }
            } else {
                List<Localite> toutesLocalites = localitesParType.values().stream()
                        .flatMap(List::stream)
                        .sorted((l1, l2) -> Double.compare(l2.calculerDensite(), l1.calculerDensite()))
                        .collect(Collectors.toList());
                afficherLocalitesHTML(writer, toutesLocalites);
            }

            writer.println("</body></html>");
        } catch (IOException e) {
            logger.severe("Erreur lors de la génération du rapport HTML: " + e.getMessage());
            throw e;
        }
    }

    private void ajouterStatistiquesTXT(PrintWriter writer, Map<TypePopulation, List<Localite>> localitesParType) {
        DoubleSummaryStatistics stats = localitesParType.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Localite::calculerDensite)
                .summaryStatistics();

        writer.println("STATISTIQUES GÉNÉRALES");
        writer.println("-----------------------");
        writer.printf("Nombre total de localités: %d%n", stats.getCount());
        writer.printf("Densité moyenne: %.2f hab/km²%n", stats.getAverage());
        writer.printf("Densité maximale: %.2f hab/km²%n", stats.getMax());
        writer.printf("Densité minimale: %.2f hab/km²%n", stats.getMin());
        writer.println();
    }

    private void ajouterStatistiquesHTML(PrintWriter writer, Map<TypePopulation, List<Localite>> localitesParType) {
        DoubleSummaryStatistics stats = localitesParType.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Localite::calculerDensite)
                .summaryStatistics();

        writer.println("<div class=\"stats\">");
        writer.println("<h2>Statistiques générales</h2>");
        writer.println("<ul>");
        writer.printf("<li>Nombre total de localités: %d</li>%n", stats.getCount());
        writer.printf("<li>Densité moyenne: %.2f hab/km²</li>%n", stats.getAverage());
        writer.printf("<li>Densité maximale: %.2f hab/km²</li>%n", stats.getMax());
        writer.printf("<li>Densité minimale: %.2f hab/km²</li>%n", stats.getMin());
        writer.println("</ul>");
        writer.println("</div>");
    }

    private void afficherLocalitesTXT(PrintWriter writer, List<Localite> localites) {
        writer.printf("%-30s %-15s %-15s %-15s %-15s%n",
                "Nom", "Population", "Superficie", "Densité", "Type");
        writer.println("-".repeat(90));

        for (Localite localite : localites) {
            writer.printf("%-30s %-15d %-15.2f %-15.2f %-15s%n",
                    localite.getNom(),
                    localite.getPopulation(),
                    localite.getSuperficie(),
                    localite.calculerDensite(),
                    localite.getType()
            );
        }
    }

    private void afficherLocalitesHTML(PrintWriter writer, List<Localite> localites) {
        writer.println("<table>");
        writer.println("<tr>");
        writer.println("<th>Nom</th>");
        writer.println("<th>Population</th>");
        writer.println("<th>Superficie</th>");
        writer.println("<th>Densité</th>");
        writer.println("<th>Type</th>");
        writer.println("</tr>");

        for (Localite localite : localites) {
            writer.println("<tr>");
            writer.printf("<td>%s</td>%n", localite.getNom());
            writer.printf("<td>%d</td>%n", localite.getPopulation());
            writer.printf("<td>%.2f</td>%n", localite.getSuperficie());
            writer.printf("<td>%.2f</td>%n", localite.calculerDensite());
            writer.printf("<td>%s</td>%n", localite.getType());
            writer.println("</tr>");
        }

        writer.println("</table>");
    }

    public Map<TypePopulation, DoubleSummaryStatistics> analyserParType() {
        return localites.stream()
                .collect(Collectors.groupingBy(
                        Localite::getType,
                        Collectors.summarizingDouble(Localite::calculerDensite)
                ));
    }

    @Override
    public void close() {
        try {
            if (connexionDB != null && !connexionDB.isClosed()) {
                connexionDB.close();
            }
        } catch (SQLException e) {
            logger.warning("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

}