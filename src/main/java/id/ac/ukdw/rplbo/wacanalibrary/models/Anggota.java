package id.ac.ukdw.rplbo.wacanalibrary.models;

import javafx.beans.property.*;

public class Anggota {
    private final StringProperty idAnggota;
    private final StringProperty namaLengkap;
    private final StringProperty tipe; // Mahasiswa, Dosen, Staff
    private final IntegerProperty batasPinjam;
    private final StringProperty aktifSejak;
    private final StringProperty status;

    public Anggota(String id, String nama, String tipe, int batas, String aktifSejak, String status) {
        this.idAnggota = new SimpleStringProperty(id);
        this.namaLengkap = new SimpleStringProperty(nama);
        this.tipe = new SimpleStringProperty(tipe);
        this.batasPinjam = new SimpleIntegerProperty(batas);
        this.aktifSejak = new SimpleStringProperty(aktifSejak);
        this.status = new SimpleStringProperty(status);
    }

    // Getter Properties untuk TableView
    public StringProperty idAnggotaProperty() { return idAnggota; }
    public StringProperty namaLengkapProperty() { return namaLengkap; }
    public StringProperty tipeProperty() { return tipe; }
    public IntegerProperty batasPinjamProperty() { return batasPinjam; }
    public StringProperty aktifSejakProperty() { return aktifSejak; }
    public StringProperty statusProperty() { return status; }
}
