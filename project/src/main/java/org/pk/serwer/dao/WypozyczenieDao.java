package org.pk.serwer.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;

public class WypozyczenieDao {

	private static WypozyczenieDao wypozyczenieDao;
	private SessionFactory fabrykaSesji;
	
	private WypozyczenieDao(SessionFactory fabrykaSesji) {
		this.fabrykaSesji = fabrykaSesji;
	}

	public static WypozyczenieDao getInstance(SessionFactory fabrykaSesji) {
		if(wypozyczenieDao==null) wypozyczenieDao = new WypozyczenieDao(fabrykaSesji);
		return wypozyczenieDao;
	}
	
	public static WypozyczenieDao getInstance() {
		if(wypozyczenieDao==null) return null;
		return wypozyczenieDao;
	}
	
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
