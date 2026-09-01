package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "umjetnik")
@XmlAccessorType(XmlAccessType.FIELD)
public class UmjetnikXml {

    @XmlElement
    private String ime;

    @XmlElement
    private String prezime;

    @XmlElement
    private String drzava;

    public UmjetnikXml() {
    }

    public UmjetnikXml(String ime, String prezime, String drzava) {
        this.ime = ime;
        this.prezime = prezime;
        this.drzava = drzava;
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
}