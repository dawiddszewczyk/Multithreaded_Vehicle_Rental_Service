package org.pk.klient.util;

import org.pk.entity.Klient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Klasa będąca singletonem. Przechowywuje referencję do najważniejszych obiektów
 * które są używane w całym programie klienckim. Przechowuje referencje takie jak:
 * - Object input stream (strumień klient-serwer)
 * - Object output stream (strumeiń klient-serwer)
 * - id obecnie zalogowanego klienta
 * - obiekt obecnie zalogowanego klienta
 * - wykonawca (executor service) używany w całym programie. Większość przycisków w interfejsie graficznym,
 * gdy zostanie wciśnięta, to tworzony jest dla nich osobny wątek by nie blokować działania programu. Wykonawca w tej klasie
 * zajmuje się wykonywaniem tych wątków.
 * - obiekt procesu obecnego wypożyczenia pojazdu użytkownika (w celu np. zatrzymania wypożyczenia)
 */
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
	
	/**
	 * Metoda służąca inicjalizacji singletona.
	 * @param odSerwera Połączenie przychodzące z serwera (ObjectInputStream)
	 * @param doSerwera Połączenie wychodzące do serwera (ObjectOutputStream)
	 * @param polaczenieSerwer Socket w którym znajduje się połączenie z serwerem (od strony klienta)
	 * @return instancję singletona ConnectionBox
	 */
	public static ConnectionBox getInstance(ObjectInputStream odSerwera, ObjectOutputStream doSerwera, Socket polaczenieSerwer) {
		if(polaczenie==null) polaczenie = new ConnectionBox(odSerwera, doSerwera, polaczenieSerwer);
		return polaczenie;
	}
	
	/**
	 * @return instancję singletona ConnectionBox
	 */
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

	/**
	 * Setter dla obiektu klienta oraz jego id
	 */
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

	/**
	 * Metoda służąca do zamknięcia wszelkich połączeń oraz wątków, wykorzystywanych przez klienta (poza wypożyczeniem)
	 */
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
