package org.pk.util;

import com.password4j.BcryptFunction;
import com.password4j.types.Bcrypt;

public class StaleWartosci {
	// Szyfrowanie
	public static final BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.Y,10);
	// Konfiguracja serwer/apka
	public static final int NUMER_PORTU = 40000;
	public static final String TYTUL_APKI = "Bolt cziler edyszyn";
	public static final String HIBERNATE_CONFIG = "hibernate_configs/hibernate.cfg.xml";
	public static final int DOSTEPNA_ILOSC_WATKOW = Runtime.getRuntime().availableProcessors();
	// Sciezki do plikow
	public static final String APP_VIEW_XML = "/fxml_files/AppView.fxml";
	public static final String REGISTER_VIEW_XML = "/fxml_files/RegisterView.fxml";
	public static final String LIST_VIEW_XML = "/fxml_files/ListView.fxml";
}
