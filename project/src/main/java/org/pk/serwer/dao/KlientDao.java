package org.pk.serwer.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pk.entity.Klient;

public class KlientDao {

	private static KlientDao klientDao;
	private SessionFactory fabrykaSesji;
	
	
	private KlientDao(SessionFactory fabrykaSesji) {
		this.fabrykaSesji = fabrykaSesji;
	}


	public static KlientDao getInstance(SessionFactory fabrykaSesji) {
		if(klientDao==null) klientDao = new KlientDao(fabrykaSesji);
		return klientDao;
	}
	
	public static KlientDao getInstance() {
		if(klientDao==null) return null;
		return klientDao;
	}
	
	public void stworzKlienta(Klient klient) {
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		sesja.save(klient);
		
		sesja.getTransaction().commit();
		sesja.close();
	}
	
	public String pobierzEmail(String podanyEmail) {
		String emailZBazy="";
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		String queryInside="select K.email FROM klient K where K.email=:podanyEmail";
		//String queryInside2="select email FROM klient where email=:podanyEmail limit 1";
		try {
			Query query = sesja.createQuery(queryInside);
			query.setParameter("podanyEmail",podanyEmail);
			// zwroci wyjatek, jezeli od poczatku w bazie bedzie wiecej niz jeden taki sam mail!
			emailZBazy = (String)query.getSingleResult();
		}
		catch(Exception e) {
			System.out.println("Very powerful exception :)");
			e.printStackTrace();
		}
		
		sesja.getTransaction().commit();
		sesja.close();
		return emailZBazy;
	}
	
}
