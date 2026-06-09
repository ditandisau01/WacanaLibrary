package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardController {

    @FXML private Label lblBukuAktif, lblPeminjaman, lblAnggota, lblDenda;
    @FXML private BarChart<String, Number> barChartPeminjaman;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        loadMetrikSummary();
        loadGrafik();
    }

    private void loadMetrikSummary() {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rsBuku = stmt.executeQuery("SELECT COUNT(*) AS total FROM Buku WHERE status != 'Rusak'");
            if (rsBuku.next()) lblBukuAktif.setText(String.valueOf(rsBuku.getInt("total")));

            ResultSet rsAnggota = stmt.executeQuery("SELECT COUNT(*) AS total FROM Anggota WHERE status = 'Aktif'");
            if (rsAnggota.next()) lblAnggota.setText(String.valueOf(rsAnggota.getInt("total")));

            ResultSet rsDenda = stmt.executeQuery("SELECT SUM(totalDenda) AS total FROM Transaksi");
            if (rsDenda.next()) lblDenda.setText("Rp " + rsDenda.getInt("total"));

            String today = java.time.LocalDate.now().toString();
            ResultSet rsToday = stmt.executeQuery("SELECT COUNT(*) AS total FROM Transaksi WHERE tanggalPinjam = '" + today + "'");
            if (rsToday.next()) lblPeminjaman.setText(String.valueOf(rsToday.getInt("total")));

        } catch (Exception e) {
            System.err.println("Gagal memuat metrik: " + e.getMessage());
        }
    }

    private void loadGrafik() {
        barChartPeminjaman.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Volume Peminjaman 2026");

        String[] namaBulan = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun"};
        int[] jumlahPeminjaman = new int[6];

        String query = "SELECT strftime('%m', tanggalPinjam) AS bulan, COUNT(*) AS jumlah " +
                "FROM Transaksi " +
                "WHERE strftime('%Y', tanggalPinjam) = '2026' " +
                "GROUP BY bulan";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String bulanStr = rs.getString("bulan");
                if (bulanStr != null) {
                    int bulanInt = Integer.parseInt(bulanStr);
                    if (bulanInt >= 1 && bulanInt <= 6) {
                        jumlahPeminjaman[bulanInt - 1] = rs.getInt("jumlah");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Database transaksi belum memiliki data/tabel valid: " + e.getMessage());
        }

        for (int i = 0; i < namaBulan.length; i++) {
            series.getData().add(new XYChart.Data<>(namaBulan[i], jumlahPeminjaman[i]));
        }

        barChartPeminjaman.getData().add(series);

        // PERBAIKAN: Gunakan Platform.runLater agar Tooltip pasti menempel setelah grafik di-render di layar
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    String tooltipText = data.getXValue() + "\nPeminjaman : " + data.getYValue();
                    Tooltip tooltip = new Tooltip(tooltipText);

                    tooltip.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                    tooltip.setShowDelay(Duration.millis(100));

                    Tooltip.install(data.getNode(), tooltip);

                    data.getNode().setOnMouseEntered(event -> data.getNode().setStyle("-fx-cursor: hand;"));
                    data.getNode().setOnMouseExited(event -> data.getNode().setStyle(""));
                }
            }
        });
    }

    @FXML
    private void handleTampilkanDefault() {
        refreshDashboard();
        System.out.println("Dashboard direset ke tampilan default (sinkronisasi data SQLite terbaru).");
    }
}