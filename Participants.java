package com.example.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class Participants {
    //Keeps track of the number of participants.
    private int entryCounter = 1;

    public VBox createParticipantInputBox(List<String> participantNames) {
        //Creates a VBox to contain the input field and entered names.
        VBox participantInputBox = new VBox();
        //Creates a TextField for user input.
        TextField userInput = new TextField();
        //Sets width of the text field.
        userInput.setPrefColumnCount(10);
        //Adds CSS class to style.
        userInput.getStyleClass().add("regular-text");
        //Creates label to prompt the user.
        Label userInputLabel = new Label("Enter participants: ");
        //Adds CSS class to style.
        userInputLabel.getStyleClass().add("regular-text");
        //Arranges label and text field side by side.
        TilePane userInputPane = new TilePane();
        userInputPane.getChildren().addAll(userInputLabel, userInput);

        //Creates a container to hold the input field and label.
        VBox userInputContainer = new VBox();
        //Adds userInputPane to userInputContainer.
        userInputContainer.getChildren().addAll(userInputPane);
        //Adds the container to the main participant input box.
        participantInputBox.getChildren().addAll(userInputContainer);

        //Handles logic when user presses enter for every participant input.
        userInput.setOnAction(event -> {
            //Alert pops out as the user reaches the limit of 10 participants.
            if (participantNames.size() >= 10) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Participant Limit Reached");
                alert.setHeaderText(null);
                alert.setContentText("You can only add up to 10 participants.");
                alert.showAndWait();
                return;
            }

            //Obtains the text entered by the user.
            String enteredText = userInput.getText();
            //Adds the entered text to the list of participant names.
            participantNames.add(enteredText);
            //Displays the entered text below the input field.
            Text enteredTextDisplay = new Text(entryCounter + ". " + enteredText);
            //Adds CSS class to style.
            enteredTextDisplay.getStyleClass().add("entered-participant-names");
            //Adds the displayed text to the user input container.
            userInputContainer.getChildren().add(enteredTextDisplay);
            //Clears the input field for the next entry
            userInput.clear();
            //Increments the entry counter for the next participant.
            entryCounter++;
        });
        return participantInputBox;
    }
}
