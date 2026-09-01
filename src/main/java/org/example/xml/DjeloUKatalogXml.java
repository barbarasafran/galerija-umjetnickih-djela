package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DjeloUKatalogXml {

    @XmlElement
    private String naziv;

    @XmlElement
    private int godinaNastanka;

    @XmlElement
    private String umjetnik;

    @XmlElement
    private String stil;

    @XmlElement
    private String materijal;

    public DjeloUKatalogXml() {
    }

    public DjeloUKatalogXml(String naziv, int godinaNastanka, String umjetnik, String stil, String materijal) {
        this.naziv = naziv;
        this.godinaNastanka = godinaNastanka;
        this.umjetnik = umjetnik;
        this.stil = stil;
        this.materijal = materijal;
    }
}