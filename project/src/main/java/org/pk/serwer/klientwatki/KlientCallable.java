package org.pk.serwer.klientwatki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.pk.serwer.util.KlientManagerKomend;

/**
 * Klasa która pełni rolę wątku dla konkretnego klienta połączonego z serwerem.
 * Przechowuje ona id podpiętego klienta oraz połączenie z nim przez obiekt klasy Socket.
 * Wątek ten jest utrzymywany, dopóki klient nie zakończy pracy (lub też serwer). Przechwytuje
 * on rozkazy od klienta i komunikuje się z bazą danych.
 */
public class KlientCallable <V> implements Callable<V> {

	private final Socket klient;
	private int idKlienta;
	
	public KlientCallable(Socket klient) {
		this.klient = klient;
	}
	
	public int getIdKlienta() {
		return idKlienta;
	}
	public void setIdKlienta(int idKlienta) {
		this.idKlienta = idKlienta;
	}
	
	@Override
	public V call() throws Exception {
		ObjectOutputStream doKlienta = null;
		ObjectInputStream odKlienta = null;
		
		try {
			doKlienta = new ObjectOutputStream(klient.getOutputStream());
			odKlienta = new ObjectInputStream(klient.getInputStream());
			Object polecenieKlient;
			// logika klienta, miejsce do przechwytywania polecen
			while(!Thread.currentThread().isInterrupted()) {
				//klient.isConnected()
				polecenieKlient = odKlienta.readObject();
				// w strumieniu moze zostac jeszcze jeden obiekt, np. klient, dlatego przekazujemy strumienie
				KlientManagerKomend.wykonajKomende((String)polecenieKlient, doKlienta, odKlienta, this);
			}
		}catch (IOException wyjatek) {
			System.out.println("Klient rozlaczony: " +  klient.getInetAddress().getHostAddress());
			KlientManagerKomend.wykonajKomende("usuniecieWypozyczenPoNaglymWylaczeniuK()",
					doKlienta, odKlienta, this);
		}finally {
			try {
				if(doKlienta!=null) doKlienta.close();
				if(odKlienta!=null) odKlienta.close();
				klient.close();
			}catch(IOException wyjatek) {
				wyjatek.printStackTrace();
			}
		}	
		return null;
	}
}
