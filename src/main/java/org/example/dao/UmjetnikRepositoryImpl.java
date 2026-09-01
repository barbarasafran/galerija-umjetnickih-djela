package org.example.dao;

import org.example.model.Umjetnik;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UmjetnikRepositoryImpl implements UmjetnikRepository {

    @Override
    public Umjetnik save(Umjetnik umjetnik) {
        String sql = "INSERT INTO umjetnik (ime, prezime, datum_rodjenja, drzava, biografija) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, umjetnik.getIme());
            stmt.setString(2, umjetnik.getPrezime());
            stmt.setDate(3, umjetnik.getDatumRodjenja() != null ? Date.valueOf(umjetnik.getDatumRodjenja()) : null);
            stmt.setString(4, umjetnik.getDrzava());
            stmt.setString(5, umjetnik.getBiografija());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    umjetnik.setId(keys.getLong(1));
                }
            }
            return umjetnik;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri spremanju umjetnika", e);
        }
    }

    @Override
    public Optional<Umjetnik> findById(Long id) {
        String sql = "SELECT * FROM umjetnik WHERE id = ?";
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
            throw new RuntimeException("Greška pri dohvatu umjetnika", e);
        }
    }

    @Override
    public List<Umjetnik> findAll() {
        String sql = "SELECT * FROM umjetnik ORDER BY prezime, ime";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        List<Umjetnik> rezultat = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rezultat.add(mapirajRedak(rs));
            }
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu svih umjetnika", e);
        }
    }

    @Override
    public void update(Umjetnik umjetnik) {
        String sql = "UPDATE umjetnik SET ime = ?, prezime = ?, datum_rodjenja = ?, drzava = ?, biografija = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, umjetnik.getIme());
            stmt.setString(2, umjetnik.getPrezime());
            stmt.setDate(3, umjetnik.getDatumRodjenja() != null ? Date.valueOf(umjetnik.getDatumRodjenja()) : null);
            stmt.setString(4, umjetnik.getDrzava());
            stmt.setString(5, umjetnik.getBiografija());
            stmt.setLong(6, umjetnik.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju umjetnika", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM umjetnik WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju umjetnika", e);
        }
    }

    private Umjetnik mapirajRedak(ResultSet rs) throws SQLException {
        Umjetnik u = new Umjetnik();
        u.setId(rs.getLong("id"));
        u.setIme(rs.getString("ime"));
        u.setPrezime(rs.getString("prezime"));

        Date datumRodjenja = rs.getDate("datum_rodjenja");
        if (datumRodjenja != null) {
            u.setDatumRodjenja(datumRodjenja.toLocalDate());
        }

        u.setDrzava(rs.getString("drzava"));
        u.setBiografija(rs.getString("biografija"));
        return u;
    }
}