package org.pk.klient.util;

import java.util.concurrent.FutureTask;

public class ObecneWypozyczenieFTask <V> extends FutureTask<V> {
	
	public ObecneWypozyczenieFTask(Runnable runnable, V result) {
		super(runnable, result);
	}

}
