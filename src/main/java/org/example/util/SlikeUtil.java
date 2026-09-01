package org.example.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class SlikeUtil {

    private static final String ASSETS_FOLDER =
            System.getProperty("user.home") + File.separator + "galerija-app-assets";

    private SlikeUtil() {
    }

    public static String spremiSliku(File izvornaDatoteka) {
        try {
            Path assetsDir = Paths.get(ASSETS_FOLDER);
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }

            String originalnoIme = izvornaDatoteka.getName();
            String ekstenzija = "";
            int tockaIndex = originalnoIme.lastIndexOf('.');
            if (tockaIndex > 0) {
                ekstenzija = originalnoIme.substring(tockaIndex);
            }

            String novoIme = UUID.randomUUID() + ekstenzija;
            Path odrediste = assetsDir.resolve(novoIme);

            Files.copy(izvornaDatoteka.toPath(), odrediste, StandardCopyOption.REPLACE_EXISTING);

            return odrediste.toString();
        } catch (IOException e) {
            throw new RuntimeException("Greška pri spremanju slike", e);
        }
    }

    public static void obrisiSliku(String putanja) {
        if (putanja == null || putanja.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(putanja));
        } catch (IOException e) {
            System.err.println("Greška pri brisanju slike (" + putanja + "): " + e.getMessage());
        }
    }
}