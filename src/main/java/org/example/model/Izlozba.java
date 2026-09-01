package org.example.model;

import java.time.LocalDate;

public class Izlozba extends Entitet {

    private String lokacija;
    private LocalDate datumPocetka;
    private LocalDate datumZavrsetka;
    private String opis;

    public Izlozba() {
        super();
    }

    public Izlozba(Long id, String naziv, String lokacija, LocalDate datumPocetka,
                   LocalDate datumZavrsetka, String opis) {
        super(id, naziv);
        this.lokacija = lokacija;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.opis = opis;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public LocalDate getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(LocalDate datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public LocalDate getDatumZavrsetka() {
        return datumZavrsetka;
    }

    public void setDatumZavrsetka(LocalDate datumZavrsetka) {
        this.datumZavrsetka = datumZavrsetka;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public boolean jeAktivna() {
        LocalDate danas = LocalDate.now();
        if (datumPocetka == null || datumZavrsetka == null) {
            return false;
        }
        return !danas.isBefore(datumPocetka) && !danas.isAfter(datumZavrsetka);
    }

    @Override
    public String opisiSe() {
        return naziv + " (" + lokacija + ", " + datumPocetka + " - " + datumZavrsetka + ")";
    }

    @Override
    public String toString() {
        return "Izlozba{id=" + id + ", naziv='" + naziv + "', lokacija='" + lokacija + "'}";
    }
}