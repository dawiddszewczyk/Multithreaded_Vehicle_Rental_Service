package org.pk.serwer.dao;

import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.pk.entity.Hulajnoga;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;

import java.util.List;

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

	public List<Pojazd> getList(){
		System.out.println("Debug DAO");
		List<Pojazd> listaHulajnog=null;

		Session session = fabrykaSesji.getCurrentSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			Query query=session.createQuery("FROM pojazd");

			listaHulajnog= (List<Pojazd>) query.list();
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
