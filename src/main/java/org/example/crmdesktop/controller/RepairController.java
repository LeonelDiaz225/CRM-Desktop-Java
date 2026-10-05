package org.example.crmdesktop.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.crmdesktop.model.*;
import org.example.crmdesktop.service.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class RepairController implements Initializable {
    @FXML private TableView<Repair> tableRepairs;
    @FXML private TableColumn<Repair, String> colClient;
    @FXML private TableColumn<Repair, String> colEquipment;
    @FXML private TableColumn<Repair, String> colStatus;
    @FXML private TableColumn<Repair, LocalDate> colDate;
    @FXML private ComboBox<Client> cmbClient;
    @FXML private ComboBox<Equipment> cmbEquipment;
    @FXML private ComboBox<RepairStatus> cmbStatus;
    @FXML private DatePicker dateReceived;
    @FXML private DatePicker dateCompleted;
    @FXML private TextField txtCost;
    @FXML private TextArea txtProblem;
    @FXML private TextArea txtDiagnosis;

    private final RepairService repairService;
    private final ClientService clientService;
    private final EquipmentService equipmentService;
    private final NavigationService navigationService;
    private Repair selectedRepair;

    public RepairController(RepairService repairService, ClientService clientService,
                            EquipmentService equipmentService, NavigationService navigationService) {
        this.repairService = repairService;
        this.clientService = clientService;
        this.equipmentService = equipmentService;
        this.navigationService = navigationService;
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        colClient.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getClient() == null ? "" : data.getValue().getClient().getName()));
        colEquipment.setCellValueFactory(data -> new SimpleStringProperty(equipmentName(data.getValue().getEquipment())));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatus() == null ? "" : data.getValue().getStatus().name()));
        colDate.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getReceivedDate()));
        cmbClient.setItems(javafx.collections.FXCollections.observableArrayList(clientService.findAll()));
        cmbEquipment.setItems(javafx.collections.FXCollections.observableArrayList(equipmentService.findAll()));
        cmbStatus.getItems().setAll(RepairStatus.values());
        cmbClient.setCellFactory(list -> clientCell());
        cmbClient.setButtonCell(clientCell());
        cmbEquipment.setCellFactory(list -> equipmentCell());
        cmbEquipment.setButtonCell(equipmentCell());
        cmbClient.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) cmbEquipment.setItems(
                    javafx.collections.FXCollections.observableArrayList(equipmentService.findByClientId(newValue.getId())));
        });
        tableRepairs.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> selectRepair(newValue));
        loadRepairs();
    }

    private ListCell<Client> clientCell() { return new ListCell<>() {
        @Override protected void updateItem(Client item, boolean empty) {
            super.updateItem(item, empty); setText(empty || item == null ? null : item.getName());
        }
    }; }
    private ListCell<Equipment> equipmentCell() { return new ListCell<>() {
        @Override protected void updateItem(Equipment item, boolean empty) {
            super.updateItem(item, empty); setText(empty || item == null ? null : equipmentName(item));
        }
    }; }

    @FXML private void saveRepair() {
        try {
            Repair repair = selectedRepair == null ? new Repair() : selectedRepair;
            repair.setClient(cmbClient.getValue());
            repair.setEquipment(cmbEquipment.getValue());
            repair.setStatus(cmbStatus.getValue());
            repair.setReceivedDate(dateReceived.getValue());
            repair.setCompletedDate(dateCompleted.getValue());
            repair.setProblemDescription(txtProblem.getText());
            repair.setTechnicalDiagnosis(txtDiagnosis.getText());
            repair.setCost(txtCost.getText().isBlank() ? null : new BigDecimal(txtCost.getText().replace(',', '.')));
            repairService.save(repair);
            clearForm(); loadRepairs(); info("Reparación guardada correctamente");
        } catch (NumberFormatException exception) { error("El costo debe ser un número válido"); }
        catch (RuntimeException exception) { error(exception.getMessage()); }
    }

    @FXML private void deleteRepair() {
        if (selectedRepair == null) { error("Selecciona una reparación para eliminar"); return; }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la reparación seleccionada?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try { repairService.deleteById(selectedRepair.getId()); clearForm(); loadRepairs(); }
                catch (RuntimeException exception) { error(exception.getMessage()); }
            }
        });
    }

    @FXML private void clearForm() {
        selectedRepair = null; tableRepairs.getSelectionModel().clearSelection();
        cmbClient.getSelectionModel().clearSelection(); cmbEquipment.getSelectionModel().clearSelection();
        cmbStatus.setValue(RepairStatus.RECIBIDO); dateReceived.setValue(LocalDate.now());
        dateCompleted.setValue(null); txtCost.clear(); txtProblem.clear(); txtDiagnosis.clear();
    }
    @FXML private void backToDashboard() { navigationService.showDashboard(); }
    @FXML private void openEquipment() { navigationService.show("/fxml/equipment.fxml", "CRM Desktop - Equipos"); }

    private void selectRepair(Repair repair) {
        selectedRepair = repair; if (repair == null) return;
        cmbClient.setValue(repair.getClient()); cmbEquipment.setValue(repair.getEquipment());
        cmbStatus.setValue(repair.getStatus()); dateReceived.setValue(repair.getReceivedDate());
        dateCompleted.setValue(repair.getCompletedDate());
        txtCost.setText(repair.getCost() == null ? "" : repair.getCost().toPlainString());
        txtProblem.setText(repair.getProblemDescription()); txtDiagnosis.setText(repair.getTechnicalDiagnosis());
    }
    private void loadRepairs() { tableRepairs.getItems().setAll(repairService.findAll()); }
    private String equipmentName(Equipment equipment) {
        if (equipment == null) return "";
        return (empty(equipment.getBrand()) + " " + empty(equipment.getModel()) + " (" + empty(equipment.getSerialNumber()) + ")").trim();
    }
    private String empty(String value) { return value == null ? "" : value; }
    private void info(String message) { new Alert(Alert.AlertType.INFORMATION, message).show(); }
    private void error(String message) { new Alert(Alert.AlertType.ERROR, message == null ? "Ocurrió un error" : message).show(); }
}
