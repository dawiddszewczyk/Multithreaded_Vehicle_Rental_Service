package org.pk.serwer.klientwatki;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class KlientFtask <V> extends FutureTask<V> {

	private KlientCallable<V> klientPolaczenie;
	
	public KlientFtask(Callable<V> klientPolaczenie) {
		super(klientPolaczenie);
		this.klientPolaczenie = (KlientCallable<V>) klientPolaczenie;
	}

}
