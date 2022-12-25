package org.pk.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Klasa służąca mapowaniu encji bazodanowej wypożyczenie na obiekt javy
 * jest połączona relacją n:1 z klientem i pojazdem
 */
@SuppressWarnings("serial")
@Table(name="klient_pojazd")
@Entity
public class Wypozyczenie implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="idwypozyczenia")
	private int id;
	@Column(name="data_wyp")
	private Timestamp dataWyp = new Timestamp(System.currentTimeMillis());
	@Column(name="data_zwr")
	private Timestamp dataZwr;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idklientaW")
	private Klient klient;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idpojazduW")
	private Pojazd pojazd;
	
	// Pusty konstruktor dla Hibernate
	public Wypozyczenie() {

	}

	public Wypozyczenie(Klient klient, Pojazd pojazd) {
		this.klient = klient;
		this.pojazd = pojazd;
	}

	public Timestamp getDataWyp() {
		return dataWyp;
	}

	public void setDataWyp(Timestamp dataWyp) {
		this.dataWyp = dataWyp;
	}

	public Timestamp getDataZwr() {
		return dataZwr;
	}

	public void setDataZwr(Timestamp dataZwr) {
		this.dataZwr = dataZwr;
	}

	public Klient getKlient() {
		return klient;
	}

	public void setKlient(Klient klient) {
		this.klient = klient;
	}

	public Pojazd getPojazd() {
		return pojazd;
	}

	public void setPojazd(Pojazd pojazd) {
		this.pojazd = pojazd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Wypozyczenie [idW=" + id + ", idK=" + klient.getId() + ", idP=" + pojazd.getId() + ", dataW=" + dataWyp + ", DataZ="
				+ dataZwr + "]";
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
		Wypozyczenie other = (Wypozyczenie) obj;
		return id == other.id;
	}
}
