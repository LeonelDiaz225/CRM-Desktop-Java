package org.example.crmdesktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.example.crmdesktop.service.NavigationService;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrmDesktopApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(CrmDesktopApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        NavigationService navigationService = springContext.getBean(NavigationService.class);
        navigationService.setPrimaryStage(primaryStage);
        navigationService.showDashboard();
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}
