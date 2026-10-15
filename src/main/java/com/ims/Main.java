package com.ims;

import com.ims.dao.DatabaseConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConnection.initializeDatabase();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ims/ui/fxml/inventory_list.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Joshua's Shop IMS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
