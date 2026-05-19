package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.models.Transaksi;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FormPengembalianController {

    @FXML private Label lblNamaAnggota, lblIdAnggota, lblJudulBuku, lblIdBuku;
    @FXML private Label lblTglPinjam, lblJatuhTempo, lblTotalDenda, lblHariTelat, lblInstruksiQris;
    @FXML private DatePicker dpTglKembali;
    @FXML private ComboBox<String> cbMetodeBayar;
    @FXML private VBox boxDenda;

    private Transaksi transaksiAktif;
    private double totalDendaKalkulasi = 0.0;
    private final double TARIF_DENDA = 1000.0;

    public void setTransaksiData(Transaksi trx) {
        this.transaksiAktif = trx;

        lblIdAnggota.setText("ID: " + trx.idAnggotaProperty().get());
        lblIdBuku.setText("ID: " + trx.idBukuProperty().get());
        lblTglPinjam.setText(trx.tanggalPinjamProperty().get().toString());
        lblJatuhTempo.setText(trx.tanggalJatuhTempoProperty().get().toString());

        muatDetailDariDB(trx.idAnggotaProperty().get(), trx.idBukuProperty().get());

        // Inisialisasi ComboBox
        cbMetodeBayar.setItems(FXCollections.observableArrayList("Cash", "QRIS"));
        cbMetodeBayar.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblInstruksiQris.setVisible("QRIS".equals(newVal));
        });

        dpTglKembali.setValue(LocalDate.now());
        hitungDendaInteraktif(LocalDate.now());

        dpTglKembali.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) hitungDendaInteraktif(newValue);
        });
    }

    private void hitungDendaInteraktif(LocalDate tanggalDikembalikan) {
        LocalDate jatuhTempo = transaksiAktif.tanggalJatuhTempoProperty().get();
        long selisihHari = ChronoUnit.DAYS.between(jatuhTempo, tanggalDikembalikan);

        if (selisihHari > 0) {
            totalDendaKalkulasi = selisihHari * TARIF_DENDA;
            lblHariTelat.setText("Terlambat: " + selisihHari + " Hari");
            lblTotalDenda.setText("Rp " + (int) totalDendaKalkulasi);
            boxDenda.setVisible(true);
            boxDenda.setManaged(true);
        } else {
            totalDendaKalkulasi = 0.0;
            boxDenda.setVisible(false);
            boxDenda.setManaged(false);
        }
    }

    private void muatDetailDariDB(String idAnggota, String idBuku) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement pstAnggota = conn.prepareStatement("SELECT namaLengkap FROM Anggota WHERE idAnggota = ?");
            pstAnggota.setString(1, idAnggota);
            ResultSet rsA = pstAnggota.executeQuery();
            if (rsA.next()) lblNamaAnggota.setText(rsA.getString("namaLengkap"));

            PreparedStatement pstBuku = conn.prepareStatement("SELECT judul FROM Buku WHERE idBuku = ?");
            pstBuku.setString(1, idBuku);
            ResultSet rsB = pstBuku.executeQuery();
            if (rsB.next()) lblJudulBuku.setText(rsB.getString("judul"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleKembalikan() {
        // Validasi: Jika ada denda, metode pembayaran wajib dipilih
        if (totalDendaKalkulasi > 0 && cbMetodeBayar.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Silakan pilih metode pembayaran denda terlebih dahulu!").showAndWait();
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            // 1. Update Transaksi ke Database
            String sqlTrx = "UPDATE Transaksi SET statusTransaksi = ?, tanggalKembali = ?, totalDenda = ?, metodePembayaran = ? WHERE idTransaksi = ?";
            PreparedStatement pstTrx = conn.prepareStatement(sqlTrx);

            pstTrx.setString(1, totalDendaKalkulasi > 0 ? "Terlambat" : "Selesai");
            pstTrx.setString(2, dpTglKembali.getValue().toString());
            pstTrx.setDouble(3, totalDendaKalkulasi);
            pstTrx.setString(4, totalDendaKalkulasi > 0 ? cbMetodeBayar.getValue() : "-");
            pstTrx.setString(5, transaksiAktif.idTransaksiProperty().get());
            pstTrx.executeUpdate();

            // 2. Kembalikan status buku di Katalog
            PreparedStatement pstBuku = conn.prepareStatement("UPDATE Buku SET status = 'Tersedia' WHERE idBuku = ?");
            pstBuku.setString(1, transaksiAktif.idBukuProperty().get());
            pstBuku.executeUpdate();

            // 3. TAMBAHKAN KEMBALI KUOTA PINJAM ANGGOTA (+1)
            PreparedStatement pstUpdateAnggota = conn.prepareStatement("UPDATE Anggota SET batasPinjam = batasPinjam + 1 WHERE idAnggota = ?");
            pstUpdateAnggota.setString(1, transaksiAktif.idAnggotaProperty().get());
            pstUpdateAnggota.executeUpdate();

            // 4. Tutup jendela Pop-up
            tutupJendela();

            // 5. Tampilkan Notifikasi Sukses
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pengembalian Berhasil");
            alert.setHeaderText(null);

            String pesan = "Buku berhasil dikembalikan ke perpustakaan.\n";
            if (totalDendaKalkulasi > 0) {
                pesan += "Status Denda: Lunas (via " + cbMetodeBayar.getValue() + ")";
            } else {
                pesan += "Status Denda: Tidak ada denda keterlambatan.";
            }
            alert.setContentText(pesan);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Error Database");
            err.setHeaderText("Gagal Menyimpan Data!");
            err.setContentText("Pesan Error: " + e.getMessage());
            err.showAndWait();
        }
    }

    @FXML
    private void handleBatal() { tutupJendela(); }

    private void tutupJendela() {
        Stage stage = (Stage) lblNamaAnggota.getScene().getWindow();
        stage.close();
    }
}