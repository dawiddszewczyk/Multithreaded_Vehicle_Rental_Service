package org.pk.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name="klient_pojazd")
@Entity(name="klient_pojazd")
public class Wypozyczenie implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="idwypozyczenia")
	private int id;
	@Column(name="data_wyp")
	private Date dataWyp = new Date(System.currentTimeMillis());
	@Column(name="data_zwr")
	private Date dataZwr;
	
	@ManyToOne
	@JoinColumn(name="idklientaW")
	private Klient klient;
	
	@ManyToOne
	@JoinColumn(name="idpojazduW")
	private Pojazd pojazd;
	
	// Pusty konstruktor dla Hibernate
	public Wypozyczenie() {

	}

	public Wypozyczenie(Klient klient, Pojazd pojazd) {
		this.klient = klient;
		this.pojazd = pojazd;
	}

	public Date getDataWyp() {
		return dataWyp;
	}

	public void setDataWyp(Date dataWyp) {
		this.dataWyp = dataWyp;
	}

	public Date getDataZwr() {
		return dataZwr;
	}

	public void setDataZwr(Date dataZwr) {
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
}
