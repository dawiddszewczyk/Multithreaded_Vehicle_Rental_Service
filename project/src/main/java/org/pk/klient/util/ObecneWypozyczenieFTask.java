package org.pk.klient.util;

import java.util.concurrent.FutureTask;

/**
 * Klasa dziedzicząca po FutureTask. Pełni funkcję "kontrolera" wątku
 * wypożyczenia użytkownika. Używany w celu zakończenia wypożyczenia
 */
public class ObecneWypozyczenieFTask <V> extends FutureTask<V> {
	
	public ObecneWypozyczenieFTask(Runnable runnable, V result) {
		super(runnable, result);
	}

}
