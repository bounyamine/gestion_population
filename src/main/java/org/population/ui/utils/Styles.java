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
 * Utility class for managing UI styles and theme configurations.
 * Provides a centralized system for maintaining consistent application appearance.
 */
public final class Styles {

    public static final int DIALOG_PADDING = 15;

    private Styles() {
        // Prevent instantiation of utility class
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Theme colors for the application UI.
     */
    public static final class Colors {
        // Brand colors
        public static final Color PRIMARY = new Color(37, 99, 235);
        public static final Color SECONDARY = new Color(227, 229, 231);
        public static final Color ACCENT = new Color(59, 130, 246);

        // Semantic colors
        public static final Color TEXT = new Color(30, 41, 59);
        public static final Color ERROR = new Color(239, 68, 68);
        public static final Color SUCCESS = new Color(34, 197, 94);
        public static final Color WARNING = new Color(234, 179, 8);

        // Component-specific colors
        public static final Color DIALOG_BACKGROUND = Color.WHITE;
        public static final Color SECTION_BORDER = new Color(226, 232, 240);
        public static final Color TAB_BACKGROUND = new Color(248, 250, 252);
        public static final Color ACTIVE_TAB = ACCENT;
        public static final Color TAB_TEXT = new Color(71, 85, 105);
        public static final Color HELP_TEXT = new Color(51, 65, 85);

        // Table-specific colors
        public static final Color TABLE_ALTERNATE_ROW = new Color(241, 245, 249);
        public static final Color TABLE_SELECTION = new Color(37, 99, 235, 180);
        public static final Color TABLE_GRID = new Color(226, 232, 240);
        public static final Color TABLE_HEADER = new Color(248, 250, 252);
        public static final Color TABLE_SELECTION_TEXT = Color.WHITE;

        private Colors() {
            throw new AssertionError("Constants class - do not instantiate");
        }
    }

    /**
     * Typography configurations for the application UI.
     */
    public static final class Fonts {
        private static final String FONT_FAMILY = "Segoe UI";

        public static final Font BASE = new Font(FONT_FAMILY, Font.PLAIN, 12);
        public static final Font TITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
        public static final Font SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 14);
        public static final Font TABLE = new Font(FONT_FAMILY, Font.PLAIN, 13);
        public static final Font TABLE_HEADER = new Font(FONT_FAMILY, Font.BOLD, 13);
        public static final Font DIALOG_TITLE = new Font(FONT_FAMILY, Font.BOLD, 15);
        public static final Font DIALOG_SECTION = new Font(FONT_FAMILY, Font.BOLD, 13);
        public static final Font DIALOG_CONTENT = new Font(FONT_FAMILY, Font.PLAIN, 12);

        private Fonts() {
            throw new AssertionError("Constants class - do not instantiate");
        }
    }

    /**
     * Layout dimensions and spacing constants.
     */
    public static final class Dimensions {
        public static final int BUTTON_HEIGHT = 40;
        public static final int BUTTON_MIN_WIDTH = 100;
        public static final int BUTTON_PREF_WIDTH = 120;
        public static final int BUTTON_MAX_WIDTH = 150;
        public static final int PADDING = 10;
        public static final int ROW_HEIGHT = 30;
        public static final int DIALOG_PADDING = 15;
        public static final int SECTION_PADDING = 10;
        public static final int TAB_HEIGHT = 35;
        public static final int SECTION_SPACING = 20;
        public static final int DIALOG_BORDER_RADIUS = 8;
        public static final Dimension DIALOG_BUTTON_SIZE = new Dimension(100, 35);

        private Dimensions() {
            throw new AssertionError("Constants class - do not instantiate");
        }
    }

