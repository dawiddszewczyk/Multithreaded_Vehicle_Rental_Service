package org.pk.serwer.klientwatki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.pk.entity.Klient;
import org.pk.serwer.dao.KlientDao;

public class KlientCallable <V> implements Callable<V> {

	private final Socket klient;
	private KlientDao klientDao;
	
	public KlientCallable(Socket klient) {
		this.klient = klient;
		this.klientDao = KlientDao.getInstance(null);
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
			while(klient.isConnected()) {
				polecenieKlient = odKlienta.readObject();
				if(polecenieKlient instanceof String) {
					System.out.println("Otrzymalem stringa!");
					if(((String)polecenieKlient).equals("stworzKlienta()")) {
						System.out.println("wewnatrz stworzKlienta!");
						polecenieKlient = odKlienta.readObject();
						if(polecenieKlient instanceof Klient)
							klientDao.stworzKlienta((Klient)polecenieKlient);
					}
				}
			}
		}catch (IOException wyjatek) {
			System.out.println("Wyjatek w klientcallable (nie wplywa na dzialanie serwera)!");
			//wyjatek.printStackTrace();
		}finally {
			try {
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
