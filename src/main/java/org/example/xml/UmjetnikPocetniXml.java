package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class UmjetnikPocetniXml {

    @XmlElement
    private String ime;

    @XmlElement
    private String prezime;

    @XmlElement
    private String drzava;

    @XmlElement
    private String biografija;

    public UmjetnikPocetniXml() {
    }

    public UmjetnikPocetniXml(String ime, String prezime, String drzava, String biografija) {
        this.ime = ime;
        this.prezime = prezime;
        this.drzava = drzava;
        this.biografija = biografija;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getDrzava() {
        return drzava;
    }

    public String getBiografija() {
        return biografija;
    }
}