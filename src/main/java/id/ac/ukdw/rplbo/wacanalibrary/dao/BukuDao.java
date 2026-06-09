package id.ac.ukdw.rplbo.wacanalibrary.dao;

import id.ac.ukdw.rplbo.wacanalibrary.models.Buku;
import java.util.List;

public interface BukuDao {
    List<Buku> getAllBuku();
    Buku getBukuById(String idBuku);
    void tambahBuku(Buku buku);
    void updateBuku(Buku buku);
    void hapusBuku(String idBuku);
    List<Buku> cariBuku(String keyword);
}