package org.pk.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Table(name="klient_pojazd")
@Entity(name="klient_pojazd")
public class Wypozyczenie {
	
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
	
}
