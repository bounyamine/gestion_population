package org.population.modele;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Localite {
    private String nom;
    private int population;
    private double superficie;
    private TypePopulation type;
    private LocalDateTime dateEnregistrement;

    public enum TypePopulation {
        URBAINE,
        RURALE
    }

    public Localite(String nom, int population, double superficie, TypePopulation type) throws LocaliteException {
        validerDonnees(nom, population, superficie);
        this.nom = nom;
        this.population = population;
        this.superficie = superficie;
        this.type = type;
        this.dateEnregistrement = LocalDateTime.now();
    }

    // Vérirication des données
    private void validerDonnees(String nom, int population, double superficie) throws LocaliteException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new LocaliteException("Le nom de la localité ne peut pas être vide");
        }
        if (population < 0) {
            throw new LocaliteException("La population ne peut pas être négative");
        }
        if (superficie <= 0) {
            throw new LocaliteException("La superficie doit être positive");
        }
    }

    // Getters
    public String getNom() { return nom; }
    public int getPopulation() { return population; }
    public double getSuperficie() { return superficie; }
    public TypePopulation getType() { return type; }
    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; }

    public double calculerDensite() {
        return population / superficie;
    }

    @Override
    public String toString() {
        return String.format("""
            Localité: %s
            Population: %d habitants
            Superficie: %.2f km²
            Type: %s
            Densité: %.2f hab/km²
            Date d'enregistrement: %s
            """,
                nom,
                population,
                superficie,
                type,
                calculerDensite(),
                dateEnregistrement.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }
}