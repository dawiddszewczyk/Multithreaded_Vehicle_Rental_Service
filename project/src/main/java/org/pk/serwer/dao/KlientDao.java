package org.pk.serwer.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pk.entity.Klient;

public class KlientDao {

	private static KlientDao klientDao;
	private SessionFactory fabrykaSesji;
	
	
	public KlientDao(SessionFactory fabrykaSesji) {
		this.fabrykaSesji = fabrykaSesji;
	}


	public static KlientDao getInstance(SessionFactory fabrykaSesji) {
		if(klientDao==null) klientDao = new KlientDao(fabrykaSesji);
		return klientDao;
	}
	
	public void stworzKlienta(Klient klient) {
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		sesja.save(klient);
		
		sesja.getTransaction().commit();
		sesja.close();
	}
	
}
