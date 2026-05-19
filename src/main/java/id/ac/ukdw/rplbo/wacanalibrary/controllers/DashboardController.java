package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip; // Import Tooltip ditambahkan
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

            // Metrik Buku
            ResultSet rsBuku = stmt.executeQuery("SELECT COUNT(*) AS total FROM Buku WHERE status != 'Rusak'");
            if (rsBuku.next()) lblBukuAktif.setText(String.valueOf(rsBuku.getInt("total")));

            // Metrik Anggota
            ResultSet rsAnggota = stmt.executeQuery("SELECT COUNT(*) AS total FROM Anggota WHERE status = 'Aktif'");
            if (rsAnggota.next()) lblAnggota.setText(String.valueOf(rsAnggota.getInt("total")));

            // Metrik Denda
            ResultSet rsDenda = stmt.executeQuery("SELECT SUM(totalDenda) AS total FROM Transaksi");
            if (rsDenda.next()) lblDenda.setText("Rp " + rsDenda.getInt("total"));

            // Peminjaman Hari Ini (Berdasarkan tanggal sistem)
            String today = java.time.LocalDate.now().toString();
            ResultSet rsToday = stmt.executeQuery("SELECT COUNT(*) AS total FROM Transaksi WHERE tanggalPinjam = '" + today + "'");
            if (rsToday.next()) lblPeminjaman.setText(String.valueOf(rsToday.getInt("total")));

        } catch (Exception e) {
            System.err.println("Gagal memuat metrik: " + e.getMessage());
        }
    }

    private void loadGrafik() {
        // Membersihkan data grafik lama
        barChartPeminjaman.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Volume Peminjaman 2026");

        // 1. Siapkan array default dengan nilai 0 untuk Semester 1 (Jan-Jun)
        String[] namaBulan = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun"};
        int[] jumlahPeminjaman = new int[6];

        // 2. Tarik data peminjaman riil dari database (hanya untuk tahun 2026)
        // Fungsi strftime('%m', ...) di SQLite mengambil bulan (01-12) dari format YYYY-MM-DD
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
                    // Masukkan ke array jika bulannya antara Januari (1) s/d Juni (6)
                    if (bulanInt >= 1 && bulanInt <= 6) {
                        jumlahPeminjaman[bulanInt - 1] = rs.getInt("jumlah");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Database transaksi belum memiliki data/tabel valid: " + e.getMessage());
        }

        // 3. Masukkan data ke dalam seri grafik
        for (int i = 0; i < namaBulan.length; i++) {
            series.getData().add(new XYChart.Data<>(namaBulan[i], jumlahPeminjaman[i]));
        }

        // Tambahkan seri ke Chart
        barChartPeminjaman.getData().add(series);

        // 4. Implementasi Fitur Hover (Tooltip)
        // Harus dilakukan SETELAH series ditambahkan ke barChart, agar node UI-nya terbentuk
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                // Konfigurasi teks dan desain Tooltip
                String tooltipText = data.getXValue() + "\nPeminjaman : " + data.getYValue();
                Tooltip tooltip = new Tooltip(tooltipText);

                // Menyamakan gaya tooltip dengan gambar (putih dengan bayangan)
                tooltip.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                tooltip.setShowDelay(Duration.millis(100)); // Muncul cepat saat di-hover

                Tooltip.install(data.getNode(), tooltip);

                // Tambahkan efek kursor tangan (pointer) saat mouse diarahkan ke batang grafik
                data.getNode().setOnMouseEntered(event -> data.getNode().setStyle("-fx-cursor: hand;"));
                data.getNode().setOnMouseExited(event -> data.getNode().setStyle(""));
            }
        }
    }

    @FXML
    private void handleTampilkanDefault() {
        refreshDashboard();
        System.out.println("Dashboard direset ke tampilan default (sinkronisasi data SQLite terbaru).");
    }
}