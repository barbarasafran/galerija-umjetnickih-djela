package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EkranConfig {

    @XmlElement
    private int sirina;

    @XmlElement
    private int visina;

    public int getSirina() {
        return sirina;
    }

    public int getVisina() {
        return visina;
    }
}