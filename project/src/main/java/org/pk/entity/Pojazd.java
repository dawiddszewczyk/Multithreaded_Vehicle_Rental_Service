package org.pk.entity;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name="pojazd")
@Entity
public class Pojazd implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="idpojazdu")
	private int id;
	@Column(name="nazwa")
	private String nazwa;
	@Column(name="stanbaterii")
	private double stanBaterii;
	@Column(name="licznikkm")
	private double licznikkm;

	@OneToMany(
		fetch = FetchType.LAZY,
		mappedBy = "pojazd",
		cascade = {CascadeType.MERGE, CascadeType.REMOVE}
	)
	private List<Wypozyczenie> wypozyczenia;

	// Pusty konstruktor dla Hibernate
	public Pojazd() {
		
	}

	public Pojazd(int id, String nazwa, double stanBaterii, double licznikkm) {
		this.id = id;
		this.nazwa = nazwa;
		this.stanBaterii = stanBaterii;
		this.licznikkm = licznikkm;

	}
	
	public Pojazd(String nazwa, double stanBaterii, double licznikkm) {
		this.nazwa = nazwa;
		this.stanBaterii = stanBaterii;
		this.licznikkm = licznikkm;
	}

	public void dodajWypozyczenie1S(Wypozyczenie wypozyczenie) {
		if(wypozyczenia==null) wypozyczenia=new ArrayList<>();
		wypozyczenia.add(wypozyczenie);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNazwa() {
		return nazwa;
	}
	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}
	public double getStanBaterii() {
		return stanBaterii;
	}
	public void setStanBaterii(double stanBaterii) {
		this.stanBaterii = stanBaterii;
	}
	public double getLicznikkm() {
		return licznikkm;
	}
	public void setLicznikkm(double licznikkm) {
		this.licznikkm = licznikkm;
	}
	public List<Wypozyczenie> getWypozyczenia() {
		return wypozyczenia;
	}
	public void setWypozyczenia(List<Wypozyczenie> wypozyczenia) {
		this.wypozyczenia = wypozyczenia;
	}
	@Override
	public String toString() {
		return "Pojazd [id=" + id + ", nazwa=" + nazwa + ", stanBaterii=" + stanBaterii + ", licznikkm=" + licznikkm
				+ ", wypozyczenia=" + wypozyczenia + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pojazd other = (Pojazd) obj;
		return id == other.id;
	}
}
