package org.population.ui.components;
import org.population.modele.Localite;
import org.population.ui.utils.Styles;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
        import java.util.List;

import static org.population.ui.utils.Styles.DIALOG_BUTTON_SIZE;
import static org.population.ui.utils.Styles.POLICE_TITRE;

public class TablePanel extends JPanel {
    private JTable tableLocalites;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel labelPopulationTotale;

    public TablePanel() {
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Liste des localités"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        createTable();
        createSearchPanel();
        createTotalPanel();
    }

    private void createTable() {
        String[] colonnes = {"Nom", "Population", "Superficie", "Type", "Densité", "Date"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableLocalites = new JTable(tableModel);
        configureTable();

        JScrollPane scrollPane = new JScrollPane(tableLocalites);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void configureTable() {
        Styles.styleTable(tableLocalites);
        int[] columnWidths = {150, 100, 100, 100, 100, 150};
        Styles.configureTableColumns(tableLocalites, columnWidths);
    }

    public void updateTable(List<Localite> localites) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        int populationTotale = 0;

        for (Localite localite : localites) {
            tableModel.addRow(new Object[]{
                    localite.getNom(),
                    localite.getPopulation(),
                    String.format("%.2f", localite.getSuperficie()),
                    localite.getType(),
                    String.format("%.2f", localite.calculerDensite()),
                    localite.getDateEnregistrement().format(formatter)
            });
            populationTotale += localite.getPopulation();
        }

        labelPopulationTotale.setText(String.format("Population totale: %,d habitants", populationTotale));
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Entrez le nom d'une localité...");
        searchPanel.add(new JLabel("Rechercher une localité:"));
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);
    }

    private void createTotalPanel() {
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        labelPopulationTotale = new JLabel("Population totale: 0 habitants");
        labelPopulationTotale.setFont(labelPopulationTotale.getFont().deriveFont(Font.BOLD));
        labelPopulationTotale.setFont(POLICE_TITRE.deriveFont(Font.BOLD));
        totalPanel.add(labelPopulationTotale);
        add(totalPanel, BorderLayout.SOUTH);
    }

    public void addSearchListener(DocumentListener listener) {
        searchField.getDocument().addDocumentListener(listener);
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public void requestSearchFocus() {
        searchField.requestFocusInWindow();
    }
}