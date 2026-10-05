package org.example.crmdesktop.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.crmdesktop.model.Repair;
import org.example.crmdesktop.service.DashboardService;
import org.example.crmdesktop.service.NavigationService;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {
    @FXML
    private Label lblTotalRepairs;

    @FXML
    private Label lblTotalClients;

    @FXML
    private TableView<Repair> tableRecentRepairs;

    @FXML
    private TableColumn<Repair, String> colClient;

    @FXML
    private TableColumn<Repair, String> colEquipment;

    @FXML
    private TableColumn<Repair, String> colStatus;

    @FXML
    private TableColumn<Repair, LocalDate> colReceivedDate;

    private final DashboardService dashboardService;
    private final NavigationService navigationService;

    public DashboardController(DashboardService dashboardService, NavigationService navigationService) {
        this.dashboardService = dashboardService;
        this.navigationService = navigationService;
    }

    @FXML
    private void openClients() {
        navigationService.show("/fxml/clients.fxml", "CRM Desktop - Clientes");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureColumns();
        loadDashboardData();
    }

    private void configureColumns() {
        colClient.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getClient() == null ? "" : cellData.getValue().getClient().getName()));
        colEquipment.setCellValueFactory(cellData -> new SimpleStringProperty(formatEquipment(cellData.getValue())));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStatus() == null ? "" : cellData.getValue().getStatus().name()));
        colReceivedDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getReceivedDate()));
    }

    private String formatEquipment(Repair repair) {
        if (repair.getEquipment() == null) {
            return "";
        }
        String brand = repair.getEquipment().getBrand() == null ? "" : repair.getEquipment().getBrand();
        String model = repair.getEquipment().getModel() == null ? "" : repair.getEquipment().getModel();
        return (brand + " " + model).trim();
    }

    private void loadDashboardData() {
        try {
            lblTotalClients.setText(String.valueOf(dashboardService.countClients()));
            lblTotalRepairs.setText(String.valueOf(dashboardService.countActiveRepairs()));
            tableRecentRepairs.getItems().setAll(dashboardService.findRecentRepairs());
        } catch (RuntimeException exception) {
            lblTotalClients.setText("-");
            lblTotalRepairs.setText("-");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de base de datos");
            alert.setHeaderText("No se pudo cargar el dashboard");
            alert.setContentText(exception.getMessage() == null
                    ? "Verifica que MySQL esté disponible y que las credenciales sean correctas."
                    : exception.getMessage());
            alert.show();
        }
    }
}
