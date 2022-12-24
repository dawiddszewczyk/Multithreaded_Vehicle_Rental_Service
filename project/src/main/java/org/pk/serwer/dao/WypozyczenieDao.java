package org.pk.serwer.dao;

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
	
	public void zaktualizujWypozyczenie(Wypozyczenie wypozyczenie){

		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		try {
			sesja.saveOrUpdate(wypozyczenie);
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
	
}
