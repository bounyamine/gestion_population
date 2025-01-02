package org.population.service;

import org.population.modele.Localite;
import org.population.gestion.GestionPopulation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DatabaseUpdateService {
    private static final Logger logger = Logger.getLogger(DatabaseUpdateService.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<DatabaseUpdateListener> listeners = new ArrayList<>();
    private LocalDateTime lastCheckTimestamp;
    private final GestionPopulation gestion;
    private static final int POLLING_INTERVAL = 5; // secondes

    public DatabaseUpdateService(GestionPopulation gestion) {
        this.gestion = gestion;
        this.lastCheckTimestamp = LocalDateTime.now();
    }

    public interface DatabaseUpdateListener {
        void onDatabaseUpdate(List<Localite> updatedData);
    }

    public void addUpdateListener(DatabaseUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeUpdateListener(DatabaseUpdateListener listener) {
        listeners.remove(listener);
    }

    public void startMonitoring() {
        scheduler.scheduleAtFixedRate(this::checkForUpdates, 0, POLLING_INTERVAL, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void checkForUpdates() {
        try {
            Connection conn = gestion.getConnection();
            String sql = """
                SELECT COUNT(*) as changes
                FROM localites
                WHERE date_enregistrement > ?
                """;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setTimestamp(1, Timestamp.valueOf(lastCheckTimestamp));

                ResultSet rs = pstmt.executeQuery();
                if (rs.next() && rs.getInt("changes") > 0) {
                    List<Localite> updatedData = gestion.rechercherParNom("");
                    notifyListeners(updatedData);
                }
            }

            lastCheckTimestamp = LocalDateTime.now();
        } catch (SQLException e) {
            logger.warning("Erreur lors de la vérification des mises à jour: " + e.getMessage());
        }
    }

    private void notifyListeners(List<Localite> updatedData) {
        for (DatabaseUpdateListener listener : listeners) {
            try {
                listener.onDatabaseUpdate(updatedData);
            } catch (Exception e) {
                logger.warning("Erreur lors de la notification d'un listener: " + e.getMessage());
            }
        }
    }
}