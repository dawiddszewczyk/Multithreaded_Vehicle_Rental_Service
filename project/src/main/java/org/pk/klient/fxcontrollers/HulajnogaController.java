package org.pk.klient.fxcontrollers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.klient.util.ConnectionBox;
import org.pk.klient.util.ObecneWypozyczenieFTask;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

public class HulajnogaController {
    
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

                        ConnectionBox.getInstance().getDoSerwera().flush();
                        ConnectionBox.getInstance().getDoSerwera().reset();
                        ConnectionBox.getInstance().getDoSerwera().writeObject("zaktualizujPojazd()");
                        ConnectionBox.getInstance().getDoSerwera().writeObject(temp.getPojazd());
                        ConnectionBox.getInstance().getDoSerwera().flush();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
				}
    			Platform.runLater(()->{
    				ustawWartosci(temp.getPojazd());
    			});
    		}
    	};
    	ObecneWypozyczenieFTask<?> wypozyczenieFTask = new ObecneWypozyczenieFTask<>(watek, null);
    	ConnectionBox.getInstance().setWypozyczenie(wypozyczenieFTask);
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(wypozyczenieFTask);
    }
    
    
    public void zakonczWypozyczenie(){
        ConnectionBox.getInstance().getWypozyczenie().cancel(true);
    }

}