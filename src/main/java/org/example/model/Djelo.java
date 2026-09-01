package org.example.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Djelo extends Entitet {

    private int godinaNastanka;
    private String dimenzije;
    private String opis;
    private String putanjaSlike;

    private Umjetnik umjetnik;
    private StilPokret stil;
    private MaterijalTehnika materijal;

    private Set<Izlozba> izlozbe = new HashSet<>();

    public Djelo() {
        super();
    }

    public Djelo(Long id, String naziv, int godinaNastanka, String dimenzije, String opis,
                 String putanjaSlike, Umjetnik umjetnik, StilPokret stil, MaterijalTehnika materijal) {
        super(id, naziv);
        this.godinaNastanka = godinaNastanka;
        this.dimenzije = dimenzije;
        this.opis = opis;
        this.putanjaSlike = putanjaSlike;
        this.umjetnik = umjetnik;
        this.stil = stil;
        this.materijal = materijal;
    }

    public int getGodinaNastanka() {
        return godinaNastanka;
    }

    public void setGodinaNastanka(int godinaNastanka) {
        this.godinaNastanka = godinaNastanka;
    }

    public String getDimenzije() {
        return dimenzije;
    }

    public void setDimenzije(String dimenzije) {
        this.dimenzije = dimenzije;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getPutanjaSlike() {
        return putanjaSlike;
    }

    public void setPutanjaSlike(String putanjaSlike) {
        this.putanjaSlike = putanjaSlike;
    }

    public Umjetnik getUmjetnik() {
        return umjetnik;
    }

    public void setUmjetnik(Umjetnik umjetnik) {
        this.umjetnik = umjetnik;
    }

    public StilPokret getStil() {
        return stil;
    }

    public void setStil(StilPokret stil) {
        this.stil = stil;
    }

    public MaterijalTehnika getMaterijal() {
        return materijal;
    }

    public void setMaterijal(MaterijalTehnika materijal) {
        this.materijal = materijal;
    }

    public Set<Izlozba> getIzlozbe() {
        return izlozbe;
    }

    public void setIzlozbe(Set<Izlozba> izlozbe) {
        this.izlozbe = izlozbe;
    }



    public void dodajNaIzlozbu(Izlozba izlozba) {
        this.izlozbe.add(izlozba);
    }

    public void ukloniSIzlozbe(Izlozba izlozba) {
        this.izlozbe.remove(izlozba);
    }

    @Override
    public String opisiSe() {
        String imeUmjetnika = umjetnik != null ? umjetnik.getImePrezime() : "nepoznat autor";
        return naziv + " (" + godinaNastanka + "), " + imeUmjetnika;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Djelo)) return false;
        if (!super.equals(o)) return false;
        Djelo djelo = (Djelo) o;
        return Objects.equals(id, djelo.id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Djelo{id=" + id + ", naziv='" + naziv + "', godina=" + godinaNastanka
                + ", umjetnik=" + (umjetnik != null ? umjetnik.getImePrezime() : "null") + "}";
    }
}