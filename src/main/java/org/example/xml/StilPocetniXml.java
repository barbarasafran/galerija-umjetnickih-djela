package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class StilPocetniXml {

    @XmlElement
    private String naziv;

    @XmlElement
    private String opis;

    @XmlElement
    private String razdoblje;

    public StilPocetniXml() {
    }

    public StilPocetniXml(String naziv, String opis, String razdoblje) {
        this.naziv = naziv;
        this.opis = opis;
        this.razdoblje = razdoblje;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getOpis() {
        return opis;
    }

    public String getRazdoblje() {
        return razdoblje;
    }
}