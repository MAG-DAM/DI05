package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 570, 150);
        stage.setTitle("Informes JasperReport"); //Establecemos titulo de la ventana
        stage.setScene(scene);
        stage.setResizable(false);  // Deshabilitamos la opción de redimensionar

        // Configuramos el evento de cierre
        stage.setOnCloseRequest(event -> {
            event.consume();// Evitamos que la ventana se cierre automáticamente
            if (MainController.mostrarConfirmacion("¿Seguro que quieres cerrar la aplicación?")) {
                System.exit(0);
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        BasicConfigurator.configure(); // Configuramos Log4j para evitar advertencias
        launch();
    }
}