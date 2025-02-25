package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ArtistaController {
    public ListView<Artista> LV_Artistas;
    private MainController mainController;
    private String urlDB ="jdbc:sqlite:datos/chinook.db";
    private ObservableList<Artista> ListaArtistas = FXCollections.observableArrayList();


    public void initialize() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Accedemos a la base de datos y
        // cargamos los datos en el ListView

        try {
            // Establecemos la conexión con SQLite
            conn = DriverManager.getConnection(urlDB);
            // Definimos la consulta SQL para obtener los artistas
            String sql = "SELECT ArtistId, Name FROM artists";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            // Recorremos los resultados y añadimos los artistas a la listaa
            while (rs.next()) {
                int id = rs.getInt("ArtistId");// Obtenemos ID del artista
                String name = rs.getString("Name");// Obtenemos nombre del artista
                // Creamos objeto Artista y lo agregamos a la lista
                ListaArtistas.add(new Artista(id, name));
            }
            // Establecer la lista en el ListView
            LV_Artistas.setItems(ListaArtistas);
            // Agregamos un listener para manejar la selección de un artista en la lista
            LV_Artistas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Si el usuario selecciona un artista cargamos su informe
                    cargarInformeArtista(newValue.getId(), newValue.getName());
                }
            });

        } catch (SQLException e) {
            System.err.println("Error al acceder a la base de datos: " + e.getMessage());
        } finally {
            // Cerramos recursos
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // INFORME ARTISTA

    private void cargarInformeArtista(int IdArtista,String Nombre) {
        // Obtenemos el Stage desde un nodo de la interfaz
        Stage primaryStage = (Stage) LV_Artistas.getScene().getWindow();
        primaryStage.setOnCloseRequest(Event::consume);
        // Deshabilitamos listview para evitar múltiples aperturas
        LV_Artistas.setDisable(true);
        try {
            // Definimos la ruta del archivo JRXML del informe
            String jasperFilePath = "informes/Artistas.jrxml";
            // Cargamos el archivo JRXML como un InputStream
            InputStream inputStream = MainApp.class.getResourceAsStream(jasperFilePath);
            // Compilamos el informe JRXML a un archivo JasperReport para su ejecución
            System.out.println("Compilando : " + jasperFilePath);
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            // Establecemos la conexión con la base de datos
            Connection conn = DriverManager.getConnection(urlDB);
            //Creamos los parametros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ArtistaId", IdArtista);
            parametros.put("ArtistaName", Nombre);
            // Generamos el informe con los datos de la base de datos
            // CON parámetros adicionales
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conn);
            // Creamos el visor de JasperReports para mostrar el informe
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            // Listener para reactivar botones cuando el informe se cierre
            viewer.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    LV_Artistas.setDisable(false); // Reactivar el ListView
                    primaryStage.setOnCloseRequest(null); // Permitir cerrar la ventana nuevamente
                }
            });
            viewer.setVisible(true);

        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMainController(MainController mainController) {
      this.mainController = mainController;
    }

}