    /**
     * Common borders used throughout the application.
     */
    public static final class Borders {
        public static final Border SECTION = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.SECTION_BORDER),
                BorderFactory.createEmptyBorder(
                        Dimensions.SECTION_PADDING,
                        Dimensions.SECTION_PADDING,
                        Dimensions.SECTION_PADDING,
                        Dimensions.SECTION_PADDING
                )
        );

        public static final Border DIALOG = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.ACCENT, 1),
                BorderFactory.createEmptyBorder(
                        Dimensions.DIALOG_PADDING,
                        Dimensions.DIALOG_PADDING,
                        Dimensions.DIALOG_PADDING,
                        Dimensions.DIALOG_PADDING
                )
        );

        private Borders() {
            throw new AssertionError("Constants class - do not instantiate");
        }
    }

    /**
     * Creates a styled button with default colors.
     * @param text The button text
     * @return A styled JButton instance
     */
    public static JButton createStyledButton(String text) {
        return createStyledButton(text, Colors.ACCENT, Colors.TEXT);
    }

    /**
     * Creates a styled button with custom colors.
     * @param text The button text
     * @param backgroundColor Button background color
     * @param textColor Button text color
     * @return A styled JButton instance
     */
    public static JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        styleButton(button, backgroundColor, textColor);
        return button;
    }

    /**
     * Applies styling to an existing button.
     * @param button The button to style
     * @param backgroundColor Button background color
     * @param textColor Button text color
     */
    public static void styleButton(JButton button, Color backgroundColor, Color textColor) {
        button.setPreferredSize(new Dimension(Dimensions.BUTTON_PREF_WIDTH, Dimensions.BUTTON_HEIGHT));
        button.setMinimumSize(new Dimension(Dimensions.BUTTON_MIN_WIDTH, Dimensions.BUTTON_HEIGHT));
        button.setMaximumSize(new Dimension(Dimensions.BUTTON_MAX_WIDTH, Dimensions.BUTTON_HEIGHT));

        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(Fonts.BASE.deriveFont(Font.BOLD));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        Border roundedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(
                        Dimensions.PADDING / 2,
                        Dimensions.PADDING,
                        Dimensions.PADDING / 2,
                        Dimensions.PADDING
                )
        );
        button.setBorder(roundedBorder);

        button.addMouseListener(new ButtonStateHandler(button, backgroundColor));
    }

    /**
     * Applies styling to a text field.
     * @param textField The text field to style
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(Fonts.BASE);
        textField.setBackground(Color.WHITE);
        textField.setForeground(Colors.PRIMARY);

        TextFieldFocusHandler focusHandler = new TextFieldFocusHandler(textField);
        textField.addFocusListener(focusHandler);
        focusHandler.updateBorder(false);
    }

    /**
     * Applies styling to a table.
     * @param table The table to style
     */
    public static void styleTable(JTable table) {
        table.setFont(Fonts.TABLE);
        table.setRowHeight(Dimensions.ROW_HEIGHT);
        table.setBackground(Colors.PRIMARY);
        table.setForeground(Colors.TEXT);
        table.setSelectionBackground(Colors.TABLE_SELECTION);
        table.setSelectionForeground(Colors.TABLE_SELECTION_TEXT);
        table.setGridColor(Colors.TABLE_GRID);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);

        styleTableHeader(table.getTableHeader());
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
    }

    /**
     * Styles the header of a table.
     * @param header The table header to style
     */
    public static void styleTableHeader(JTableHeader header) {
        header.setBackground(Colors.TABLE_HEADER);
        header.setForeground(Colors.TEXT);
        header.setFont(Fonts.TABLE_HEADER);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.ACCENT));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                c.setBackground(Colors.TABLE_HEADER);
                c.setFont(Fonts.TABLE_HEADER);
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return c;
            }
        });
    }

    /**
     * Configures table columns with specified widths.
     * @param table The table to configure
     * @param columnWidths Array of preferred widths for each column
     */
    public static void configureTableColumns(JTable table, int[] columnWidths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < Math.min(columnWidths.length, columnModel.getColumnCount()); i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    /**
     * Styles a table's scroll pane.
     * @param scrollPane The scroll pane to style
     */
    public static void styleTableScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(Colors.TABLE_GRID));
        scrollPane.getViewport().setBackground(Colors.PRIMARY);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(Colors.PRIMARY);
        verticalScrollBar.setUI(new ModernScrollBarUI());
    }

    /**
     * Styles a tabbed pane for dialogs.
     * @param tabbedPane The tabbed pane to style
     */
    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setFont(Fonts.DIALOG_SECTION);
        tabbedPane.setBackground(Colors.DIALOG_BACKGROUND);
        tabbedPane.setForeground(Colors.TAB_TEXT);

        UIManager.put("TabbedPane.selected", Colors.ACTIVE_TAB);
        UIManager.put("TabbedPane.contentAreaColor", Colors.DIALOG_BACKGROUND);
        UIManager.put("TabbedPane.tabAreaBackground", Colors.TAB_BACKGROUND);
        UIManager.put("TabbedPane.focus", Colors.DIALOG_BACKGROUND);

        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return Dimensions.TAB_HEIGHT;
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(Colors.ACTIVE_TAB);
                    g.fillRect(x, y + h - 2, w, 2);
                }
            }
        });
    }

    /**
     * Styles a text area for help content.
     * @param textArea The text area to style
     */
    public static void styleHelpTextArea(JTextArea textArea) {
        textArea.setFont(Fonts.DIALOG_CONTENT);
        textArea.setForeground(Colors.HELP_TEXT);
        textArea.setBackground(Colors.DIALOG_BACKGROUND);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setMargin(new Insets(5, 5, 5, 5));
    }

    /**
     * Styles a dialog panel.
     * @param panel The panel to style
     */
    public static void styleDialogPanel(JPanel panel) {
        panel.setBackground(Colors.DIALOG_BACKGROUND);
        panel.setBorder(Borders.DIALOG);
    }

    /**
     * Styles a dialog button.
     * @param button The button to style
     * @param isDefault Whether this is the default button
     */
    public static void styleDialogButton(JButton button, boolean isDefault) {
        Color bgColor = isDefault ? Colors.ACCENT : new Color(243, 244, 246);
        Color fgColor = isDefault ? Color.WHITE : Colors.TEXT;

        button.setPreferredSize(Dimensions.DIALOG_BUTTON_SIZE);
        button.setFont(Fonts.DIALOG_CONTENT.deriveFont(Font.BOLD));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);

        button.addMouseListener(new ButtonStateHandler(button, bgColor));
    }

    /**
     * Modern scroll bar UI implementation.
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = Colors.SECONDARY;
            this.trackColor = Colors.PRIMARY;
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
    }

    // Private helper classes

    private static class TextFieldFocusHandler extends FocusAdapter {
        private final JTextField textField;

        TextFieldFocusHandler(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void focusGained(FocusEvent e) {
            updateBorder(true);
        }

        @Override
        public void focusLost(FocusEvent e) {
            updateBorder(false);
        }

        void updateBorder(boolean focused) {
            Color borderColor = focused ? Colors.ACCENT : Colors.SECONDARY;
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(5, 0, 5, 0)
            ));
        }
    }

    private static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ?
                        Colors.SECONDARY : Colors.TABLE_ALTERNATE_ROW);
            }

            ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            return c;
        }
    }

    private static class ButtonStateHandler extends MouseAdapter {
        private final JButton button;
        private final Color defaultColor;

        ButtonStateHandler(JButton button, Color defaultColor) {
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