package org.pk.serwer.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	
}
