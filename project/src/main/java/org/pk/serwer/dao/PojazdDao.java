package org.pk.serwer.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pk.entity.Pojazd;

public class PojazdDao {
	
	private static PojazdDao pojazdDao;
	private SessionFactory fabrykaSesji;
	
	private PojazdDao(SessionFactory fabrykaSesji) {
		this.fabrykaSesji = fabrykaSesji;
	}

	public static PojazdDao getInstance(SessionFactory fabrykaSesji) {
		if(pojazdDao==null) pojazdDao = new PojazdDao(fabrykaSesji);
		return pojazdDao;
	}
	
	public static PojazdDao getInstance() {
		if(pojazdDao==null) return null;
		return pojazdDao;
	}
	
	public void zaktualizujPojazd(Pojazd pojazd){
		
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();
		
		try {
			sesja.saveOrUpdate(pojazd);
			sesja.flush();
		}
		catch (Exception wyjatek) {
			wyjatek.printStackTrace();
		}
		
		sesja.getTransaction().commit();
		sesja.close();
	}
	
	@SuppressWarnings("unchecked")
	public List<Pojazd> getListaPojazdow(int idPojazdu, boolean uzyjId){
		
		List<Pojazd> listaHulajnog=null;
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();

		try {
			Query query = null;
			if(!uzyjId)
				query = sesja.createQuery("select distinct p from Pojazd p LEFT JOIN FETCH p.wypozyczenia "
						+ "WHERE p.id NOT IN (SELECT k.pojazd.id FROM Wypozyczenie k WHERE k.dataZwr IS NULL) "
						+ "AND p.stanBaterii > 0", 
						Pojazd.class);
			else {
				query = sesja.createQuery("select distinct p from Pojazd p LEFT JOIN FETCH p.wypozyczenia "
						+ "WHERE p.id NOT IN (SELECT k.pojazd.id FROM Wypozyczenie k WHERE k.dataZwr IS NULL) "
						+ "AND p.stanBaterii > 0 "
						+ "AND p.id=:idPojazdu", 
						Pojazd.class);
				query.setParameter("idPojazdu", idPojazdu);
			}
			
			listaHulajnog= (List<Pojazd>) query.getResultList();
		}
		catch (NoResultException brakWyniku) {
			System.out.println("Nie znaleziono listy");
		}
		catch (Exception wyjatek) {
			wyjatek.printStackTrace();
		}
		
		sesja.getTransaction().commit();
		sesja.close();
		return listaHulajnog;
	}
}
