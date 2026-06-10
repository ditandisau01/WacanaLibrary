package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.dao.BukuDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.BukuDaoImpl;
import id.ac.ukdw.rplbo.wacanalibrary.dao.AnggotaDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.AnggotaDaoImpl;
import id.ac.ukdw.rplbo.wacanalibrary.dao.TransaksiDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.TransaksiDaoImpl;
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

    private final BukuDao bukuDao = new BukuDaoImpl();
    private final AnggotaDao anggotaDao = new AnggotaDaoImpl();
    private final TransaksiDao transaksiDao = new TransaksiDaoImpl();

    @FXML
    private Label lblBukuAktif, lblPeminjaman, lblAnggota, lblDenda;
    @FXML
    private BarChart<String, Number> barChartPeminjaman;

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        loadMetrikSummary();
        loadGrafik();
    }

    private void loadMetrikSummary() {
        try {
            lblBukuAktif.setText(String.valueOf(bukuDao.countBukuNonRusak()));
            lblAnggota.setText(String.valueOf(anggotaDao.countAnggotaAktif()));
            lblDenda.setText("Rp " + (int) transaksiDao.getTotalDenda());

            String today = java.time.LocalDate.now().toString();
            lblPeminjaman.setText(String.valueOf(transaksiDao.countTransaksiByTanggal(today)));
        } catch (Exception e) {
            System.err.println("Gagal memuat metrik: " + e.getMessage());
        }
    }

    private void loadGrafik() {
        barChartPeminjaman.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Volume Peminjaman 2026");

        String[] namaBulan = { "Jan", "Feb", "Mar", "Apr", "Mei", "Jun" };
        int[] jumlahPeminjaman = new int[6];

        try {
            java.util.Map<Integer, Integer> map = transaksiDao.getPeminjamanPerBulan("2026");
            for (int i = 1; i <= 6; i++) {
                jumlahPeminjaman[i - 1] = map.getOrDefault(i, 0);
            }
        } catch (Exception e) {
            System.err.println("Database transaksi belum memiliki data/tabel valid: " + e.getMessage());
        }

        for (int i = 0; i < namaBulan.length; i++) {
            series.getData().add(new XYChart.Data<>(namaBulan[i], jumlahPeminjaman[i]));
        }

        barChartPeminjaman.getData().add(series);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    String tooltipText = data.getXValue() + "\nPeminjaman : " + data.getYValue();
                    Tooltip tooltip = new Tooltip(tooltipText);

                    tooltip.setStyle(
                            "-fx-background-color: white; -fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
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
