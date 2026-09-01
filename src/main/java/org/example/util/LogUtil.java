package org.example.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.example.xml.LogXml;
import org.example.xml.LogZapisXml;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LogUtil {

    private static final String LOG_DATOTEKA =
            System.getProperty("user.home") + File.separator + "galerija-app-log.xml";

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");

    private LogUtil() {
    }

    public static synchronized void zabiljezi(String korisnickoIme, String akcija, String detalji) {
        try {
            LogXml log = ucitajPostojeciLog();

            String vrijeme = LocalDateTime.now().format(FORMAT);
            log.getZapisi().add(new LogZapisXml(vrijeme, korisnickoIme, akcija, detalji));

            spremiLog(log);
        } catch (Exception e) {
            System.err.println("Greška pri pisanju log zapisa: " + e.getMessage());
        }
    }

    private static LogXml ucitajPostojeciLog() throws JAXBException {
        File datoteka = new File(LOG_DATOTEKA);
        if (!datoteka.exists()) {
            return new LogXml();
        }

        JAXBContext context = JAXBContext.newInstance(LogXml.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (LogXml) unmarshaller.unmarshal(datoteka);
    }

    private static void spremiLog(LogXml log) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(LogXml.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(log, new File(LOG_DATOTEKA));
    }
}