package org.example.crmdesktop.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.crmdesktop.model.Client;
import org.example.crmdesktop.service.ClientService;
import org.example.crmdesktop.service.NavigationService;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ClientController implements Initializable {

    @FXML
    private TableView<Client> tableClients;

    @FXML
    private TableColumn<Client, String> colName;

    @FXML
    private TableColumn<Client, String> colPhone;

    @FXML
    private TableColumn<Client, String> colEmail;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtAddress;

    private final ClientService clientService;
    private final NavigationService navigationService;
    private Client selectedClient;

    public ClientController(ClientService clientService, NavigationService navigationService) {
        this.clientService = clientService;
        this.navigationService = navigationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getPhone())));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getEmail())));
        tableClients.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectClient(newValue));
        loadClients();
    }

    @FXML
    private void searchClients() {
        tableClients.getItems().setAll(clientService.searchByName(txtSearch.getText()));
    }

    @FXML
    private void saveClient() {
        try {
            Client client = selectedClient == null ? new Client() : selectedClient;
            client.setName(txtName.getText());
            client.setPhone(txtPhone.getText());
            client.setEmail(txtEmail.getText());
            client.setAddress(txtAddress.getText());
            clientService.save(client);
            clearForm();
            loadClients();
            showInfo("Cliente guardado correctamente");
        } catch (RuntimeException exception) {
            showError(exception.getMessage());
        }
    }

    @FXML
    private void deleteClient() {
        if (selectedClient == null) {
            showError("Selecciona un cliente para eliminar");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el cliente seleccionado?", ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirmar eliminación");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    clientService.deleteById(selectedClient.getId());
                    clearForm();
                    loadClients();
                } catch (RuntimeException exception) {
                    showError("No se pudo eliminar el cliente: " + exception.getMessage());
                }
            }
        });
    }

    @FXML
    private void clearForm() {
        selectedClient = null;
        tableClients.getSelectionModel().clearSelection();
        txtName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtAddress.clear();
    }

    @FXML
    private void backToDashboard() {
        navigationService.showDashboard();
    }

    private void selectClient(Client client) {
        selectedClient = client;
        if (client == null) {
            return;
        }
        txtName.setText(nullToEmpty(client.getName()));
        txtPhone.setText(nullToEmpty(client.getPhone()));
        txtEmail.setText(nullToEmpty(client.getEmail()));
        txtAddress.setText(nullToEmpty(client.getAddress()));
    }

    private void loadClients() {
        tableClients.getItems().setAll(clientService.findAll());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message == null ? "Ocurrió un error" : message).show();
    }
}
