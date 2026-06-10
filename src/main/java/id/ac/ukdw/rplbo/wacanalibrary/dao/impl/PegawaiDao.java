package id.ac.ukdw.rplbo.wacanalibrary.dao;

import id.ac.ukdw.rplbo.wacanalibrary.models.Pegawai;

public interface PegawaiDao {
    Pegawai getPegawaiByUsernameAndPassword(String username, String password);
}
