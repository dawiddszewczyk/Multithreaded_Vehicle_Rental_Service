package org.pk.klient.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConnectionBox {

	private static ConnectionBox polaczenie;
	private ObjectInputStream odSerwera;
	private ObjectOutputStream doSerwera;
	
	private ConnectionBox(ObjectInputStream odSerwera, ObjectOutputStream doSerwera) {
		this.odSerwera=odSerwera;
		this.doSerwera=doSerwera;
	}
	
	public static ConnectionBox getInstance(ObjectInputStream odSerwera, ObjectOutputStream doSerwera) {
		if(polaczenie==null) polaczenie = new ConnectionBox(odSerwera, doSerwera);
		return polaczenie;
	}
	
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
}
