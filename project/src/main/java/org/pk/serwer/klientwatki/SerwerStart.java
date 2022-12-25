package org.pk.serwer.klientwatki;

import static org.pk.util.StaleWartosci.DOSTEPNA_ILOSC_WATKOW;
import static org.pk.util.StaleWartosci.NUMER_PORTU;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hibernate.SessionFactory;

/**
 * Klasa mająca statyczną metodę, do inicjalizacji pracy serwera. Metoda ta
 * jest wywoływana w klasie MainSerwer. Przyjmuje obiekt fabryki sesji i w niej
 * tworzony jest ServerSocket przyjmujący połączenia od potencjalnych klientów, oraz wątki
 * klasy KlientCallable. Po zakończeniu pracy (wystąpieniu wyjątku) wszystkie zasoby (w tym fabryka sesji)
 * są zwalniane
 */
public class SerwerStart {
	public static void start(SessionFactory fabrykaSesji){
		
		ServerSocket serwer = null;
		ExecutorService wykonawcaKlient = null;
		
		try {
			
			serwer = new ServerSocket(NUMER_PORTU);
			wykonawcaKlient = Executors.newFixedThreadPool(DOSTEPNA_ILOSC_WATKOW);
			serwer.setReuseAddress(true);
			
			// petla nasluchujaca na nowych klientow, .accept() blokuje dzialanie programu
			while(true) {
				Socket klient = serwer.accept();
				System.out.println("Klient podlaczony: " + klient.getInetAddress().getHostAddress());
				
				KlientCallable<Integer> klientPolaczenie = new KlientCallable<>(klient);
				// Obiekt futuretask do zarzadzania watkiem, wrapper dla polaczenia, mozliwe, ze bedzie bezuzyteczne w przyszlosci
				KlientFtask<Integer> klientFPolaczenie = new KlientFtask<>(klientPolaczenie);
				wykonawcaKlient.execute(klientFPolaczenie);
			}	
			
		}catch(IOException wyjatek) {
			wyjatek.printStackTrace();
		}finally {
			if(serwer!=null)
				try {
					serwer.close();
					wykonawcaKlient.shutdownNow();
					fabrykaSesji.close();
				} catch (IOException wyjatek) {
					wyjatek.printStackTrace();
				}
			
		}
	}
}
