package org.population;
import org.population.service.DatabaseUpdateService;
import org.population.ui.components.*;
import org.population.ui.utils.Styles;
import org.population.modele.*;
import org.population.modele.Localite.TypePopulation;
import org.population.gestion.GestionPopulation;
import org.population.gestion.GestionPopulation.ConfigurationRapport;
import org.population.gestion.GestionPopulation.FormatRapport;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

import static org.population.ui.utils.Styles.*;
import static org.population.ui.utils.Styles.Colors.ACCENT;
import static org.population.ui.utils.Styles.Colors.TEXT;
import static org.population.ui.utils.Styles.Fonts.BASE;
import static org.population.ui.utils.Styles.Fonts.SUBTITLE;

public class Main extends JFrame {
    private GestionPopulation gestion;
    private FormPanel formPanel;
    private TablePanel tablePanel;
    private StatsPanel statsPanel;
    private DatabaseUpdateService updateService;

    public Main() {
        try {
            gestion = new GestionPopulation();
            updateService = new DatabaseUpdateService(gestion);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            initUI();
            setupRealTimeUpdates();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    private void setupRealTimeUpdates() {
        updateService.addUpdateListener(updatedData -> {
            SwingUtilities.invokeLater(() -> {
                tablePanel.updateTable(updatedData);
                Map<TypePopulation, DoubleSummaryStatistics> stats = gestion.analyserParType();
                statsPanel.updateCharts(stats);
            });
        });

        updateService.startMonitoring();
    }

    @Override
    public void dispose() {
        updateService.stopMonitoring();
        super.dispose();
    }

    private void initUI() {
        setTitle("Gestion de la Population - Région de l'Extrême-Nord");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        formPanel = new FormPanel();
        tablePanel = new TablePanel();
        statsPanel = new StatsPanel();

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(formPanel, BorderLayout.WEST);
        content.add(tablePanel, BorderLayout.CENTER);
        content.add(statsPanel, BorderLayout.EAST);

        add(content, BorderLayout.CENTER);
        add(createToolBar(), BorderLayout.NORTH);
        add(createStatusBar(), BorderLayout.SOUTH);

        refreshData();
        setupEventHandlers();
    }

    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton btnExporter = Styles.createStyledButton("Rapport de densité");
        JButton btnRafraichir = Styles.createStyledButton("Rafraîchir");
        JButton btnAide = Styles.createStyledButton("Aide");

        btnExporter.addActionListener(e -> exporterRapportDensite());
        btnRafraichir.addActionListener(e -> refreshData());
        btnAide.addActionListener(e -> showHelp());

        toolbar.add(btnExporter);
        toolbar.add(btnRafraichir);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(btnAide);

        return toolbar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        JLabel statusLabel = new JLabel(" Prêt");
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private void setupEventHandlers() {
        formPanel.addAjouterListener(e -> ajouterLocalite());

        tablePanel.addSearchListener(new DocumentListener() {
            private void searchLocalites() {
                String searchText = tablePanel.getSearchText();
                List<Localite> results = gestion.rechercherParNom(searchText);
                tablePanel.updateTable(results);
            }

            @Override
            public void insertUpdate(DocumentEvent e) { searchLocalites(); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchLocalites(); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchLocalites(); }
        });

        setupKeyboardShortcuts();
    }

    private void ajouterLocalite() {
        try {
            String nom = formPanel.getNom();
            int population = Integer.parseInt(formPanel.getPopulation());
            double superficie = Double.parseDouble(formPanel.getSuperficie());
            TypePopulation type = formPanel.getType();

            Localite localite = new Localite(nom, population, superficie, type);
            gestion.ajouterLocalite(localite);
            refreshData();
            updateService.checkForUpdates();
            formPanel.clear();

            JOptionPane.showMessageDialog(this,
                    "Localité ajoutée avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        List<Localite> localites = gestion.rechercherParNom("");
        tablePanel.updateTable(localites);
        Map<TypePopulation, DoubleSummaryStatistics> stats = gestion.analyserParType();
        statsPanel.updateCharts(stats);
    }

    private void setupKeyboardShortcuts() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "search");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "export");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");

        actionMap.put("search", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tablePanel.requestSearchFocus();
            }
        });

        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formPanel.requestNewFocus();
            }
        });

        actionMap.put("export", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exporterRapportDensite();
            }
        });

        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });

        actionMap.put("help", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
    }

    private void handleInitializationError(Exception e) {
        JOptionPane.showMessageDialog(this,
                "Erreur d'initialisation: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void showHelp() {
        JDialog helpDialog = new JDialog(this, "Aide", true);
        helpDialog.setLayout(new BorderLayout(10, 10));
        Styles.styleDialogPanel((JPanel)helpDialog.getContentPane());

        JTabbedPane tabbedPane = new JTabbedPane();
        Styles.styleTabbedPane(tabbedPane);

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        generalPanel.setBorder(BorderFactory.createEmptyBorder(Styles.DIALOG_PADDING,
                Styles.DIALOG_PADDING,
                Styles.DIALOG_PADDING,
                Styles.DIALOG_PADDING));
        Styles.styleDialogPanel(generalPanel);

        generalPanel.add(createHelpSection("Ajouter une localité",
                """
                1. Remplissez le formulaire à gauche
                2. Assurez-vous que tous les champs sont valides:
                   - Le nom ne doit pas être vide
                   - La population doit être un nombre positif
                   - La superficie doit être un nombre positif
                3. Sélectionnez le type de population
                4. Cliquez sur "Ajouter"
                """));

        generalPanel.add(createHelpSection("Gérer les données",
                """
                1. Utilisez la table centrale pour visualiser les données
                2. Triez les colonnes en cliquant sur leurs en-têtes
                3. Utilisez la barre de recherche pour filtrer les localités
                4. Clic droit sur une ligne pour plus d'options
                """));

        generalPanel.add(createHelpSection("Statistiques",
                """
                1. Consultez le panneau de droite pour les statistiques
                2. Les statistiques sont mises à jour automatiquement
                3. Exportez les données via la barre d'outils
                """));

        JPanel shortcutsPanel = new JPanel();
        shortcutsPanel.setLayout(new BoxLayout(shortcutsPanel, BoxLayout.Y_AXIS));
        shortcutsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        shortcutsPanel.add(createHelpSection("Raccourcis clavier",
                """
                Ctrl + N : Nouveau
                Ctrl + E : Exporter
                Ctrl + F : Rechercher
                F5      : Rafraîchir
                F1      : Aide
                """));

        tabbedPane.addTab("Général", new JScrollPane(generalPanel));
        tabbedPane.addTab("Raccourcis", new JScrollPane(shortcutsPanel));

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> helpDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        helpDialog.add(tabbedPane, BorderLayout.CENTER);
        helpDialog.add(buttonPanel, BorderLayout.SOUTH);

        helpDialog.setSize(500, 600);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setVisible(true);
    }

    public static JPanel createHelpSection(String title, String content) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Configuration de la bordure avec titre
        Border titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                SUBTITLE,
                TEXT
        );

        panel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Configuration de la zone de texte
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setBackground(null);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(BASE);
        textArea.setForeground(TEXT);
        Styles.styleHelpTextArea(textArea);

        panel.add(textArea);
        return panel;
    }

    private void exporterRapportDensite() {
        // Créer un dialogue personnalisé pour les options d'export
        JDialog optionsDialog = new JDialog(this, "Options d'export", true);
        optionsDialog.setLayout(new BorderLayout());

        // Panel principal avec GridBagLayout pour un alignement précis
        JPanel mainPanel = new JPanel(new GridBagLayout());
        Styles.styleDialogPanel(mainPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Format de sortie
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Format de sortie :"), gbc);

        JComboBox<FormatRapport> formatCombo = new JComboBox<>(FormatRapport.values());
        gbc.gridx = 1;
        mainPanel.add(formatCombo, gbc);

        // Nombre de résultats
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Nombre de résultats :"), gbc);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 1, 1000, 1);
        JSpinner limiteSpinner = new JSpinner(spinnerModel);
        gbc.gridx = 1;
        mainPanel.add(limiteSpinner, gbc);

        // Options supplémentaires
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JCheckBox statsCheckBox = new JCheckBox("Inclure les statistiques", true);
        mainPanel.add(statsCheckBox, gbc);

        gbc.gridy = 3;
        JCheckBox grouperCheckBox = new JCheckBox("Grouper par type de population", true);
        mainPanel.add(grouperCheckBox, gbc);

        // Panel des boutons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        Styles.styleDialogButton(okButton, true);
        Styles.styleDialogButton(cancelButton, false);

        // Créer et styler le panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Styles.styleDialogPanel(buttonPanel);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Ajouter les panels au dialogue
        optionsDialog.add(mainPanel, BorderLayout.CENTER);
        optionsDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Variable pour stocker le choix de l'utilisateur
        final boolean[] approved = {false};

        // Gestionnaires d'événements
        cancelButton.addActionListener(e -> optionsDialog.dispose());

        okButton.addActionListener(e -> {
            approved[0] = true;
            optionsDialog.dispose();
        });

        // Ajuster la taille et la position du dialogue
        optionsDialog.pack();
        optionsDialog.setLocationRelativeTo(this);
        optionsDialog.setResizable(false);
        optionsDialog.setVisible(true);

        // Si l'utilisateur a validé, procéder à l'export
        if (approved[0]) {
            JFileChooser chooser = new JFileChooser();
            FormatRapport format = (FormatRapport) formatCombo.getSelectedItem();

            // Configurer le filtre selon le format choisi
            assert format != null;
            String extension = switch (format) {
                case TXT -> "txt";
                case CSV -> "csv";
                case HTML -> "html";
            };

            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Fichiers " + extension.toUpperCase() + " (*." + extension + ")",
                    extension
            );
            chooser.setFileFilter(filter);

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    // Construire le nom de fichier avec l'extension appropriée
                    String fichier = chooser.getSelectedFile().getAbsolutePath();
                    if (!fichier.toLowerCase().endsWith("." + extension)) {
                        fichier += "." + extension;
                    }

                    // Créer la configuration du rapport
                    ConfigurationRapport config = new ConfigurationRapport.Builder(fichier)
                            .format(format)
                            .limiteResultats((Integer) limiteSpinner.getValue())
                            .inclureStatistiques(statsCheckBox.isSelected())
                            .grouperParType(grouperCheckBox.isSelected())
                            .build();

                    // Générer le rapport
                    gestion.genererRapportDensite(config);

                    // Afficher message de succès avec option pour ouvrir le fichier
                    int choice = JOptionPane.showOptionDialog(this,
                            "Rapport généré avec succès dans " + fichier + "\nVoulez-vous ouvrir le fichier ?",
                            "Export réussi",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            new String[]{"Ouvrir", "Fermer"},
                            "Fermer");

                    if (choice == 0) {
                        Desktop.getDesktop().open(new File(fichier));
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la génération du rapport : " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Une erreur inattendue est survenue : " + ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}