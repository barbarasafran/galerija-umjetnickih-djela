package org.example.dao;

import org.example.model.StilPokret;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StilPokretRepositoryImpl implements StilPokretRepository {

    @Override
    public StilPokret save(StilPokret stil) {
        String sql = "INSERT INTO stil_pokret (naziv, opis, razdoblje) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, stil.getNaziv());
            stmt.setString(2, stil.getOpis());
            stmt.setString(3, stil.getRazdoblje());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    stil.setId(keys.getLong(1));
                }
            }
            return stil;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri spremanju stila/pokreta", e);
        }
    }

    @Override
    public Optional<StilPokret> findById(Long id) {
        String sql = "SELECT * FROM stil_pokret WHERE id = ?";
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
            throw new RuntimeException("Greška pri dohvatu stila/pokreta", e);
        }
    }

    @Override
    public List<StilPokret> findAll() {
        String sql = "SELECT * FROM stil_pokret ORDER BY naziv";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        List<StilPokret> rezultat = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rezultat.add(mapirajRedak(rs));
            }
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu stilova/pokreta", e);
        }
    }

    @Override
    public void update(StilPokret stil) {
        String sql = "UPDATE stil_pokret SET naziv = ?, opis = ?, razdoblje = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stil.getNaziv());
            stmt.setString(2, stil.getOpis());
            stmt.setString(3, stil.getRazdoblje());
            stmt.setLong(4, stil.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju stila/pokreta", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM stil_pokret WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju stila/pokreta", e);
        }
    }

    private StilPokret mapirajRedak(ResultSet rs) throws SQLException {
        StilPokret s = new StilPokret();
        s.setId(rs.getLong("id"));
        s.setNaziv(rs.getString("naziv"));
        s.setOpis(rs.getString("opis"));
        s.setRazdoblje(rs.getString("razdoblje"));
        return s;
    }
}