package org.pk.serwer.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.serwer.dao.KlientDao;

public class KlientManagerKomend {
	public static void wykonajKomende(String komenda, ObjectOutputStream doKlienta, ObjectInputStream odKlienta) throws ClassNotFoundException, IOException {
		switch (komenda) {
		case "stworzKlienta()":
			KlientDao.getInstance().stworzKlienta((Klient)odKlienta.readObject());
			System.out.println("Pomyslnie utworzono klienta!");
			break;
			case "stworzWypozyczenie()":
				KlientDao.getInstance().stworzWypozyczenia((Wypozyczenie) odKlienta.readObject());
				System.out.println("Pomyslnie utworzono wypozyczenia!");
				break;
			case "zaktualizujPojazd()":
				KlientDao.getInstance().zaktualizujPojazd((Pojazd) odKlienta.readObject());
				System.out.println("Pomyslnie utworzono pojazd!");
				break;
			case "zaktualizujWypozyczenie()":
				KlientDao.getInstance().zaktualizujWypozyczenie((Wypozyczenie) odKlienta.readObject());
				System.out.println("Pomyslnie utworzono pojazd!");
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
			case "getList()":
				System.out.println("Manager");
				doKlienta.writeObject(KlientDao.getInstance().getList());
				doKlienta.flush();
				System.out.println("Wyslalem hulajnogi uwu");
				break;
			case "wyslijWypozyczenie()":

				break;
		default:
			break;
		}
	}
}
