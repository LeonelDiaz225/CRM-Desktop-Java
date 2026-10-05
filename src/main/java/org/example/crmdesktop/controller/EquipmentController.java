package org.example.crmdesktop.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.crmdesktop.model.Client;
import org.example.crmdesktop.model.Equipment;
import org.example.crmdesktop.service.ClientService;
import org.example.crmdesktop.service.EquipmentService;
import org.example.crmdesktop.service.NavigationService;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class EquipmentController implements Initializable {
    @FXML private TableView<Equipment> tableEquipment;
    @FXML private TableColumn<Equipment, String> colType;
    @FXML private TableColumn<Equipment, String> colBrand;
    @FXML private TableColumn<Equipment, String> colModel;
    @FXML private TableColumn<Equipment, String> colSerial;
    @FXML private TableColumn<Equipment, String> colClient;
    @FXML private TextField txtSearch;
    @FXML private TextField txtType;
    @FXML private TextField txtBrand;
    @FXML private TextField txtModel;
    @FXML private TextField txtSerial;
    @FXML private ComboBox<Client> cmbClient;

    private final EquipmentService equipmentService;
    private final ClientService clientService;
    private final NavigationService navigationService;
    private Equipment selectedEquipment;

    public EquipmentController(EquipmentService equipmentService, ClientService clientService,
                                NavigationService navigationService) {
        this.equipmentService = equipmentService;
        this.clientService = clientService;
        this.navigationService = navigationService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        colBrand.setCellValueFactory(data -> new SimpleStringProperty(empty(data.getValue().getBrand())));
        colModel.setCellValueFactory(data -> new SimpleStringProperty(empty(data.getValue().getModel())));
        colSerial.setCellValueFactory(data -> new SimpleStringProperty(empty(data.getValue().getSerialNumber())));
        colClient.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getClient() == null ? "" : data.getValue().getClient().getName()));
        cmbClient.setItems(javafx.collections.FXCollections.observableArrayList(clientService.findAll()));
        cmbClient.setCellFactory(list -> clientCell());
        cmbClient.setButtonCell(clientCell());
        tableEquipment.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectEquipment(newValue));
        loadEquipment();
    }

    private ListCell<Client> clientCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }

    @FXML private void searchEquipment() {
        tableEquipment.getItems().setAll(equipmentService.searchBySerialNumber(txtSearch.getText()));
    }

    @FXML private void saveEquipment() {
        try {
            Equipment equipment = selectedEquipment == null ? new Equipment() : selectedEquipment;
            equipment.setType(txtType.getText());
            equipment.setBrand(txtBrand.getText());
            equipment.setModel(txtModel.getText());
            equipment.setSerialNumber(txtSerial.getText().isBlank() ? null : txtSerial.getText());
            equipment.setClient(cmbClient.getValue());
            equipmentService.save(equipment);
            clearForm();
            loadEquipment();
            info("Equipo guardado correctamente");
        } catch (RuntimeException exception) {
            error(exception.getMessage());
        }
    }

    @FXML private void deleteEquipment() {
        if (selectedEquipment == null) {
            error("Selecciona un equipo para eliminar");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar el equipo seleccionado?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    equipmentService.deleteById(selectedEquipment.getId());
                    clearForm();
                    loadEquipment();
                } catch (RuntimeException exception) {
                    error("No se pudo eliminar: " + exception.getMessage());
                }
            }
        });
    }

    @FXML private void clearForm() {
        selectedEquipment = null;
        tableEquipment.getSelectionModel().clearSelection();
        txtType.clear(); txtBrand.clear(); txtModel.clear(); txtSerial.clear();
        cmbClient.getSelectionModel().clearSelection();
    }

    @FXML private void backToDashboard() { navigationService.showDashboard(); }
    @FXML private void openRepairs() { navigationService.show("/fxml/repairs.fxml", "CRM Desktop - Reparaciones"); }

    private void selectEquipment(Equipment equipment) {
        selectedEquipment = equipment;
        if (equipment == null) return;
        txtType.setText(empty(equipment.getType()));
        txtBrand.setText(empty(equipment.getBrand()));
        txtModel.setText(empty(equipment.getModel()));
        txtSerial.setText(empty(equipment.getSerialNumber()));
        cmbClient.setValue(equipment.getClient());
    }

    private void loadEquipment() { tableEquipment.getItems().setAll(equipmentService.findAll()); }
    private String empty(String value) { return value == null ? "" : value; }
    private void info(String message) { new Alert(Alert.AlertType.INFORMATION, message).show(); }
    private void error(String message) { new Alert(Alert.AlertType.ERROR, message == null ? "Ocurrió un error" : message).show(); }
}
