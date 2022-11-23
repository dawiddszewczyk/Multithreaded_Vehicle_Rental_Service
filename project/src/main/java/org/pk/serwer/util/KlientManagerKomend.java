package org.pk.serwer.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.NoResultException;

import org.pk.entity.Klient;
import org.pk.serwer.dao.KlientDao;

public class KlientManagerKomend {
	public static void wykonajKomende(String komenda, ObjectOutputStream doKlienta, ObjectInputStream odKlienta) throws ClassNotFoundException, IOException {
		switch (komenda) {
		case "stworzKlienta()":
			KlientDao.getInstance().stworzKlienta((Klient)odKlienta.readObject());
			System.out.println("Pomyslnie utworzono klienta!");
			break;
		case "pobierzEmail()":
			String emailZSerwera = KlientDao.getInstance().pobierzEmail((String)odKlienta.readObject());
			doKlienta.writeObject(emailZSerwera);
			doKlienta.flush();
			break;
		case "logowanie()":
			String emailOdKlienta = (String) odKlienta.readObject();
			String hasloOdKlienta = (String) odKlienta.readObject();
			Klient pobranyKlientZSerwera = KlientDao.getInstance()
										   .logowanie(emailOdKlienta,hasloOdKlienta);
			doKlienta.writeObject(pobranyKlientZSerwera);
			doKlienta.flush();
		default:
			break;
		}
	}
}
