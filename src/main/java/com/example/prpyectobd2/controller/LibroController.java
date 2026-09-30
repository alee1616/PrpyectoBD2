package com.example.prpyectobd2.controller;

import com.example.prpyectobd2.connection.ConexionBD;
import com.example.prpyectobd2.model.Libro;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LibroController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private TableView<Libro> tblLibros;
    @FXML
    private TableColumn<Libro, Integer> colId;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colAutor;
    @FXML
    private TableColumn<Libro, String> colCategoria;
    @FXML
    private TableColumn<Libro, Double> colPrecio;
    @FXML
    private TableColumn<Libro, Integer> colStock;

    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        configurarTabla();
        configurarComboBox();
        cargarLibros();
    }

    @FXML
    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

    }

    @FXML
    private void configurarComboBox() {
        cmbCategoria.getItems().addAll("Programación",
                "Base de datos",
                "Comedia",
                "Otros");
    }

    private void cargarLibros(){
        listaLibros.clear();

        String sql = "SELECT id,titulo, autor, categoria, precio, stock FROM libro ORDER BY id";

        try (
                Connection connection =
                        ConexionBD.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()

        ){
            while (resultSet.next()) {

                Libro libro = new Libro();

                libro.setId(resultSet.getInt("id"));
                libro.setTitulo(resultSet.getString("titulo"));
                libro.setAutor(resultSet.getString("autor"));
                libro.setCategoria(resultSet.getString("categoria"));
                libro.setPrecio(resultSet.getDouble("precio"));
                libro.setStock(resultSet.getInt("stock"));

                listaLibros.add(libro);
            }

            tblLibros.setItems(listaLibros);



        }

        catch (SQLException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No fue posible consultar los libros: " + e.getMessage()
            );
        }


    }

    @FXML
    private void onGuardarLibro() {
        if (!validarCampos()) {
            return;
        }

        String sql = "INSERT INTO libro(titulo, autor, categoria, precio, stock) VALUES(?, ?, ?, ?, ?)";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);

        ) {
            //Pasar los parámetros a la consulta
            statement.setString(1, txtTitulo.getText().trim());
            statement.setString(2, txtAutor.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));

            statement.executeUpdate();

        } catch (SQLException e) {

        }

    }
    
    

    private void  mostrarAlerta(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validarCampos(){
        return true;
    }


    public void onActualizarTabla(ActionEvent actionEvent) {
    }

    public void onLimpiar(ActionEvent actionEvent) {
    }
}
