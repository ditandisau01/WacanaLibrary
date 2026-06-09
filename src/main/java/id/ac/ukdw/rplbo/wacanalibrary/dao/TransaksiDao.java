package id.ac.ukdw.rplbo.wacanalibrary.dao;

import id.ac.ukdw.rplbo.wacanalibrary.models.Transaksi;
import java.util.List;

public interface TransaksiDao {
    List<Transaksi> getAllTransaksi();
    void prosesPengembalian(String idTransaksi, String status, String tglKembali, double totalDenda, String metodePembayaran);
}
