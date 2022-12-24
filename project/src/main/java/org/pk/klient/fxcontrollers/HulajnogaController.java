package org.pk.klient.fxcontrollers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.klient.util.ConnectionBox;
import org.pk.klient.util.ObecneWypozyczenieFTask;

import static org.pk.util.StaleWartosci.LIST_VIEW_XML;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

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
    
    public void ustawWartosci(Pojazd temp){
        TextAreaId.setText(Integer.toString(temp.getId()));
        TextAreaName.setText(temp.getNazwa());
        TextAreaBattery.setText(Double.toString(temp.getStanBaterii()));
        TextAreaRange.setText(Double.toString(temp.getLicznikkm()));
    }
    
    public void zmienStan(Wypozyczenie temp) throws InterruptedException {
    	
    	Runnable watek = () ->{
    		while(!Thread.currentThread().isInterrupted()) {
    			try {
					Thread.sleep(1000);
                    if(temp.getPojazd().getStanBaterii()<=0){
                        return;
                    }else{
                        BigDecimal stanBaterii = BigDecimal.valueOf(temp.getPojazd().getStanBaterii()).subtract(new BigDecimal("0.1"));
                        BigDecimal licznikKm = BigDecimal.valueOf(temp.getPojazd().getLicznikkm()).subtract(new BigDecimal("0.02"));
                        temp.getPojazd().setStanBaterii(stanBaterii.doubleValue());
                        temp.getPojazd().setLicznikkm(licznikKm.doubleValue());
                    }
				} catch (InterruptedException wyjatekIE) {
                    try {
                        wyjatekIE.printStackTrace();
                        temp.setDataZwr(new Date(System.currentTimeMillis()));
                        
                        // reset konieczny z uwagi na odczyt pojazdu sprzed przypisania wypozyczeniu id
                        ConnectionBox.getInstance().getDoSerwera().flush();
                        ConnectionBox.getInstance().getDoSerwera().reset();
                        ConnectionBox.getInstance().getDoSerwera().writeObject("zaktualizujPojazd()");
                        ConnectionBox.getInstance().getDoSerwera().writeObject(temp.getPojazd());
                        ConnectionBox.getInstance().getDoSerwera().flush();

                    } catch (IOException wyjatekIo) {
                        wyjatekIo.printStackTrace();
                    }
                    return;
				}
    			Platform.runLater(()->{
    				ustawWartosci(temp.getPojazd());
    			});
    		}
    	};
    	ObecneWypozyczenieFTask<?> wypozyczenieFTask = new ObecneWypozyczenieFTask<>(watek, null);
    	ConnectionBox.getInstance().setWypozyczenieFt(wypozyczenieFTask);
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(wypozyczenieFTask);
    }
    
    @FXML
    public void zakonczWypozyczenie(ActionEvent zdarzenie){
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