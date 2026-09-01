package org.example.model;

public class MaterijalTehnika extends Entitet {

    private String opis;

    public MaterijalTehnika() {
        super();
    }

    public MaterijalTehnika(Long id, String naziv, String opis) {
        super(id, naziv);
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    @Override
    public String opisiSe() {
        return naziv;
    }

    @Override
    public String toString() {
        return "MaterijalTehnika{id=" + id + ", naziv='" + naziv + "'}";
    }
}