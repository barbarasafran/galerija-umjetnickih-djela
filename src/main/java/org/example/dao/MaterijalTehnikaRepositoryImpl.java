package org.example.dao;

import org.example.model.MaterijalTehnika;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterijalTehnikaRepositoryImpl implements MaterijalTehnikaRepository {

    @Override
    public MaterijalTehnika save(MaterijalTehnika materijal) {
        String sql = "INSERT INTO materijal_tehnika (naziv, opis) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, materijal.getNaziv());
            stmt.setString(2, materijal.getOpis());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    materijal.setId(keys.getLong(1));
                }
            }
            return materijal;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri spremanju materijala/tehnike", e);
        }
    }

    @Override
    public Optional<MaterijalTehnika> findById(Long id) {
        String sql = "SELECT * FROM materijal_tehnika WHERE id = ?";
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
            throw new RuntimeException("Greška pri dohvatu materijala/tehnike", e);
        }
    }

    @Override
    public List<MaterijalTehnika> findAll() {
        String sql = "SELECT * FROM materijal_tehnika ORDER BY naziv";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        List<MaterijalTehnika> rezultat = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rezultat.add(mapirajRedak(rs));
            }
            return rezultat;
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri dohvatu materijala/tehnika", e);
        }
    }

    @Override
    public void update(MaterijalTehnika materijal) {
        String sql = "UPDATE materijal_tehnika SET naziv = ?, opis = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, materijal.getNaziv());
            stmt.setString(2, materijal.getOpis());
            stmt.setLong(3, materijal.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri ažuriranju materijala/tehnike", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM materijal_tehnika WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju materijala/tehnike", e);
        }
    }

    private MaterijalTehnika mapirajRedak(ResultSet rs) throws SQLException {
        MaterijalTehnika m = new MaterijalTehnika();
        m.setId(rs.getLong("id"));
        m.setNaziv(rs.getString("naziv"));
        m.setOpis(rs.getString("opis"));
        return m;
    }
}