package org.example.crmdesktop.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NavigationService {

    private final ConfigurableApplicationContext springContext;
    private Stage primaryStage;

    public NavigationService(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show(String resource, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("La ventana principal todavía no fue configurada");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(root, 1000, 650));
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar la vista " + resource, exception);
        }
    }

    public void showDashboard() {
        show("/fxml/dashboard.fxml", "CRM Desktop - Dashboard");
    }
}
