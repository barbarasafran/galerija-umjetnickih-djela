package org.example.dao;

import org.example.model.Izlozba;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IzlozbaRepositoryImpl implements IzlozbaRepository {

    @Override
    public Izlozba save(Izlozba izlozba) {
        String sql = "INSERT INTO izlozba (naziv, lokacija, datum_pocetka, datum_zavrsetka, opis) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, izlozba.getNaziv());
            stmt.setString(2, izlozba.getLokacija());
            stmt.setDate(3, izlozba.getDatumPocetka() != null ? Date.valueOf(izlozba.getDatumPocetka()) : null);
            stmt.setDate(4, izlozba.getDatumZavrsetka() != null ? Date.valueOf(izlozba.getDatumZavrsetka()) : null);
            stmt.setString(5, izlozba.getOpis());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    izlozba.setId(keys.getLong(1));
                }
            }
            return izlozba;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri spremanju izložbe", e);
        }
    }

    @Override
    public Optional<Izlozba> findById(Long id) {
        String sql = "SELECT * FROM izlozba WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapirajRedak(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu izložbe", e);
        }
    }

    @Override
    public List<Izlozba> findAll() {
        String sql = "SELECT * FROM izlozba ORDER BY datum_pocetka DESC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        List<Izlozba> rezultat = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rezultat.add(mapirajRedak(rs));
            }
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu izložbi", e);
        }
    }

    @Override
    public void update(Izlozba izlozba) {
        String sql = "UPDATE izlozba SET naziv = ?, lokacija = ?, datum_pocetka = ?, datum_zavrsetka = ?, opis = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, izlozba.getNaziv());
            stmt.setString(2, izlozba.getLokacija());
            stmt.setDate(3, izlozba.getDatumPocetka() != null ? Date.valueOf(izlozba.getDatumPocetka()) : null);
            stmt.setDate(4, izlozba.getDatumZavrsetka() != null ? Date.valueOf(izlozba.getDatumZavrsetka()) : null);
            stmt.setString(5, izlozba.getOpis());
            stmt.setLong(6, izlozba.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju izložbe", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM izlozba WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju izložbe", e);
        }
    }

    private Izlozba mapirajRedak(ResultSet rs) throws SQLException {
        Izlozba i = new Izlozba();
        i.setId(rs.getLong("id"));
        i.setNaziv(rs.getString("naziv"));
        i.setLokacija(rs.getString("lokacija"));

        Date pocetak = rs.getDate("datum_pocetka");
        if (pocetak != null) i.setDatumPocetka(pocetak.toLocalDate());

        Date zavrsetak = rs.getDate("datum_zavrsetka");
        if (zavrsetak != null) i.setDatumZavrsetka(zavrsetak.toLocalDate());

        i.setOpis(rs.getString("opis"));
        return i;
    }
}