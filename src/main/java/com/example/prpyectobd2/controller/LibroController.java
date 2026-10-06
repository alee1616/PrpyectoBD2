package com.example.prpyectobd2.controller;

import com.example.prpyectobd2.connection.ConexionBD;
import com.example.prpyectobd2.model.Libro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private void initialize() {
        configurarTabla();
        configurarComboBox();
        cargarLibros();

        // Selección de las celdas del TableView para cargar los datos en los campos
        tblLibros.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarLibroSeleccionado(newValue);
            }
        });
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
        cmbCategoria.getItems().addAll(
                "Programación",
                "Base de datos",
                "Comedia",
                "Otros"
        );
    }

    private void cargarLibroSeleccionado(Libro libro) {
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        cmbCategoria.getSelectionModel().select(libro.getCategoria());
        txtPrecio.setText(Double.toString(libro.getPrecio()));
        txtStock.setText(Integer.toString(libro.getStock()));
    }

    private void cargarLibros() {
        listaLibros.clear();
        String sql = "SELECT id, titulo, autor, categoria, precio, stock FROM libro ORDER BY id";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
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

        } catch (SQLException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    null,
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
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, txtTitulo.getText().trim());
            statement.setString(2, txtAutor.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));

            statement.executeUpdate();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", null, "Libro guardado correctamente.");
            cargarLibros();
            onLimpiar();

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", null, "No se pudo guardar: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", null, "Precio y stock deben ser numéricos.");
        }
    }

    @FXML
    public void onActualizarRegistro() {
        Libro libroSeleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay libro seleccionado",
                    "Estimado usuario, favor seleccionar un libro de TableView"
            );
            return; // Detiene la ejecución si no hay libro seleccionado
        }

        if (!validarCampos()) {
            return;
        }

        String sql = "UPDATE libro SET titulo=?, autor=?, categoria=?, precio=?, stock=? WHERE id=?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, txtTitulo.getText().trim());
            statement.setString(2, txtAutor.getText().trim());
            statement.setString(3, cmbCategoria.getValue());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            statement.setInt(6, libroSeleccionado.getId()); // Faltaba enviar el ID del libro

            int filasActualizadas = statement.executeUpdate();

            if (filasActualizadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "Actualización completada",
                        "El libro fue actualizado correctamente"
                );
            }
            cargarLibros();
            onLimpiar();

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error BD", null, "Error al actualizar: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de formato", null, "Precio y stock deben ser numéricos.");
        }
    }

    @FXML
    public void onEliminar() {
        Libro libroSeleccionado = tblLibros.getSelectionModel().getSelectedItem();

        if (libroSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay libro seleccionado",
                    "Seleccione un libro de la tabla para eliminarlo."
            );
            return;
        }


        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación de eliminación");
        confirmacion.setHeaderText("Eliminar Libro");
        confirmacion.setContentText("¿Está seguro que desea eliminar el libro: " + libroSeleccionado.getTitulo() + "?");

        if (confirmacion.showAndWait().get() != ButtonType.OK) {
            return;
        }

        String sql = "DELETE FROM libro WHERE id=?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, libroSeleccionado.getId());

            int filasEliminadas = statement.executeUpdate();

            if (filasEliminadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro eliminado",
                        "Eliminación completada",
                        "El libro fue eliminado correctamente."
                );
            }

            cargarLibros();
            onLimpiar();
        } catch (SQLException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error BD",
                    "Fallo al eliminar",
                    "No se pudo eliminar el libro: " + e.getMessage()
            );
        }
    }

    @FXML
    public void onActualizarTabla() {
        cargarLibros();
    }

    @FXML
    public void onLimpiar() {
        txtTitulo.clear();
        txtAutor.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtStock.clear();
        tblLibros.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty() ||
                txtAutor.getText().trim().isEmpty() ||
                cmbCategoria.getValue() == null ||
                txtPrecio.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty()) {

            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", null, "Por favor, complete todos los campos.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}