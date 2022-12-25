package org.pk.klient.fxcontrollers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.klient.util.ConnectionBox;
import org.pk.klient.util.ObecneWypozyczenieFTask;

import static org.pk.util.StaleWartosci.LIST_VIEW_XML;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Klasa będąca łącznikiem między interfejsem javafx (tutaj: HulajnogaView.fxml -
 * okno wypożyczenia hulajnogi) a programem
 */
public class HulajnogaController {
    
	private Stage stage;
	private Scene scene;
	private Parent kontener;
	
    @FXML
    private TextField TextAreaBattery;

    @FXML
    private TextField TextAreaId;

    @FXML
    private TextField TextAreaName;

    @FXML
    private TextField TextAreaRange;
    
    /**
     * Metoda służąca do zmiany wartości kontrolek id, nazwy pojazdu, baterii oraz zasięgu podczas wypożyczenia
     * @param wypozyczonyPojazd obiekt pojazdu zaciągnięty z wypożyczenia w metodzie zmienStan()
     */
    public void ustawWartosci(Pojazd wypozyczonyPojazd){
        TextAreaId.setText(Integer.toString(wypozyczonyPojazd.getId()));
        TextAreaName.setText(wypozyczonyPojazd.getNazwa());
        TextAreaBattery.setText(Double.toString(wypozyczonyPojazd.getStanBaterii()));
        TextAreaRange.setText(Double.toString(wypozyczonyPojazd.getLicznikkm()));
    }
    
    /**
     * Metoda służąca do przeprowadzenia procesu wypożyczenia. Jest to wątek który co 1s aktualizuje dane wypożyczenia.
     * Nalicza użytkownikowi koszt, zmniejsza zasoby hulajnogi.
     * @param wypozyczenie uzupełniony obiekt wypożyczenia o klienta oraz pojazd.
     * @throws InterruptedException wyjątek jest wyrzucany/przechwytywany, gdy klient zakończy wypożyczenie
     * lub statystyki hulajnogi (stan baterii) będzie mniejszy lub równy 0.
     */
    public void procesWypozyczenia(Wypozyczenie wypozyczenie) throws InterruptedException {
    	
    	Runnable watek = () ->{
    		while(!Thread.currentThread().isInterrupted()) {
    			try {
					Thread.sleep(1000);
                    if(wypozyczenie.getPojazd().getStanBaterii()<=0){
                        throw new InterruptedException();
                    }else{
                        BigDecimal stanBaterii = BigDecimal.valueOf(wypozyczenie.getPojazd().getStanBaterii()).subtract(new BigDecimal("0.1"));
                        BigDecimal licznikKm = BigDecimal.valueOf(wypozyczenie.getPojazd().getLicznikkm()).subtract(new BigDecimal("0.02"));
                        BigDecimal zadluzenie = BigDecimal.valueOf(wypozyczenie.getKlient().getZadluzenie()).add(new BigDecimal("0.01"));
                        wypozyczenie.getPojazd().setStanBaterii(stanBaterii.doubleValue());
                        wypozyczenie.getPojazd().setLicznikkm(licznikKm.doubleValue());
                        wypozyczenie.getKlient().setZadluzenie(zadluzenie.doubleValue());
                    }
				} catch (InterruptedException wyjatekIE) {
                    try {
                    	wypozyczenie.setDataZwr(new Timestamp(System.currentTimeMillis()));
                        // reset konieczny z uwagi na odczyt pojazdu sprzed przypisania wypozyczeniu id
                        ConnectionBox.getInstance().getDoSerwera().flush();
                        ConnectionBox.getInstance().getDoSerwera().reset();
                        ConnectionBox.getInstance().getDoSerwera().writeObject("zaktualizujWypozyczenie()");
                        ConnectionBox.getInstance().getDoSerwera().writeObject(wypozyczenie);
                        ConnectionBox.getInstance().getDoSerwera().flush();

                    } catch (IOException wyjatekIo) {
                        wyjatekIo.printStackTrace();
                    }
                    return;
				}
    			Platform.runLater(()->{
    				ustawWartosci(wypozyczenie.getPojazd());
    			});
    		}
    	};
    	ObecneWypozyczenieFTask<?> wypozyczenieFTask = new ObecneWypozyczenieFTask<>(watek, null);
    	ConnectionBox.getInstance().setWypozyczenieFt(wypozyczenieFTask);
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(wypozyczenieFTask);
    }
    
    /**
     * Metoda obsługująca przycisk zakończenia wypożyczenia w interfejsie graficznym
     * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
     */
    @FXML
    public void zakonczWypozyczenie(ActionEvent zdarzenie){
    	
		Alert alert = new Alert(AlertType.CONFIRMATION, "Czy na pewno chcesz zakończyć wypożyczenie?",
				ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle("Potwierdzenie");
		alert.setHeaderText("Potwierdz zakończenie wypożyczenia");
		
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Potwierdz");
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
		alert.showAndWait();
		
		if(alert.getResult() == ButtonType.CANCEL)
			return;
    	
    	Runnable watek = () -> {
    		ConnectionBox.getInstance().getWypozyczenieFt().cancel(true);
    		
			// inicjalizacja listy z dostepnymi pojazdami
			FXMLLoader loader = new FXMLLoader(getClass().getResource(LIST_VIEW_XML));
			Platform.runLater(()->{
				try {
					kontener = loader.load();
				} catch (IOException wyjatekIo) {
					wyjatekIo.printStackTrace();
				}
				stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
				scene = new Scene(kontener);
				stage.setScene(scene);
				
				ListController listController = loader.getController();
				listController.pobierzListe(zdarzenie);
			
				stage.show();
			});
    	};
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
    }

}