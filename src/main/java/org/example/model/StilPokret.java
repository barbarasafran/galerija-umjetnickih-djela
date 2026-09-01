package org.example.model;

public class StilPokret extends Entitet {

    private String opis;
    private String razdoblje;

    public StilPokret() {
        super();
    }

    public StilPokret(Long id, String naziv, String opis, String razdoblje) {
        super(id, naziv);
        this.opis = opis;
        this.razdoblje = razdoblje;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getRazdoblje() {
        return razdoblje;
    }

    public void setRazdoblje(String razdoblje) {
        this.razdoblje = razdoblje;
    }

    @Override
    public String opisiSe() {
        return naziv + (razdoblje != null ? " (" + razdoblje + ")" : "");
    }

    @Override
    public String toString() {
        return "StilPokret{id=" + id + ", naziv='" + naziv + "', razdoblje='" + razdoblje + "'}";
    }
}