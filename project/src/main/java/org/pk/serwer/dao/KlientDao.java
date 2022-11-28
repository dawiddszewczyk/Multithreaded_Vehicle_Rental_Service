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
		
		try {
			sesja.save(klient);
		}catch(Exception wyjatek) {
			wyjatek.printStackTrace();
		}

		sesja.getTransaction().commit();
		sesja.close();
	}

	public String pobierzEmail(String podanyEmail) {
		String emailZBazy="";
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();

		try {
			Query query = sesja.createQuery("select K.email FROM klient K where K.email=:podanyEmail");
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

	public Klient logowanie(String podanyEmail, String podaneHaslo) {
		Klient klientZBazy=null;
		Session sesja = fabrykaSesji.getCurrentSession();
		sesja.beginTransaction();

		try {
			Query query = sesja.createQuery("FROM klient K where K.email=:podanyEmail");
			query.setParameter("podanyEmail", podanyEmail);
			klientZBazy = (Klient)query.getSingleResult();
			// sprawdzanie hasla
			BcryptFunction bcrypt = BcryptFunction.getInstanceFromHash(klientZBazy.getHaslo());
			if(!Password.check(podaneHaslo,klientZBazy.getHaslo())
						.with(bcrypt)) klientZBazy=null;
		}catch(NoResultException wyjatekNRE) {
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
			Query query=session.createQuery("SELECT p FROM pojazd p LEFT JOIN FETCH klient_pojazd k ON p.id=k.id WHERE k.dataZwr IS NULL");

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
