package com.ims;

import com.ims.dao.DatabaseConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize SQLite database and tables
        DatabaseConnection.initializeDatabase();

        // Load Main Dashboard View
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ims/ui/fxml/dashboard.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Joshua's Shop IMS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
