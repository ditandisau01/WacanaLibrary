package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.dao.BukuDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.BukuDaoImpl;
import id.ac.ukdw.rplbo.wacanalibrary.dao.TransaksiDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.TransaksiDaoImpl;
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

    private final BukuDao bukuDao = new BukuDaoImpl();
    private final TransaksiDao transaksiDao = new TransaksiDaoImpl();

    @FXML private Label lblTotalDenda, lblTotalKategori;
    @FXML private PieChart pieChartKategori;
    @FXML private ListView<String> listLog;

    @FXML
    public void initialize() {
        loadMetrik();
        loadPieChart();

        listLog.getItems().addAll(
                "Hari ini, 09:12 - Peminjaman Buku (T001) - Sukses",
                "Kemarin, 14:30 - Pembaruan Anggota (M002) - Sukses",
                "2 Hari lalu - Buku 'Sapiens' ditandai Rusak - Peringatan"
        );
    }

    private void loadMetrik() {
        try {
            lblTotalDenda.setText("Rp " + (int) transaksiDao.getTotalDenda());
            lblTotalKategori.setText(String.valueOf(bukuDao.countUniqueKategori()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
 
        try {
            java.util.Map<String, Integer> map = bukuDao.getBukuCountPerKategori();
            for (java.util.Map.Entry<String, Integer> entry : map.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
 
            pieChartKategori.setData(pieChartData);
 
            // PERBAIKAN: Menghitung persentase dan memodifikasi label potongan pie chart
            double total = 0;
            for (PieChart.Data data : pieChartKategori.getData()) {
                total += data.getPieValue();
            }
 
            for (PieChart.Data data : pieChartKategori.getData()) {
                double persentase = (data.getPieValue() / total) * 100;
                String labelBaru = String.format("%s (%.1f%%)", data.getName(), persentase);
                data.setName(labelBaru);
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
