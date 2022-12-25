package org.pk.serwer.dao;

import java.math.BigDecimal;

import java.sql.Timestamp;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;

/**
 * Klasa (singleton) w której znajdują się metody operujące na bazie danych, które
 * są związane z klasą Wypozyczenie
 */
public class WypozyczenieDao {

	private static WypozyczenieDao wypozyczenieDao;
	private SessionFactory fabrykaSesji;
	
	private WypozyczenieDao(SessionFactory fabrykaSesji) {
		this.fabrykaSesji = fabrykaSesji;
	}
	
	/**
	 * Służy do inicjalizacji singletona, z wykorzystaniem obiektu fabrykiSesji stworzonego w MainServer
	 * @param fabrykaSesji obiekt klasy SessionFactory
	 * @return obiekt singleton WypozyczenieDao
	 */
	public static WypozyczenieDao getInstance(SessionFactory fabrykaSesji) {
		if(wypozyczenieDao==null) wypozyczenieDao = new WypozyczenieDao(fabrykaSesji);
		return wypozyczenieDao;
	}
	
	/**
	 * @return obiekt singleton WypozyczenieDao
	 */
	public static WypozyczenieDao getInstance() {
		if(wypozyczenieDao==null) return null;
		return wypozyczenieDao;
	}
	
	/**
	 * Metoda służaca do stworzenia wypożyczenia w bazie danych
	 * @param wypozyczenie gotowy obiekt klasy Wypozyczenie
	 * @return stworzone wypożyczenie, w celu pobrania z niego id (z bazy danych) po stronie klienta
	 */
	public Wypozyczenie stworzWypozyczenie(Wypozyczenie wypozyczenie) {
		
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		try {
			wypozyczenie=(Wypozyczenie)sesja.merge(wypozyczenie);
		}catch(Exception wyjatek) {
			wyjatek.printStackTrace();
		}

		sesja.getTransaction().commit();
		sesja.close();
		return wypozyczenie;
	}
	
	/**
	 * Metoda do aktualizacji wypożyczenia. Przy okazji są aktualizowane także obiekty
	 * pojazdu i klienta, powiązane z wypożyczeniem
	 * @param wypozyczenie gotowy obiekt klasy Wypozyczenie 
	 */
	public void zaktualizujWypozyczenie(Wypozyczenie wypozyczenie) {

		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		try {
			sesja.saveOrUpdate(wypozyczenie);
			sesja.saveOrUpdate(wypozyczenie.getKlient());
			sesja.saveOrUpdate(wypozyczenie.getPojazd());
			sesja.flush();
		}
		catch (Exception wyjatek) {
			wyjatek.printStackTrace();
		} 
		
		sesja.getTransaction().commit();
		sesja.close();
	}
	
	/**
	 * Metoda do sprawdzenia, czy dany pojazd jest dostępny (czy nie jest obecnie wypożyczony)
	 * @param pojazd do sprawdzenia, obiekt klasy Pojazd
	 * @return flagę boolean true - dostępny, false - zajęty
	 */
	public Boolean sprawdzDostepnosc(Pojazd pojazd) {
		
		Session sesja = fabrykaSesji.getCurrentSession();
		boolean dostepny=false;
		
		sesja.beginTransaction();
		
		try {
			Query query = sesja.createQuery("select w from Wypozyczenie w where "
					+ "w.pojazd.id=:idPojazdu and w.dataZwr is null", Wypozyczenie.class);
			query.setParameter("idPojazdu", pojazd.getId());
			query.getSingleResult();
		}
		catch (NoResultException brakWyniku) {
			dostepny=true;
		}
		catch (Exception wyjatek) {
			wyjatek.printStackTrace();
		} 
		
		sesja.getTransaction().commit();
		sesja.close();
		
		return dostepny;
	}
	
	/**
	 * Metoda służąca do skoordynowania z serwerem wypożyczenia, gdy klient w sposób nagły/nieprzewidywalny wyłączy aplikację
	 * @param idKlienta id klienta, który zakończył działanie aplikacji
	 */
	public void usuniecieWypozyczenPoNaglymWylaczeniu(int idKlienta) {
		
		Session sesja = fabrykaSesji.getCurrentSession();
		Timestamp obecnaData = new Timestamp(System.currentTimeMillis());
		sesja.beginTransaction();
		try {
			Query query = sesja.createQuery("select w from Wypozyczenie w "
					+ "left join fetch w.pojazd "
					+ "left join fetch w.klient where "
					+ "w.dataZwr is null and w.klient.id=:idKlienta");
			query.setParameter("idKlienta", idKlienta);
			Wypozyczenie wypozyczenieDoAktualizacji = (Wypozyczenie) query.getSingleResult();
			
			long roznicaWypZwrWSek = (obecnaData.getTime()/1000) -
					(wypozyczenieDoAktualizacji.getDataWyp().getTime()/1000);
			
            BigDecimal stanBaterii = BigDecimal.valueOf(wypozyczenieDoAktualizacji
            		.getPojazd().getStanBaterii()).subtract(new BigDecimal("0.1")
            				.multiply(new BigDecimal(roznicaWypZwrWSek)));

            BigDecimal licznikKm = BigDecimal.valueOf(wypozyczenieDoAktualizacji
            		.getPojazd().getLicznikkm()).subtract(new BigDecimal("0.02")
            				.multiply(new BigDecimal(roznicaWypZwrWSek)));
            
            BigDecimal zadluzenie = BigDecimal.valueOf(wypozyczenieDoAktualizacji
            		.getKlient().getZadluzenie()).add(new BigDecimal("0.01")
            				.multiply(new BigDecimal(roznicaWypZwrWSek)));
            
            
			wypozyczenieDoAktualizacji.setDataZwr(obecnaData);
			wypozyczenieDoAktualizacji.getPojazd().setStanBaterii(stanBaterii.doubleValue());
			wypozyczenieDoAktualizacji.getPojazd().setLicznikkm(licznikKm.doubleValue());
			wypozyczenieDoAktualizacji.getKlient().setZadluzenie(zadluzenie.doubleValue());
			sesja.update(wypozyczenieDoAktualizacji);
			sesja.update(wypozyczenieDoAktualizacji.getPojazd());
			sesja.update(wypozyczenieDoAktualizacji.getKlient());
		}
		catch (NoResultException brakWyniku) {
			System.out.println("Nie znaleziono wypozyczenia do usuniecia z powodu abort'u uzytkownika");
		}
		catch (Exception wyjatek) {
			wyjatek.printStackTrace();
		} 
		
		sesja.getTransaction().commit();
		sesja.close();
	}
}
