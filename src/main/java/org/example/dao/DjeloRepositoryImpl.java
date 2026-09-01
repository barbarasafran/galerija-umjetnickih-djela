package org.example.dao;

import org.example.model.Djelo;
import org.example.model.Izlozba;
import org.example.model.MaterijalTehnika;
import org.example.model.StilPokret;
import org.example.model.Umjetnik;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DjeloRepositoryImpl implements DjeloRepository {

    private static final String SELECT_BAZA =
            "SELECT d.id AS d_id, d.naziv AS d_naziv, d.godina_nastanka, d.dimenzije, " +
                    "       d.opis AS d_opis, d.putanja_slike, " +
                    "       u.id AS u_id, u.ime, u.prezime, u.datum_rodjenja, u.drzava, u.biografija, " +
                    "       s.id AS s_id, s.naziv AS s_naziv, s.opis AS s_opis, s.razdoblje, " +
                    "       m.id AS m_id, m.naziv AS m_naziv, m.opis AS m_opis " +
                    "FROM djelo d " +
                    "JOIN umjetnik u ON d.umjetnik_id = u.id " +
                    "LEFT JOIN stil_pokret s ON d.stil_id = s.id " +
                    "LEFT JOIN materijal_tehnika m ON d.materijal_id = m.id ";

    @Override
    public Djelo save(Djelo djelo) {
        String sql = "{CALL sp_dodaj_djelo(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, djelo.getNaziv());
            stmt.setInt(2, djelo.getGodinaNastanka());
            stmt.setString(3, djelo.getDimenzije());
            stmt.setString(4, djelo.getOpis());
            stmt.setString(5, djelo.getPutanjaSlike());
            stmt.setLong(6, djelo.getUmjetnik().getId());
            setNullableLong(stmt, 7, djelo.getStil() != null ? djelo.getStil().getId() : null);
            setNullableLong(stmt, 8, djelo.getMaterijal() != null ? djelo.getMaterijal().getId() : null);
            stmt.registerOutParameter(9, Types.INTEGER);

            stmt.execute();

            djelo.setId((long) stmt.getInt(9));
            return djelo;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri spremanju djela", e);
        }
    }

    @Override
    public void update(Djelo djelo) {
        String sql = "{CALL sp_azuriraj_djelo(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setLong(1, djelo.getId());
            stmt.setString(2, djelo.getNaziv());
            stmt.setInt(3, djelo.getGodinaNastanka());
            stmt.setString(4, djelo.getDimenzije());
            stmt.setString(5, djelo.getOpis());
            stmt.setString(6, djelo.getPutanjaSlike());
            stmt.setLong(7, djelo.getUmjetnik().getId());
            setNullableLong(stmt, 8, djelo.getStil() != null ? djelo.getStil().getId() : null);
            setNullableLong(stmt, 9, djelo.getMaterijal() != null ? djelo.getMaterijal().getId() : null);

            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju djela", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "{CALL sp_obrisi_djelo(?)}";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setLong(1, id);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju djela", e);
        }
    }

    @Override
    public void dodajNaIzlozbu(Long djeloId, Long izlozbaId) {
        String sql = "{CALL sp_dodaj_djelo_na_izlozbu(?, ?)}";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setLong(1, djeloId);
            stmt.setLong(2, izlozbaId);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dodavanju djela na izložbu", e);
        }
    }

    @Override
    public void ukloniSIzlozbe(Long djeloId, Long izlozbaId) {
        String sql = "{CALL sp_ukloni_djelo_s_izlozbe(?, ?)}";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setLong(1, djeloId);
            stmt.setLong(2, izlozbaId);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri uklanjanju djela s izložbe", e);
        }
    }

    @Override
    public Optional<Djelo> findById(Long id) {
        String sql = SELECT_BAZA + " WHERE d.id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Djelo djelo = mapirajRedak(rs);
                    djelo.setIzlozbe(ucitajIzlozbeZaDjelo(djelo.getId()));
                    return Optional.of(djelo);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu djela", e);
        }
    }

    @Override
    public List<Djelo> findAll() {
        String sql = SELECT_BAZA + " ORDER BY d.naziv";
        return izvrsiListuUpita(sql, stmt -> {});
    }

    @Override
    public List<Djelo> findByUmjetnik(Long umjetnikId) {
        String sql = SELECT_BAZA + " WHERE u.id = ? ORDER BY d.naziv";
        return izvrsiListuUpita(sql, stmt -> stmt.setLong(1, umjetnikId));
    }

    @Override
    public List<Djelo> findByStil(Long stilId) {
        String sql = SELECT_BAZA + " WHERE s.id = ? ORDER BY d.naziv";
        return izvrsiListuUpita(sql, stmt -> stmt.setLong(1, stilId));
    }

    @Override
    public List<Djelo> findByMaterijal(Long materijalId) {
        String sql = SELECT_BAZA + " WHERE m.id = ? ORDER BY d.naziv";
        return izvrsiListuUpita(sql, stmt -> stmt.setLong(1, materijalId));
    }

    @Override
    public List<Djelo> findByIzlozba(Long izlozbaId) {
        String sql = SELECT_BAZA +
                " JOIN djelo_izlozba di ON di.djelo_id = d.id " +
                " WHERE di.izlozba_id = ? ORDER BY d.naziv";
        return izvrsiListuUpita(sql, stmt -> stmt.setLong(1, izlozbaId));
    }

    // ---------- pomoćne (private) metode ----------

    @FunctionalInterface
    private interface ParamSetter {
        void postavi(PreparedStatement stmt) throws SQLException;
    }

    private List<Djelo> izvrsiListuUpita(String sql, ParamSetter setter) {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        List<Djelo> rezultat = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setter.postavi(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Djelo djelo = mapirajRedak(rs);
                    djelo.setIzlozbe(ucitajIzlozbeZaDjelo(djelo.getId()));
                    rezultat.add(djelo);
                }
            }
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu djela", e);
        }
    }

    private Set<Izlozba> ucitajIzlozbeZaDjelo(Long djeloId) {
        String sql = "SELECT i.* FROM izlozba i " +
                "JOIN djelo_izlozba di ON di.izlozba_id = i.id " +
                "WHERE di.djelo_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Set<Izlozba> izlozbe = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, djeloId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Izlozba i = new Izlozba();
                    i.setId(rs.getLong("id"));
                    i.setNaziv(rs.getString("naziv"));
                    i.setLokacija(rs.getString("lokacija"));
                    Date pocetak = rs.getDate("datum_pocetka");
                    Date zavrsetak = rs.getDate("datum_zavrsetka");
                    if (pocetak != null) i.setDatumPocetka(pocetak.toLocalDate());
                    if (zavrsetak != null) i.setDatumZavrsetka(zavrsetak.toLocalDate());
                    i.setOpis(rs.getString("opis"));
                    izlozbe.add(i);
                }
            }
            return izlozbe;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu izložbi za djelo", e);
        }
    }

    private void setNullableLong(CallableStatement stmt, int index, Long value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setLong(index, value);
        }
    }

    private Djelo mapirajRedak(ResultSet rs) throws SQLException {
        Umjetnik umjetnik = new Umjetnik();
        umjetnik.setId(rs.getLong("u_id"));
        umjetnik.setIme(rs.getString("ime"));
        umjetnik.setPrezime(rs.getString("prezime"));
        Date datumRodjenja = rs.getDate("datum_rodjenja");
        if (datumRodjenja != null) umjetnik.setDatumRodjenja(datumRodjenja.toLocalDate());
        umjetnik.setDrzava(rs.getString("drzava"));
        umjetnik.setBiografija(rs.getString("biografija"));

        StilPokret stil = null;
        long stilId = rs.getLong("s_id");
        if (!rs.wasNull()) {
            stil = new StilPokret();
            stil.setId(stilId);
            stil.setNaziv(rs.getString("s_naziv"));
            stil.setOpis(rs.getString("s_opis"));
            stil.setRazdoblje(rs.getString("razdoblje"));
        }

        MaterijalTehnika materijal = null;
        long materijalId = rs.getLong("m_id");
        if (!rs.wasNull()) {
            materijal = new MaterijalTehnika();
            materijal.setId(materijalId);
            materijal.setNaziv(rs.getString("m_naziv"));
            materijal.setOpis(rs.getString("m_opis"));
        }

        Djelo djelo = new Djelo();
        djelo.setId(rs.getLong("d_id"));
        djelo.setNaziv(rs.getString("d_naziv"));
        djelo.setGodinaNastanka(rs.getInt("godina_nastanka"));
        djelo.setDimenzije(rs.getString("dimenzije"));
        djelo.setOpis(rs.getString("d_opis"));
        djelo.setPutanjaSlike(rs.getString("putanja_slike"));
        djelo.setUmjetnik(umjetnik);
        djelo.setStil(stil);
        djelo.setMaterijal(materijal);

        return djelo;
    }
}