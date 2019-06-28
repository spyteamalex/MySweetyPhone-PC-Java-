package sample;

import Utils.Session;
import Utils.SessionServer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;


public class SServer {

    @FXML
    private AnchorPane MainPane;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button NewSession;

    @FXML
    private CheckBox ServerMode;

    @FXML
    private ChoiceBox<String> SessionType;

    @FXML
    private BorderPane NewSessionMainPane;

    @FXML
    public void initialize(){
        try{
            FileInputStream propFile = new FileInputStream("properties.properties");
            Properties props = new Properties();
            props.load(propFile);
            propFile.close();
            Thread Resize = new Thread(()->{
                try {
                    while (MainPane.getScene() == null || MainPane.getScene().getWindow() == null) Thread.sleep(100);
                    MainPane.prefWidthProperty().bind(MainActivity.controller.Replace.widthProperty());
                    MainPane.prefHeightProperty().bind(MainActivity.controller.Replace.heightProperty());
                    NewSessionMainPane.prefHeightProperty().bind(MainActivity.controller.Replace.heightProperty());
                    NewSessionMainPane.prefWidthProperty().bind(MainActivity.controller.Replace.widthProperty());
                    MainPane.prefWidthProperty().bind(NewSession.getScene().widthProperty());
                    MainPane.prefHeightProperty().bind(NewSession.getScene().heightProperty());

                    NewSession.setDisable(false);
                    SessionType.setDisable(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            Resize.start();
            for (int t : SessionServer.allowedTypes) {
                SessionType.getItems().add(Session.decodeType(t));
            }
            NewSession.setOnMouseClicked(this::OpenSession);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void OpenSession(MouseEvent e){
        try{
            if (SessionType.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Выберите тип сеанса");
                alert.show();
            } else {
                NewSession.setOnMouseClicked(this::CloseSession);
                NewSession.setText("Закрыть сессию");
                Utils.SessionServer s = new Utils.SessionServer(Session.encodeType(SessionType.getSelectionModel().getSelectedItem()),0,()->{
                    NewSession.setOnMouseClicked(this::OpenSession);
                    NewSession.setText("Открыть сессию");
                    SessionType.setDisable(false);
                });
                s.Start();
                SessionType.setDisable(true);
            }
        } catch (IOException err){
            err.printStackTrace();
        }
    }

    private void CloseSession(MouseEvent e) {
        try {
            NewSession.setOnMouseClicked(this::OpenSession);
            NewSession.setText("Открыть сессию");
            Session.sessions.pop().Stop();
            SessionType.setDisable(false);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    private void SwitchServerMove(MouseEvent e){

    }
}
