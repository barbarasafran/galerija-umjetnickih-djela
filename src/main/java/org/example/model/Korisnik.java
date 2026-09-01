package org.example.model;

public class Korisnik extends Entitet {

    private String korisnickoIme;
    private String lozinkaHash;
    private Uloga uloga;

    public Korisnik() {
        super();
    }

    public Korisnik(Long id, String korisnickoIme, String lozinkaHash, Uloga uloga) {
        super(id, korisnickoIme); // naziv = korisničko ime, korisno za prikaz/sortiranje
        this.korisnickoIme = korisnickoIme;
        this.lozinkaHash = lozinkaHash;
        this.uloga = uloga;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
        this.naziv = korisnickoIme;
    }

    public String getLozinkaHash() {
        return lozinkaHash;
    }

    public void setLozinkaHash(String lozinkaHash) {
        this.lozinkaHash = lozinkaHash;
    }

    public Uloga getUloga() {
        return uloga;
    }

    public void setUloga(Uloga uloga) {
        this.uloga = uloga;
    }

    public boolean jeAdministrator() {
        return uloga == Uloga.ADMINISTRATOR;
    }

    @Override
    public String opisiSe() {
        return korisnickoIme + " (" + uloga + ")";
    }

    @Override
    public String toString() {
        return "Korisnik{id=" + id + ", korisnickoIme='" + korisnickoIme + "', uloga=" + uloga + "}";
    }
}