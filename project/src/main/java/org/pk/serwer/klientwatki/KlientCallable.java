package org.pk.serwer.klientwatki;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

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
		}catch(EOFException wyjatekStrumienia) {
			System.out.println("Klient rozlaczony: " +  klient.getInetAddress().getHostAddress());
		}catch (IOException wyjatek) {
			wyjatek.printStackTrace();
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
