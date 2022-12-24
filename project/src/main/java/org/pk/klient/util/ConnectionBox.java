package org.pk.klient.util;

import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionBox {

	private static volatile ConnectionBox polaczenie;
	private final Socket polaczenieSerwer;
	private ObjectInputStream odSerwera;
	private ObjectOutputStream doSerwera;
	private int idKlienta;
	private Klient klient;
	private ExecutorService wykonawcaGlobalny;
	private ObecneWypozyczenieFTask<?> wypozyczenieFt;
	
	
	private ConnectionBox(ObjectInputStream odSerwera, ObjectOutputStream doSerwera, Socket polaczenieSerwer) {
		this.odSerwera=odSerwera;
		this.doSerwera=doSerwera;
		this.polaczenieSerwer = polaczenieSerwer;
		this.idKlienta=-1;
		this.klient=null;
		this.wykonawcaGlobalny=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	public static ConnectionBox getInstance(ObjectInputStream odSerwera, ObjectOutputStream doSerwera, Socket polaczenieSerwer) {
		if(polaczenie==null) polaczenie = new ConnectionBox(odSerwera, doSerwera, polaczenieSerwer);
		return polaczenie;
	}
	
	public static ConnectionBox getInstance() {
		if(polaczenie==null) return null;
		return polaczenie;
	}

	public ObjectInputStream getOdSerwera() {
		return odSerwera;
	}

	public ObjectOutputStream getDoSerwera() {
		return doSerwera;
	}

	public int getIdKlienta() {
		return idKlienta;
	}

	public Klient getKlient() {
		return klient;
	}

	public void setKlientIIdKlienta(int idKlienta,Klient klient) {
		this.idKlienta = idKlienta;
		this.klient=klient;
	}

	public Socket getPolaczenieSerwer() {
		return polaczenieSerwer;
	}

	public ExecutorService getWykonawcaGlobalny() {
		return wykonawcaGlobalny;
	}

	public void setWykonawcaGlobalny(ExecutorService wykonawcaGlobalny) {
		this.wykonawcaGlobalny = wykonawcaGlobalny;
	}
	
	public ObecneWypozyczenieFTask<?> getWypozyczenieFt() {
		return wypozyczenieFt;
	}

	public void setWypozyczenieFt(ObecneWypozyczenieFTask<?> wypozyczenieFt) {
		this.wypozyczenieFt = wypozyczenieFt;
	}

	public void zamknijPolaczenia() {
		this.wykonawcaGlobalny.shutdownNow();
		try {
			this.polaczenieSerwer.close();
			this.odSerwera.close();
			this.doSerwera.close();
		} catch (IOException wyjatekIO) {
			wyjatekIO.printStackTrace();
		}
	}
	
}
