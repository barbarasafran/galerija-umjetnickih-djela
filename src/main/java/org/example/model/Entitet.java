package org.example.model;

import java.util.Objects;
public abstract class Entitet implements Comparable<Entitet> {

    protected Long id;
    protected String naziv;

    public Entitet() {
    }

    public Entitet(Long id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public abstract String opisiSe();

    @Override
    public int compareTo(Entitet other) {
        if (this.naziv == null || other.naziv == null) {
            return 0;
        }
        return this.naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entitet entitet = (Entitet) o;
        return Objects.equals(id, entitet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", naziv='" + naziv + "'}";
    }
}