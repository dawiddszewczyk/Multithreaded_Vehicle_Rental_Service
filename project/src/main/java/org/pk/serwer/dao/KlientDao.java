package org.pk.serwer.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;

import java.util.List;

import com.password4j.BcryptFunction;
import com.password4j.Password;
import org.pk.entity.Wypozyczenie;

/**
 * Klasa (singleton) w której znajdują się metody operujące na bazie danych, które
 * są związane z klasą Klient
 */
public class KlientDao {

	private static KlientDao klientDao;
	private SessionFactory fabrykaSesji;
	
	private KlientDao(SessionFactory fabrykaSesji) {
		this.fabrykaSesji = fabrykaSesji;
	}

	/**
	 * Służy do inicjalizacji singletona, z wykorzystaniem obiektu fabrykiSesji stworzonego w MainServer
	 * @param fabrykaSesji obiekt klasy SessionFactory
	 * @return obiekt singleton KlientDao
	 */
	public static KlientDao getInstance(SessionFactory fabrykaSesji) {
		if(klientDao==null) klientDao = new KlientDao(fabrykaSesji);
		return klientDao;
	}
	
	/**
	 * @return obiekt singleton KlientDao
	 */
	public static KlientDao getInstance() {
		if(klientDao==null) return null;
		return klientDao;
	}
	
	/**
	 * Metoda służąca do stworzenia klienta w bazie danych
	 * @param klient gotowy obiekt klasy Klient
	 */
	public void stworzKlienta(Klient klient) {
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		try {
			sesja.save(klient);
		}catch(Exception wyjatek) {
			wyjatek.printStackTrace();
		}

		sesja.getTransaction().commit();
		sesja.close();
	}

	/**
	 * Metoda służąca do pobrania emaila z bazy danych.
	 * @param podanyEmail podany mail przez klienta
	 * @return email z bazy danych lub null
	 */
	public String pobierzEmail(String podanyEmail) {
		
		String emailZBazy="";
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();

		try {
			Query query = sesja.createQuery("select K.email FROM Klient K where K.email=:podanyEmail");
			query.setParameter("podanyEmail",podanyEmail);
			// zwroci wyjatek, jezeli od poczatku w bazie bedzie wiecej niz jeden taki sam mail!
			emailZBazy = (String)query.getSingleResult();
		}catch(NoResultException wyjatekNRE) {
			System.out.println("Nie znaleziono podanego adresu email");
		}catch(Exception wyjatek) {
			wyjatek.printStackTrace();
		}

		sesja.getTransaction().commit();
		sesja.close();
		return emailZBazy;
	}

	/**
	 * Metoda służąca do zalogowania użytkownika
	 * @param podanyEmail podany email
	 * @param podaneHaslo podane haslo
	 * @return obiekt klasy klient, jeżeli użytkownik podał poprawne dane. W przeciwnym wypadku null
	 */
	public Klient logowanie(String podanyEmail, String podaneHaslo) {
		
		Klient klientZBazy=null;
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();

		try {
			Query query = sesja.createQuery("FROM Klient K LEFT JOIN FETCH K.wypozyczenia W where K.email=:podanyEmail", Klient.class);
			query.setParameter("podanyEmail", podanyEmail);
			klientZBazy = (Klient)query.getSingleResult();
			// sprawdzanie hasla
			BcryptFunction bcrypt = BcryptFunction.getInstanceFromHash(klientZBazy.getHaslo());
			if(!Password.check(podaneHaslo,klientZBazy.getHaslo())
						.with(bcrypt)) klientZBazy=null;
			
		}catch(NoResultException wyjatekNre) {
			System.out.println("Nie znaleziono uzytkownika");
		}catch(Exception wyjatek) {
			wyjatek.printStackTrace();
		}
		
		sesja.getTransaction().commit();
		sesja.close();
		return klientZBazy;
	}

	public List<Pojazd> getList(){
		System.out.println("Debug DAO");
		List<Pojazd> listaHulajnog=null;

		Session session = fabrykaSesji.getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Query query=session.createQuery("FROM pojazd p LEFT JOIN FETCH p.wypozyczenia WHERE p.id NOT IN (SELECT k.pojazd.id FROM klient_pojazd k WHERE k.dataZwr IS NULL) AND p.stanBaterii > 0");

			listaHulajnog= (List<Pojazd>) query.getResultList();
			tx.commit();
		}
		catch (Exception e) {
			if (tx!=null) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return listaHulajnog;
	}
}
