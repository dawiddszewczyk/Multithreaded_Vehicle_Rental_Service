package org.pk.serwer.klientwatki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.pk.entity.Klient;
import org.pk.serwer.dao.KlientDao;
import org.pk.serwer.util.KlientManagerKomend;

public class KlientCallable <V> implements Callable<V> {

	private final Socket klient;
	
	public KlientCallable(Socket klient) {
		this.klient = klient;
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
				KlientManagerKomend.wykonajKomende((String)polecenieKlient, doKlienta, odKlienta);
			}
		}catch (IOException wyjatek) {
			System.out.println("Wyjatek w klientcallable (nie wplywa na dzialanie serwera)!");
			//wyjatek.printStackTrace();
		}finally {
			try {
				System.out.println("Klient zamykany! (Serwer)");
				if(doKlienta!=null) doKlienta.close();
				if(odKlienta!=null) odKlienta.close();
				klient.close();
			}catch(IOException wyjatek) {
				System.out.println("Wyjatek przy zamykaniu zasobow klienta!");
				wyjatek.printStackTrace();
			}
		}
		
		return null;
	}

}
