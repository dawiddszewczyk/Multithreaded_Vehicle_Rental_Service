package org.pk.serwer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;

public class MainServer {

	public static void main(String[] args) {
		SessionFactory fabrykaSesji = new Configuration().configure("hibernate.cfg.xml")
				 										 .addAnnotatedClass(Klient.class)
				 										 .addAnnotatedClass(Pojazd.class)
				 										 .addAnnotatedClass(Wypozyczenie.class)
				 										 .buildSessionFactory();

		Session sesja = fabrykaSesji.getCurrentSession();

		try {
			System.out.println("Tworze klienta");
			Klient tempKlient= new Klient("Adrian","Testowy","Rosol@gmail.com");
			System.out.println(tempKlient);
			System.out.println("Tworze pojazd");
			Pojazd tempPojazd = new Pojazd("Szybki",10.0,15.0);
			System.out.println(tempPojazd);
			System.out.println("Tworze wypozyczenie");
			tempKlient.dodajPojazd(tempPojazd);
			sesja.beginTransaction();
			System.out.println("Poczek transakcji");
			sesja.save(tempKlient);
			sesja.save(tempPojazd);
			sesja.getTransaction().commit();
			System.out.println("Koniec");
			

		}finally {
			fabrykaSesji.close();
		}
	}
}
