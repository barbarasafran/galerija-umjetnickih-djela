package org.example.util;

import org.example.xml.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static volatile DatabaseConnection instance;

    private Connection connection;

    private DatabaseConnection() {
        AppConfig config = ConfigUtil.ucitajKonfiguraciju();
        String url = config.getBaza().getConnectionString();
        String korisnik = config.getBaza().getKorisnik();
        String lozinka = config.getBaza().getLozinka();

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
        } catch (SQLException e) {
            throw new RuntimeException("Neuspjelo spajanje na bazu podataka", e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                AppConfig config = ConfigUtil.ucitajKonfiguraciju();
                connection = DriverManager.getConnection(
                        config.getBaza().getConnectionString(),
                        config.getBaza().getKorisnik(),
                        config.getBaza().getLozinka()
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri provjeri konekcije", e);
        }
        return connection;
    }
}