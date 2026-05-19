package id.ac.ukdw.rplbo.wacanalibrary.models;

import javafx.beans.property.*;

public class Buku {
    private final StringProperty idBuku;
    private final StringProperty judul;
    private final StringProperty pengarang;
    private final StringProperty tipe;
    private final StringProperty kategori;
    private final IntegerProperty tahunTerbit;
    private final IntegerProperty halaman; // ATRIBUT BARU
    private final StringProperty status;

    // PERBAIKAN DI SINI: Menambahkan "int halaman" di dalam parameter constructor
    public Buku(String idBuku, String judul, String pengarang, String tipe, String kategori, int tahunTerbit, int halaman, String status) {
        this.idBuku = new SimpleStringProperty(idBuku);
        this.judul = new SimpleStringProperty(judul);
        this.pengarang = new SimpleStringProperty(pengarang);
        this.tipe = new SimpleStringProperty(tipe);
        this.kategori = new SimpleStringProperty(kategori);
        this.tahunTerbit = new SimpleIntegerProperty(tahunTerbit);
        this.halaman = new SimpleIntegerProperty(halaman);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty idBukuProperty() { return idBuku; }
    public StringProperty judulProperty() { return judul; }
    public StringProperty pengarangProperty() { return pengarang; }
    public StringProperty kategoriProperty() { return kategori; }
    public IntegerProperty tahunTerbitProperty() { return tahunTerbit; }
    public IntegerProperty halamanProperty() { return halaman; } // GETTER BARU
    public StringProperty statusProperty() { return status; }

    public String getStatus() { return status.get(); }
}