package org.pk.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.action.internal.OrphanRemovalAction;

@Table(name="klient")
@Entity(name="klient")
public class Klient implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="idklienta")
	private int id;
	@Column(name="imie")
	private String imie;
	@Column(name="nazwisko")
	private String nazwisko;
	@Column(name="email")
	private String email;
	@Column(name="haslo")
	private String haslo;
	
	@OneToMany(
		mappedBy = "klient",
		cascade = CascadeType.ALL
	)
	private List<Wypozyczenie> wypozyczenia;
	
	public Klient() {
		
	}
	
	public Klient(String imie, String nazwisko, String email) {
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.email = email;
	}

	public Klient(int id, String imie, String nazwisko, String email) {
		this.id = id;
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.email = email;
	}
	
	public Klient(int id, String imie, String nazwisko, String email, String haslo) {
		this.id = id;
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.email = email;
		this.haslo = haslo;
	}
	
	public Klient(String imie, String nazwisko, String email, String haslo) {
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.email = email;
		this.haslo = haslo;
	}

	public void dodajPojazd(Pojazd pojazd) {
		if(wypozyczenia==null) wypozyczenia = new ArrayList();
		if(pojazd.getWypozyczenia()==null) pojazd.setWypozyczenia(new ArrayList());
		Wypozyczenie wypozyczenie = new Wypozyczenie(this, pojazd);
		this.wypozyczenia.add(wypozyczenie);
		pojazd.getWypozyczenia().add(wypozyczenie);
		
	}
	
	public void usunPojazd(Pojazd pojazd) {
		for(Wypozyczenie wypozyczenie : wypozyczenia) {
			if(wypozyczenie.getKlient().equals(this) &&
					wypozyczenie.getPojazd().equals(pojazd)) {
				this.wypozyczenia.remove(wypozyczenie);
				pojazd.getWypozyczenia().remove(wypozyczenie);
				wypozyczenie.setKlient(null);
				wypozyczenie.setPojazd(null);
			}
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImie() {
		return imie;
	}
	public void setImie(String imie) {
		this.imie = imie;
	}
	public String getNazwisko() {
		return nazwisko;
	}
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Wypozyczenie> getWypozyczenia() {
		return wypozyczenia;
	}
	public void setWypozyczenia(List<Wypozyczenie> wypozyczenia) {
		this.wypozyczenia = wypozyczenia;
	}
	public String getHaslo() {
		return haslo;
	}

	public void setHaslo(String haslo) {
		this.haslo = haslo;
	}

	@Override
	public String toString() {
		return "Klient [id=" + id + ", imie=" + imie + ", nazwisko=" + nazwisko + ", email=" + email + ", wypozyczenia="
				+ wypozyczenia + "]";
	}
}
