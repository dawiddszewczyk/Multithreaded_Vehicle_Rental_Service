package org.pk.serwer.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.pk.entity.Klient;
import org.pk.serwer.dao.KlientDao;

public class KlientManagerKomend {
	public static void wykonajKomende(String komenda, ObjectOutputStream doKlienta, ObjectInputStream odKlienta) throws ClassNotFoundException, IOException {
		switch (komenda) {
		case "stworzKlienta()":
			KlientDao.getInstance().stworzKlienta((Klient)odKlienta.readObject());
			System.out.println("Pomyslnie utworzono klienta!");
			break;
			case "getList()":
				System.out.println("Manager");
				KlientDao.getInstance().getList();
				break;
		default:
			break;
		}
	}
}
