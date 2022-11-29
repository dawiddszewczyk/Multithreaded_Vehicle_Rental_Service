package org.pk.klient.fxcontrollers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.pk.entity.Pojazd;
import org.pk.klient.util.ConnectionBox;
import org.pk.klient.util.ObecneWypozyczenieFTask;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.round;

public class ScooterController {
    private Stage stage;
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
    public void zmienStan(Pojazd temp) throws InterruptedException {
    	Runnable watek = () ->{
    		while(!Thread.currentThread().isInterrupted()) {
    			try {
					Thread.sleep(1000);
                    if(temp.getStanBaterii()<=0){
                        ConnectionBox.getInstance().getWypozyczenie().cancel(true);
                    }else{
                        BigDecimal stanBaterii = BigDecimal.valueOf(temp.getStanBaterii()).subtract(new BigDecimal("1"));
                        BigDecimal licznikKm = BigDecimal.valueOf(temp.getLicznikkm()).subtract(new BigDecimal("2"));
                        temp.setStanBaterii(stanBaterii.doubleValue());
                        temp.setLicznikkm(licznikKm.doubleValue());
                    }
				} catch (InterruptedException wyjatekIE) {
                    try {
                        wyjatekIE.printStackTrace();
                        System.out.println(temp.getStanBaterii() + "   " + temp.getLicznikkm());
                        ConnectionBox.getInstance().getDoSerwera().writeObject("stworzPojazd()");
                        ConnectionBox.getInstance().getDoSerwera().writeObject(temp);
                        ConnectionBox.getInstance().getDoSerwera().flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
				}
    			Platform.runLater(()->{
    				ustawWartosci(temp);
    			});
    			System.out.println("kekw");
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