package com.example.demo;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

public class MainController {
    @FXML
    private HBox hbox;
    private String urlDB ="jdbc:sqlite:datos/chinook.db";
    @FXML
    private ButtonBar barraBotones;

    public void initialize() {

    }

    // INFORME CLIENTES

    @FXML
    private void handlerInformeClientes(ActionEvent actionEvent) {
        // Obtenemos el Stage desde el ActionEvent
        Stage primaryStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        // Deshabilitamos botones para evitar múltiples aperturas
        barraBotones.setDisable(true);
        // Deshabilitamos el cierre de la ventana principal
        primaryStage.setOnCloseRequest(Event::consume);
        // Deshabilitamos minimizar
        primaryStage.setIconified(false);
        primaryStage.iconifiedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                primaryStage.setIconified(false);
            }
        });

        try {
            // Definimos la ruta del archivo JRXML del informe
            String jasperFilePath = "informes/Clientes.jrxml";
            // Cargamos el archivo JRXML como un InputStream
            InputStream inputStream = MainApp.class.getResourceAsStream(jasperFilePath);
            // Compilamos el informe JRXML a un archivo JasperReport para su ejecución
            System.out.println("Compilando : " + jasperFilePath);
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            // Establecemos la conexión con la base de datos
            Connection conn = DriverManager.getConnection(urlDB);
            // Generamos el informe con los datos de la base de datos
            // SIN parámetros adicionales
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), conn);
            // Creamos el visor de JasperReports para mostrar el informe
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            // Listener para reactivar botones cuando el informe se cierre
            viewer.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    barraBotones.setDisable(false); // Reactivar botones
                    // Permitimos minimizar nuevamente
                    primaryStage.iconifiedProperty().removeListener(
                            (obs, oldValue, newValue) -> primaryStage.setIconified(false)
                    );
                    // Restablecemos el evento de cierre de la ventana principal
                    primaryStage.setOnCloseRequest(event -> {
                        event.consume();
                        if (MainController.mostrarConfirmacion("¿Seguro que quieres cerrar la aplicación?")) {
                            System.exit(0);
                        }
                    });
                }
            });
            viewer.setVisible(true);// Mostramos la ventana del visor de JasperReports

        } catch (JRException | SQLException e) {
            e.printStackTrace();
            //En caso de error
            barraBotones.setDisable(false);// Reactivamos la barra de botones
            primaryStage.setOnCloseRequest(null); // Permitimos cerrar la ventana
            primaryStage.setResizable(true);// Permitimos redimensionar
        }
    }


    // INFORME ARTISTAS

    @FXML
    private void handlerInformeArtistas(ActionEvent actionEvent) {
       abrirFormulario("Seleccionar Artista");
    }


    private void abrirFormulario(String titulo) {
        try {
            // Cargamos archivo FXML que define la interfaz del formulario
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("ArtistaView.fxml"));
            // Creamos nueva escena con el diseño cargado
            Scene escena = new Scene(loader.load(),560,400);
            // Obtenemos el controlador asociado a la vista cargada
            ArtistaController controlador = loader.getController();
            // Asignamos la instancia del controlador principal al formulario
            controlador.setMainController(this);
            // Creamos nueva ventana (Stage) y asignamos titulo y escena
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(escena);
            // Configuramos como ventana modal
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);  // Deshabilitamos la opción de redimensionar
            stage.centerOnScreen();// Centramos la ventana en la pantalla

            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error al abrir el formulario: " + titulo);
            e.printStackTrace();
        }
    }

    // CERRAR

    @FXML
    public void handlerCerrar(ActionEvent actionEvent) {
        if (mostrarConfirmacion("¿Seguro que quieres cerrar la aplicación?")) {
            System.exit(0);
        }
    }

    // Metodo para mostrar una ALERTA DE CONFIRMACION

    public static boolean mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

}