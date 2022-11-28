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

import java.util.Timer;
import java.util.TimerTask;

public class ScooterController {
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
    	/*
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->
                {
                    ustawWartosci(temp);
                });
                System.out.println("eldo");
            }
        },0,1000L);
    	*/
    	Runnable watek = () ->{
    		while(!Thread.currentThread().isInterrupted()) {
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException wyjatekIE) {
					//wyjatekIE.printStackTrace();
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
}