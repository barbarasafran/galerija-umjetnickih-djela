package org.example.model;

import java.time.LocalDate;
import java.util.Objects;

public class Umjetnik extends Entitet {

    private String ime;
    private String prezime;
    private LocalDate datumRodjenja;
    private String drzava;
    private String biografija;

    public Umjetnik() {
        super();
    }

    public Umjetnik(Long id, String ime, String prezime, LocalDate datumRodjenja,
                    String drzava, String biografija) {
        super(id, ime + " " + prezime);
        this.ime = ime;
        this.prezime = prezime;
        this.datumRodjenja = datumRodjenja;
        this.drzava = drzava;
        this.biografija = biografija;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
        azurirajNaziv();
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
        azurirajNaziv();
    }

    public LocalDate getDatumRodjenja() {
        return datumRodjenja;
    }

    public void setDatumRodjenja(LocalDate datumRodjenja) {
        this.datumRodjenja = datumRodjenja;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    public String getBiografija() {
        return biografija;
    }

    public void setBiografija(String biografija) {
        this.biografija = biografija;
    }

    public String getImePrezime() {
        return ime + " " + prezime;
    }

    private void azurirajNaziv() {
        this.naziv = (ime != null ? ime : "") + " " + (prezime != null ? prezime : "");
    }

    @Override
    public String opisiSe() {
        return getImePrezime() + (drzava != null ? " (" + drzava + ")" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Umjetnik)) return false;
        if (!super.equals(o)) return false;
        Umjetnik umjetnik = (Umjetnik) o;
        return Objects.equals(ime, umjetnik.ime) && Objects.equals(prezime, umjetnik.prezime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ime, prezime);
    }

    @Override
    public String toString() {
        return "Umjetnik{id=" + id + ", ime='" + ime + "', prezime='" + prezime + "', drzava='" + drzava + "'}";
    }
}