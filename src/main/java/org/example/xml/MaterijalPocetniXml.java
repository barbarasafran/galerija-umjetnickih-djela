package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MaterijalPocetniXml {

    @XmlElement
    private String naziv;

    @XmlElement
    private String opis;

    public MaterijalPocetniXml() {
    }

    public MaterijalPocetniXml(String naziv, String opis) {
        this.naziv = naziv;
        this.opis = opis;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getOpis() {
        return opis;
    }
}