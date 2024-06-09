package com.example.javafx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Represents the main application, which extends the JavaFX Application class.
//Since Application class is the parent class, there is an abstract method that we need to inherit or override.
public class SplitBillApplication extends Application {
    public static void main(String[] args) {
        //Launches the application.
        launch(args);
    }

    //Calling the start method when the application is launched.
    @Override
    public void start(Stage primaryStage) {
        try {
            //Initialize the FXMLLoader to load the FXML file.
            FXMLLoader fxmlLoader = new FXMLLoader(SplitBillApplication.class.getResource("Scene1.fxml"));
            //Create parent node to arrange all the different nodes we have.
            Parent root = fxmlLoader.load();
            //Create a Scene.
            Scene scene = new Scene(root, 650, 700);
            //Load CSS file.
            String css = this.getClass().getResource("style.css").toExternalForm();
            scene.getStylesheets().add(css);
            //Set the title of the Stage.
            primaryStage.setTitle("EasySplit");
            primaryStage.setScene(scene);
            //Display the Stage.
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}