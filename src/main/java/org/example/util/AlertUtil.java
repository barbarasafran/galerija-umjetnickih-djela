package org.example.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class AlertUtil {

    private AlertUtil() {
    }

    public static void prikaziGresku(String poruka) {
        Alert alert = new Alert(Alert.AlertType.ERROR, poruka);
        alert.setTitle("Greška");
        alert.showAndWait();
    }

    public static void prikaziInfo(String poruka) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, poruka);
        alert.setTitle("Informacija");
        alert.showAndWait();
    }

    public static boolean potvrdi(String naslov, String poruka) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, poruka);
        alert.setTitle(naslov);
        Optional<ButtonType> rezultat = alert.showAndWait();
        return rezultat.isPresent() && rezultat.get().getButtonData().isDefaultButton();
    }
}