package org.population.ui.components;
import org.population.modele.Localite.TypePopulation;
import org.population.ui.utils.Styles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FormPanel extends JPanel {
    private JTextField champNom, champPopulation, champSuperficie;
    private JComboBox<TypePopulation> comboType;
    private JButton btnAjouter;

    public FormPanel() {
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createTitledBorder("Nouvelle localité"));

        JPanel form = createForm();
        add(form, BorderLayout.NORTH);
    }

    private JPanel createForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        champNom = new JTextField(20);
        champPopulation = new JTextField(20);
        champSuperficie = new JTextField(20);
        comboType = new JComboBox<>(TypePopulation.values());
        Styles.styleTextField(champNom);
        Styles.styleTextField(champPopulation);
        Styles.styleTextField(champSuperficie);
        btnAjouter = Styles.createStyledButton("Ajouter");

        // Ajout des composants
        addFormField(form, "Nom:", champNom, gbc, 0);
        addFormField(form, "Population:", champPopulation, gbc, 1);
        addFormField(form, "Superficie (km²):", champSuperficie, gbc, 2);
        addFormField(form, "Type:", comboType, gbc, 3);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 8;
        gbc.insets = new Insets(15, 0, 8, 0);
        form.add(btnAjouter, gbc);

        return form;
    }

    private void addFormField(JPanel panel, String label, JComponent field,
                              GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    public void addAjouterListener(ActionListener listener) {
        btnAjouter.addActionListener(listener);
    }

    public String getNom() { return champNom.getText(); }
    public String getPopulation() { return champPopulation.getText(); }
    public String getSuperficie() { return champSuperficie.getText(); }
    public TypePopulation getType() { return (TypePopulation) comboType.getSelectedItem(); }

    public void requestNewFocus() {
        champNom.requestFocusInWindow();
    }

    public void clear() {
        champNom.setText("");
        champPopulation.setText("");
        champSuperficie.setText("");
        comboType.setSelectedIndex(0);
    }
}