package id.ac.ukdw.rplbo.wacanalibrary.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Transaksi {
    private final StringProperty idTransaksi;
    private final StringProperty idAnggota;
    private final StringProperty idBuku;
    private final ObjectProperty<LocalDate> tanggalPinjam;
    private final ObjectProperty<LocalDate> tanggalJatuhTempo;
    private final StringProperty statusTransaksi;
    private final StringProperty metodePembayaran; // TAMBAHAN

    public Transaksi(String id, String idAnggota, String idBuku, LocalDate pinjam, LocalDate jatuhTempo, String status, String metode) {
        this.idTransaksi = new SimpleStringProperty(id);
        this.idAnggota = new SimpleStringProperty(idAnggota);
        this.idBuku = new SimpleStringProperty(idBuku);
        this.tanggalPinjam = new SimpleObjectProperty<>(pinjam);
        this.tanggalJatuhTempo = new SimpleObjectProperty<>(jatuhTempo);
        this.statusTransaksi = new SimpleStringProperty(status);
        this.metodePembayaran = new SimpleStringProperty(metode); // TAMBAHAN
    }

    public StringProperty idTransaksiProperty() { return idTransaksi; }
    public StringProperty idAnggotaProperty() { return idAnggota; }
    public StringProperty idBukuProperty() { return idBuku; }
    public ObjectProperty<LocalDate> tanggalPinjamProperty() { return tanggalPinjam; }
    public ObjectProperty<LocalDate> tanggalJatuhTempoProperty() { return tanggalJatuhTempo; }
    public StringProperty statusTransaksiProperty() { return statusTransaksi; }
    public StringProperty metodePembayaranProperty() { return metodePembayaran; } // TAMBAHAN
}