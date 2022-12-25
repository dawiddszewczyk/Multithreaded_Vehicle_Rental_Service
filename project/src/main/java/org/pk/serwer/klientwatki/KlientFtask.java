package org.pk.serwer.klientwatki;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Klasa dziedzicząca po FutureTask, pełni rolę wrappera dla KlientCallable
 * w celu ewentualnego zatrzymania.
 */
public class KlientFtask <V> extends FutureTask<V> {

	@SuppressWarnings("unused")
	private KlientCallable<V> klientPolaczenie;
	
	public KlientFtask(Callable<V> klientPolaczenie) {
		super(klientPolaczenie);
		this.klientPolaczenie = (KlientCallable<V>) klientPolaczenie;
	}

}
