package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LogZapisXml {

    @XmlElement
    private String vrijeme;

    @XmlElement
    private String korisnik;

    @XmlElement
    private String akcija;

    @XmlElement
    private String detalji;

    public LogZapisXml() {
    }

    public LogZapisXml(String vrijeme, String korisnik, String akcija, String detalji) {
        this.vrijeme = vrijeme;
        this.korisnik = korisnik;
        this.akcija = akcija;
        this.detalji = detalji;
    }
}