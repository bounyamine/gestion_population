package org.population.ui.components;

import org.population.modele.Localite.TypePopulation;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.util.DoubleSummaryStatistics;
import java.util.Map;

public class StatsPanel extends JPanel {
    private JPanel chartsPanel;
    private JFreeChart densityChart;
    private JFreeChart proportionChart;

    public StatsPanel() {
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(325, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Statistiques"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        chartsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        add(chartsPanel, BorderLayout.CENTER);

        createInitialCharts();
    }

    private void createInitialCharts() {
        DefaultCategoryDataset densityDataset = new DefaultCategoryDataset();
        @SuppressWarnings("rawtypes")
        DefaultPieDataset proportionDataset = new DefaultPieDataset();

        densityChart = ChartFactory.createBarChart(
                "Densité par type", "Type", "Densité moyenne (hab/km²)",
                densityDataset
        );

        proportionChart = ChartFactory.createPieChart(
                "Répartition des localités",
                proportionDataset,
                true, true, false
        );

        updateChartsPanel();
    }

    private void updateChartsPanel() {
        chartsPanel.removeAll();
        chartsPanel.add(new ChartPanel(densityChart));
        chartsPanel.add(new ChartPanel(proportionChart));
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }

    @SuppressWarnings("unchecked")
    public void updateCharts(Map<TypePopulation, DoubleSummaryStatistics> stats) {
        DefaultCategoryDataset densityDataset = new DefaultCategoryDataset();
        @SuppressWarnings("rawtypes")
        DefaultPieDataset proportionDataset = new DefaultPieDataset();

        stats.forEach((type, stat) -> {
            densityDataset.addValue(stat.getAverage(), "Densité moyenne", type.toString());
            proportionDataset.setValue(type.toString(), stat.getCount());
        });

        densityChart = ChartFactory.createBarChart(
                "Densité par type", "Type", "Densité moyenne (hab/km²)",
                densityDataset
        );

        proportionChart = ChartFactory.createPieChart(
                "Répartition des localités",
                proportionDataset,
                true, true, false
        );

        updateChartsPanel();
    }
}
