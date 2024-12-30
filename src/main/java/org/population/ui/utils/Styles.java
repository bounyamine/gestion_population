package org.population.ui.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;

/**
 * Classe utilitaire pour la gestion des styles de l'interface utilisateur.
 * Fournit des méthodes et constantes pour maintenir une apparence cohérente.
 */
public class Styles {

    // Couleurs des textes
    public static final Color COULEUR_PRIMAIRE = new Color(137, 201, 99);
    public static final Color COULEUR_SECONDAIRE = new Color(90, 91, 220);
    public static final Color COULEUR_ACCENT = new Color(59, 130, 246);
    public static final Color COULEUR_TEXTE = new Color(30, 41, 59);
    public static final Color COULEUR_ERREUR = new Color(239, 68, 68);
    public static final Color COULEUR_SUCCES = new Color(34, 197, 94);
    public static final Color COULEUR_AVERTISSEMENT = new Color(234, 179, 8);

    // Couleurs pour les dialogues et panneaux d'aide
    public static final Color COULEUR_FOND_DIALOG = new Color(255, 255, 255);
    public static final Color COULEUR_BORDURE_SECTION = new Color(226, 232, 240);
    public static final Color COULEUR_FOND_ONGLET = new Color(248, 250, 252);
    public static final Color COULEUR_ONGLET_ACTIF = new Color(59, 130, 246);
    public static final Color COULEUR_TEXTE_ONGLET = new Color(71, 85, 105);
    public static final Color COULEUR_TEXTE_AIDE = new Color(51, 65, 85);

    // Nouvelles couleurs pour les tableaux
    public static final Color COULEUR_TABLEAU_LIGNE_ALTERNEE = new Color(241, 245, 249);
    public static final Color COULEUR_TABLEAU_SELECTION = new Color(37, 99, 235, 180);
    public static final Color COULEUR_TABLEAU_GRILLE = new Color(226, 232, 240);
    public static final Color COULEUR_TABLEAU_ENTETE = new Color(248, 250, 252);

    // Configuration des polices
    public static final Font POLICE_PRINCIPALE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font POLICE_TITRE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font POLICE_SOUS_TITRE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font POLICE_TABLEAU = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font POLICE_TABLEAU_ENTETE = new Font("Segoe UI", Font.BOLD, 13);

    // Polices pour les dialogues
    public static final Font POLICE_DIALOG_TITRE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font POLICE_DIALOG_SECTION = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font POLICE_DIALOG_CONTENU = new Font("Segoe UI", Font.PLAIN, 12);

    // Constantes de dimensions
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_MIN_WIDTH = 100;
    private static final int BUTTON_PREF_WIDTH = 120;
    private static final int BUTTON_MAX_WIDTH = 150;
    private static final int PADDING = 10;
    private static final int ROW_HEIGHT = 30;

    // Constantes de dimensions pour les dialogues
    public static final int DIALOG_PADDING = 15;
    public static final int SECTION_PADDING = 10;
    public static final int ONGLET_HEIGHT = 35;
    public static final int SECTION_SPACING = 20;
    public static final Dimension DIALOG_BUTTON_SIZE = new Dimension(100, 35);
    public static final int DIALOG_BORDER_RADIUS = 8;

