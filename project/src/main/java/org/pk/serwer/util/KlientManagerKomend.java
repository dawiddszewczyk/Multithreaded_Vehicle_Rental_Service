package org.pk.serwer.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.klient.util.ConnectionBox;
import org.pk.serwer.dao.KlientDao;
import org.pk.serwer.dao.PojazdDao;
import org.pk.serwer.dao.WypozyczenieDao;

public class KlientManagerKomend {
	public static void wykonajKomende(String komenda, ObjectOutputStream doKlienta, ObjectInputStream odKlienta) throws ClassNotFoundException, IOException {
		
		switch (komenda) {
		
			case "stworzKlienta()":
				KlientDao.getInstance().stworzKlienta((Klient)odKlienta.readObject());
				System.out.println("Pomyslnie utworzono klienta!");
				break;
				
			case "stworzWypozyczenie()":
				Wypozyczenie dbWypozyczenie = WypozyczenieDao.getInstance().stworzWypozyczenie((Wypozyczenie) odKlienta.readObject());
				System.out.println("Pomyslnie utworzono wypozyczenie!");
				doKlienta.flush();
				doKlienta.reset();
				doKlienta.writeObject(dbWypozyczenie);
				doKlienta.flush();
				doKlienta.reset();
				break;
				
			case "zaktualizujPojazd()":
				PojazdDao.getInstance().zaktualizujPojazd((Pojazd) odKlienta.readObject());
				System.out.println("Pomyslnie zaktualizowano pojazd!");
				break;
				
			case "zaktualizujWypozyczenie()":
				WypozyczenieDao.getInstance().zaktualizujWypozyczenie((Wypozyczenie) odKlienta.readObject());
				System.out.println("Pomyslnie zaktualizowano wypozyczenie!");
				break;
				
			case "pobierzEmail()":
				String emailZSerwera = KlientDao.getInstance().pobierzEmail((String)odKlienta.readObject());
				doKlienta.flush();
				doKlienta.reset();
				doKlienta.writeObject(emailZSerwera);
				doKlienta.flush();
				doKlienta.reset();
				break;
				
			case "logowanie()":
				String emailOdKlienta = (String) odKlienta.readObject();
				String hasloOdKlienta = (String) odKlienta.readObject();
				Klient pobranyKlientZSerwera = KlientDao.getInstance()
						.logowanie(emailOdKlienta,hasloOdKlienta);
				doKlienta.flush();
				doKlienta.reset();
				doKlienta.writeObject(pobranyKlientZSerwera);
				doKlienta.flush();
				doKlienta.reset();
				
			case "getListaPojazdow(false)":
				doKlienta.flush();
				doKlienta.reset();
				doKlienta.writeObject(PojazdDao.getInstance().getListaPojazdow(0,false));
				doKlienta.flush();
				doKlienta.reset();
				break;
			
			case "getListaPojazdow(true)":
				int idPojazdu = (int) odKlienta.readObject();
				doKlienta.flush();
				doKlienta.reset();
				doKlienta.writeObject(PojazdDao.getInstance().getListaPojazdow(idPojazdu, true));
				doKlienta.flush();
				doKlienta.reset();
				break;
				
			case "sprawdzDostepnosc()":
				doKlienta.flush();
				doKlienta.reset();
				doKlienta.writeObject(
						WypozyczenieDao.getInstance()
						.sprawdzDostepnosc((Pojazd)odKlienta.readObject()));
				doKlienta.flush();
				doKlienta.reset();
			default:
				break;
		}
	}
}
