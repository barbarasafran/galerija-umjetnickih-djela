package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class BazaConfig {

    @XmlElement
    private String connectionString;

    @XmlElement
    private String korisnik;

    @XmlElement
    private String lozinka;

    public String getConnectionString() {
        return connectionString;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public String getLozinka() {
        return lozinka;
    }
}