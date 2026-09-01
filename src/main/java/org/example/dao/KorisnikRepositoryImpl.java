package org.example.dao;

import org.example.model.Korisnik;
import org.example.model.Uloga;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KorisnikRepositoryImpl implements KorisnikRepository {

    @Override
    public Korisnik save(Korisnik korisnik) {
        String sql = "INSERT INTO korisnik (korisnicko_ime, lozinka_hash, uloga) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, korisnik.getKorisnickoIme());
            stmt.setString(2, korisnik.getLozinkaHash());
            stmt.setString(3, korisnik.getUloga().name());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    korisnik.setId(keys.getLong(1));
                }
            }
            return korisnik;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri spremanju korisnika", e);
        }
    }

    @Override
    public Optional<Korisnik> findById(Long id) {
        String sql = "SELECT * FROM korisnik WHERE id = ?";
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
            throw new RuntimeException("Greška pri dohvatu korisnika", e);
        }
    }

    @Override
    public Optional<Korisnik> findByKorisnickoIme(String korisnickoIme) {
        String sql = "SELECT * FROM korisnik WHERE korisnicko_ime = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnickoIme);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapirajRedak(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu korisnika po korisničkom imenu", e);
        }
    }

    @Override
    public List<Korisnik> findAll() {
        String sql = "SELECT * FROM korisnik ORDER BY korisnicko_ime";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        List<Korisnik> rezultat = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rezultat.add(mapirajRedak(rs));
            }
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu korisnika", e);
        }
    }

    @Override
    public void update(Korisnik korisnik) {
        String sql = "UPDATE korisnik SET korisnicko_ime = ?, lozinka_hash = ?, uloga = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, korisnik.getKorisnickoIme());
            stmt.setString(2, korisnik.getLozinkaHash());
            stmt.setString(3, korisnik.getUloga().name());
            stmt.setLong(4, korisnik.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju korisnika", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM korisnik WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju korisnika", e);
        }
    }

    private Korisnik mapirajRedak(ResultSet rs) throws SQLException {
        Korisnik k = new Korisnik();
        k.setId(rs.getLong("id"));
        k.setKorisnickoIme(rs.getString("korisnicko_ime"));
        k.setLozinkaHash(rs.getString("lozinka_hash"));
        k.setUloga(Uloga.valueOf(rs.getString("uloga")));
        return k;
    }
}