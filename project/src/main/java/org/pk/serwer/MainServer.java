package org.pk.serwer;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.serwer.dao.KlientDao;
import org.pk.serwer.klientwatki.SerwerStart;

public class MainServer {

	public static void main(String[] args) {
		// obiekt sluzacy do tworzenia sesji hibernate (pozniej za kazdym razem trzeba wywolywac sesje przy zapytaniach)
		SessionFactory fabrykaSesji = new Configuration().configure("hibernate_configs/hibernate.cfg.xml")
				 										 .addAnnotatedClass(Klient.class)
				 										 .addAnnotatedClass(Pojazd.class)
				 										 .addAnnotatedClass(Wypozyczenie.class)
				 										 .buildSessionFactory();
		// inicjalizacja singletona dao
		KlientDao.getInstance(fabrykaSesji);
		// przesylam fabryke sesji, by zamknac ja gdy serwer zostanie wylaczony
		SerwerStart.start(fabrykaSesji);
	}
}