    // Styles de bordures réutilisables
    public static final Border BORDURE_SECTION = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COULEUR_BORDURE_SECTION),
            BorderFactory.createEmptyBorder(SECTION_PADDING, SECTION_PADDING, SECTION_PADDING, SECTION_PADDING)
    );

    public static final Border BORDURE_DIALOG = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COULEUR_ACCENT, 1),
            BorderFactory.createEmptyBorder(DIALOG_PADDING, DIALOG_PADDING, DIALOG_PADDING, DIALOG_PADDING)
    );

    /**
     * Crée un bouton stylisé avec le texte spécifié.
     * @param text Le texte du bouton
     * @return JButton Un bouton stylisé
     */
    public static JButton createStyledButton(String text) {
        return createStyledButton(text, COULEUR_ACCENT, COULEUR_TEXTE);
    }

    /**
     * Crée un bouton stylisé avec des couleurs personnalisées.
     * @param text Le texte du bouton
     * @param backgroundColor Couleur de fond
     * @param textColor Couleur du texte
     * @return JButton Un bouton stylisé
     */
    public static JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        styleButton(button, backgroundColor, textColor);
        return button;
    }

    /**
     * Applique un style à un bouton existant.
     * @param button Le bouton à styler
     * @param backgroundColor Couleur de fond
     * @param textColor Couleur du texte
     */
    public static void styleButton(JButton button, Color backgroundColor, Color textColor) {
        // Configuration des dimensions
        button.setPreferredSize(new Dimension(BUTTON_PREF_WIDTH, BUTTON_HEIGHT));
        button.setMinimumSize(new Dimension(BUTTON_MIN_WIDTH, BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(BUTTON_MAX_WIDTH, BUTTON_HEIGHT));

        // Style du bouton
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(POLICE_PRINCIPALE.deriveFont(Font.BOLD));

        // Création d'une bordure arrondie
        Border roundedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(PADDING/2, PADDING, PADDING/2, PADDING)
        );
        button.setBorder(roundedBorder);

        // Paramètres d'apparence
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Gestionnaire d'événements pour les effets visuels
        ButtonStateHandler stateHandler = new ButtonStateHandler(button, backgroundColor);
        button.addMouseListener(stateHandler);
    }

    /**
     * Applique un style à un champ de texte.
     * @param textField Le champ de texte à styler
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(POLICE_PRINCIPALE);
        textField.setBackground(Color.WHITE);
        textField.setForeground(COULEUR_PRIMAIRE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COULEUR_SECONDAIRE),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COULEUR_ACCENT),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COULEUR_SECONDAIRE),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
        });
    }

    /**
     * Applique un style à un panneau.
     * @param panel Le panneau à styler
     */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(COULEUR_PRIMAIRE);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
    }

    /**
     * Applique un style complet à une JTable.
     * @param table La table à styler
     */
    public static void styleTable(JTable table) {
        // Configuration générale
        table.setFont(POLICE_TABLEAU);
        table.setRowHeight(ROW_HEIGHT);
        table.setBackground(COULEUR_PRIMAIRE);
        table.setForeground(COULEUR_TEXTE);
        table.setSelectionBackground(COULEUR_TABLEAU_SELECTION);
        table.setSelectionForeground(COULEUR_TEXTE);
        table.setGridColor(COULEUR_TABLEAU_GRILLE);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);

        // Configuration de l'en-tête
        styleTableHeader(table.getTableHeader());

        // Configuration des lignes alternées
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? COULEUR_PRIMAIRE : COULEUR_TABLEAU_LIGNE_ALTERNEE);
                }

                // Ajout d'un padding interne aux cellules
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        });

        // Configuration de la sélection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
    }

    /**
     * Style l'en-tête d'une table.
     * @param header L'en-tête de la table à styler
     */
    public static void styleTableHeader(JTableHeader header) {
        header.setBackground(COULEUR_TABLEAU_ENTETE);
        header.setForeground(COULEUR_TEXTE);
        header.setFont(POLICE_TABLEAU_ENTETE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COULEUR_ACCENT));
        header.setReorderingAllowed(false);

        // Style personnalisé pour les cellules d'en-tête
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(COULEUR_TABLEAU_ENTETE);
                c.setFont(POLICE_TABLEAU_ENTETE);
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return c;
            }
        });
    }

    /**
     * Configure les colonnes d'une table avec des largeurs optimisées.
     * @param table La table à configurer
     * @param columnWidths Tableau des largeurs préférées pour chaque colonne
     */
    public static void configureTableColumns(JTable table, int[] columnWidths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < Math.min(columnWidths.length, columnModel.getColumnCount()); i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    /**
     * Style le conteneur de défilement d'une table.
     * @param scrollPane Le conteneur de défilement à styler
     */
    public static void styleTableScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(COULEUR_TABLEAU_GRILLE));
        scrollPane.getViewport().setBackground(COULEUR_PRIMAIRE);

        // Style de la barre de défilement
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(COULEUR_PRIMAIRE);
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COULEUR_SECONDAIRE;
                this.trackColor = COULEUR_PRIMAIRE;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    /**
     * Style un JTabbedPane pour les dialogues.
     * @param tabbedPane Le JTabbedPane à styler
     */
    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setFont(POLICE_DIALOG_SECTION);
        tabbedPane.setBackground(COULEUR_FOND_DIALOG);
        tabbedPane.setForeground(COULEUR_TEXTE_ONGLET);

        // Style des onglets
        UIManager.put("TabbedPane.selected", COULEUR_ONGLET_ACTIF);
        UIManager.put("TabbedPane.contentAreaColor", COULEUR_FOND_DIALOG);
        UIManager.put("TabbedPane.tabAreaBackground", COULEUR_FOND_ONGLET);
        UIManager.put("TabbedPane.focus", COULEUR_FOND_DIALOG);

        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return ONGLET_HEIGHT;
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(COULEUR_ONGLET_ACTIF);
                    g.fillRect(x, y + h - 2, w, 2);
                }
            }
        });
    }

    /**
     * Style une zone de texte pour l'aide.
     * @param textArea La JTextArea à styler
     */
    public static void styleHelpTextArea(JTextArea textArea) {
        textArea.setFont(POLICE_DIALOG_CONTENU);
        textArea.setForeground(COULEUR_TEXTE_AIDE);
        textArea.setBackground(COULEUR_FOND_DIALOG);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setMargin(new Insets(5, 5, 5, 5));
    }

    /**
     * Style un panneau de dialogue.
     * @param panel Le JPanel à styler
     */
    public static void styleDialogPanel(JPanel panel) {
        panel.setBackground(COULEUR_FOND_DIALOG);
        panel.setBorder(BORDURE_DIALOG);
    }

    /**
     * Style un bouton de dialogue.
     * @param button Le JButton à styler
     * @param isDefault true si c'est le bouton par défaut
     */
    public static void styleDialogButton(JButton button, boolean isDefault) {
        Color bgColor = isDefault ? COULEUR_ACCENT : new Color(243, 244, 246);
        Color fgColor = isDefault ? Color.WHITE : COULEUR_TEXTE;

        button.setPreferredSize(DIALOG_BUTTON_SIZE);
        button.setFont(POLICE_DIALOG_CONTENU.deriveFont(Font.BOLD));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);

        // Effets de survol
        button.addMouseListener(new ButtonStateHandler(button, bgColor));
    }

    /**
     * Classe interne pour gérer les états du bouton.
     */
    private static class ButtonStateHandler extends MouseAdapter {
        private final JButton button;
        private final Color defaultColor;

        public ButtonStateHandler(JButton button, Color defaultColor) {
            this.button = button;
            this.defaultColor = defaultColor;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(defaultColor.brighter());
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(defaultColor);
            button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            button.setBackground(defaultColor.darker());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            button.setBackground(defaultColor);
        }
    }
}