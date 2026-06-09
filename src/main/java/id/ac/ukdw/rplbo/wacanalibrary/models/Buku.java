package id.ac.ukdw.rplbo.wacanalibrary.models;
import javafx.beans.property.*;

public class Buku {
    private final StringProperty idBuku, isbn, judul, pengarang, tipe, kategori, gambar, status;
    private final IntegerProperty tahunTerbit, halaman;

    public Buku(String idBuku, String isbn, String judul, String pengarang, String tipe, String kategori, int tahunTerbit, int halaman, String gambar, String status) {
        this.idBuku = new SimpleStringProperty(idBuku);
        this.isbn = new SimpleStringProperty(isbn);
        this.judul = new SimpleStringProperty(judul);
        this.pengarang = new SimpleStringProperty(pengarang);
        this.tipe = new SimpleStringProperty(tipe);
        this.kategori = new SimpleStringProperty(kategori);
        this.tahunTerbit = new SimpleIntegerProperty(tahunTerbit);
        this.halaman = new SimpleIntegerProperty(halaman);
        this.gambar = new SimpleStringProperty(gambar != null ? gambar : "");
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty idBukuProperty() { return idBuku; }
    public StringProperty isbnProperty() { return isbn; }
    public StringProperty judulProperty() { return judul; }
    public StringProperty pengarangProperty() { return pengarang; }
    public StringProperty tipeProperty() { return tipe; }
    public StringProperty kategoriProperty() { return kategori; }
    public IntegerProperty tahunTerbitProperty() { return tahunTerbit; }
    public IntegerProperty halamanProperty() { return halaman; }
    public StringProperty gambarProperty() { return gambar; }
    public StringProperty statusProperty() { return status; }

    public String getStatus() { return status.get(); }
    public String getIsbn() { return isbn.get(); }
    public String getGambar() { return gambar.get(); }
}