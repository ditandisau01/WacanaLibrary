package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LaporanController {

    @FXML private Label lblTotalDenda, lblTotalKategori;
    @FXML private PieChart pieChartKategori;
    @FXML private ListView<String> listLog;

    @FXML
    public void initialize() {
        loadMetrik();
        loadPieChart();

        // Data statis untuk Log Operasional (Sebagai contoh UI)
        listLog.getItems().addAll(
                "Hari ini, 09:12 - Peminjaman Buku (T001) - Sukses",
                "Kemarin, 14:30 - Pembaruan Anggota (M002) - Sukses",
                "2 Hari lalu - Buku 'Sapiens' ditandai Rusak - Peringatan"
        );
    }

    private void loadMetrik() {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement()) {

            // Ambil total denda (Agregasi)
            ResultSet rsDenda = stmt.executeQuery("SELECT SUM(totalDenda) AS denda FROM Transaksi");
            if (rsDenda.next()) lblTotalDenda.setText("Rp " + rsDenda.getInt("denda"));

            // Ambil jumlah kategori unik
            ResultSet rsKategori = stmt.executeQuery("SELECT COUNT(DISTINCT kategori) AS totalKat FROM Buku");
            if (rsKategori.next()) lblTotalKategori.setText(String.valueOf(rsKategori.getInt("totalKat")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String query = "SELECT kategori, COUNT(*) AS jumlah FROM Buku GROUP BY kategori";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                pieChartData.add(new PieChart.Data(
                        rs.getString("kategori"),
                        rs.getInt("jumlah")
                ));
            }
            pieChartKategori.setData(pieChartData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}