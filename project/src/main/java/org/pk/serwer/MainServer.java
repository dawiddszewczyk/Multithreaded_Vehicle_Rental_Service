package org.pk.serwer;

import static org.pk.util.StaleWartosci.HIBERNATE_CONFIG;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.serwer.dao.KlientDao;
import org.pk.serwer.dao.PojazdDao;
import org.pk.serwer.dao.WypozyczenieDao;
import org.pk.serwer.klientwatki.SerwerStart;

/**
 * Klasa z metodą main inicjującą serwer. Należy uruchomić tylko jeden raz, jako zwykłą
 * aplikację javy. Tworzy ona instancje singletonów dao oraz tworzy obiekt dostarczający
 * sesje do bazy danych. Sesje te są wykorzystywane w DAO. Na samym końcu inicjuje metodę z klasy SerwerStart.
 */
public class MainServer {

	public static void main(String[] args) {
		// obiekt sluzacy do tworzenia sesji hibernate (pozniej za kazdym razem trzeba wywolywac sesje przy zapytaniach)
		SessionFactory fabrykaSesji = new Configuration().configure(HIBERNATE_CONFIG)
				 										 .addAnnotatedClass(Klient.class)
				 										 .addAnnotatedClass(Pojazd.class)
				 										 .addAnnotatedClass(Wypozyczenie.class)
				 										 .buildSessionFactory();
		// inicjalizacja singletonow dao
		KlientDao.getInstance(fabrykaSesji);
		WypozyczenieDao.getInstance(fabrykaSesji);
		PojazdDao.getInstance(fabrykaSesji);
		// przesylam fabryke sesji, by zamknac ja gdy serwer zostanie wylaczony
		SerwerStart.start(fabrykaSesji);
	}
}
