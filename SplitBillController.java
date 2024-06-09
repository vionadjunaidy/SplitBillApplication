package com.example.javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class SplitBillController implements Initializable {
    //Stage to display contents.
    @FXML
    private Stage stage;
    //Allows the user to upload the image of a bill.
    private FileChooser fileChooser;
    //Displays the image of the bill.
    private ImageView imageView;
    //Enables rubber band selection on the image of the bill to crop.
    private RubberBandSelection rubberBandSelection;
    //Handles image cropping feature.
    private ImageCropper imageCropper;
    //Represents the file of the image.
    private File file;
    //Performs the OCR Tesseract to extract the text from the bill.
    private OCRProcessor ocrProcessor;
    //Extract information from the bill, including item name, quantity, price, service charge, and tax.
    private ItemDetailsParser parser;
    //Holds a list of participant names.
    private List<String> participantNames = new ArrayList<>();
    //Holds a list of item details, including item name, quantity, and price.
    private List<ItemDetailsParser.ItemDetail> itemDetails = new ArrayList<>();
    //Maps every item to their own assigned participants.
    private HashMap<ItemDetailsParser.ItemDetail, List<String>> itemToParticipantsMap = new HashMap<>();
    //Displays the image of the uploaded bill.
    private String css = this.getClass().getResource("style.css").toExternalForm();

    //Constructor to initialize OCR Processor and Image Cropper.
    public SplitBillController() {
        this.ocrProcessor = new OCRProcessor("C:\\Users\\viona\\Tesseract-OCR\\tesseract");
        this.imageCropper = new ImageCropper();
    }

    //Initializes the controller.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Creates a new instance of the 'Stage' class to display content of a new window.
        stage = new Stage();
        //Creates a new instance of the 'FileChooser' class to allow the user to upload bill image.
        fileChooser = new FileChooser();
        //Creates a new instance of the 'ItemDetailsParser' class to extract item details.
        parser = new ItemDetailsParser();
    }

    //Handles the upload file button.
    @FXML
    void uploadFile() {
        //Opens a file chooser dialog for the user to upload the bill image.
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            //After a file is uploaded, the image is loaded.
            //Represents the image of the bill.
            Image image = new Image(file.toURI().toString());
            //Scale factor of the bill image to make it bigger to enhance the accuracy of the OCR.
            int scale = 2;
            //Displays the bill image.
            displayImageForCropping(image, scale);
        }
    }

    //Method to display and process bill image.
    private void displayImageForCropping(Image image, int scale) {
        //Creates a new stage to display the bill image.
        Stage stage = new Stage();
        //Root pane for the scene
        BorderPane root = new BorderPane();
        root.setId("croppingRoot");
        //Allows user to scroll the image if the image exceeds the size of the scene.
        ScrollPane scrollPane = new ScrollPane();
        Group imageLayer = new Group();
        //Initializes the ImageView.
        imageView = new ImageView(image);
        //Adds the image view to the image layer.
        imageLayer.getChildren().add(imageView);
        //Set the content of the scroll pane.
        scrollPane.setContent(imageLayer);
        //Sets the scroll pane as the center of the root pane.
        root.setCenter(scrollPane);
        //Initializes the rubber band selection.
        rubberBandSelection = new RubberBandSelection(imageLayer);

        //Keeps the aspect ratio of the bill image.
        imageView.setPreserveRatio(true);
        //Adjusts the width of the imageView with the scrollPane.
        imageView.fitWidthProperty().bind(scrollPane.widthProperty());
        //Adjusts the height of the imageView with the scrollPane.
        imageView.fitHeightProperty().bind(scrollPane.heightProperty());

        //Button to confirm the selected cropped area and proceed to the next scene.
        Button cropButton = new Button("Crop");
        cropButton.setOnAction(event -> {
            //Gets selection bounds.
            Bounds selectionBounds = rubberBandSelection.getBounds();
            //Crops the image and performs OCR on the cropped area.
            File croppedFile = imageCropper.crop(selectionBounds, file, imageView, scale);
            if (croppedFile != null) {
                String extractedText = ocrProcessor.performOCR(croppedFile);
                if (extractedText != null) {
                    //The extracted text from the OCR proceeds to the processBillText method.
                    processBillText(extractedText);
                }
            }
        });
        //Container for the "Crop" button.
        VBox buttonContainer = new VBox(cropButton);
        // Sets the button container at the bottom of the root pane
        root.setBottom(buttonContainer);
        //Creates the scene
        Scene scene = new Scene(root, 650, 700);
        // Load and apply the CSS file
        scene.getStylesheets().add(css);
        //Sets the scene for the stage.
        stage.setScene(scene);
        stage.setTitle("EasySplit");
        //Shows the stage.
        stage.show();
    }

    private void processBillText(String billText) {
        //Initializes participant names.
        Participants participants = new Participants();
        //Creates input box for user to input participant names.
        VBox participantInputBox = participants.createParticipantInputBox(participantNames);
        //Calls the 'parse' method from 'ItemDetailsParser' to extract item details.
        VBox itemDetailsBox = parser.parse(billText, itemDetails);

        try {
            //Initializes the FXMLLoader to load the FXML file.
            FXMLLoader fxmlLoader2 = new FXMLLoader(SplitBillApplication.class.getResource("Scene2.fxml"));
            //Creates parent node to arrange all the different nodes we have.
            Parent root2 = fxmlLoader2.load();
            //Gets a VBox with the id "scene2VBox" from the FXML file.
            VBox scene2VBox = (VBox) root2.lookup("#scene2VBox");
            //Clears any existing children in the VBox.
            scene2VBox.getChildren().clear();
            //Adds all children from the participantInputBox to the scene2VBox.
            scene2VBox.getChildren().addAll(participantInputBox.getChildren());
            //Adds all children from the itemDetailsBox to the scene2VBox.
            scene2VBox.getChildren().addAll(itemDetailsBox.getChildren());
            //Creates a ScrollPane to contain the scene2VBox and allow users to scroll if the content exceeds the space on the scene.
            ScrollPane scrollPane2 = new ScrollPane(scene2VBox);
            //Sets the ScrollPane to fit the width of its content.
            scrollPane2.setFitToWidth(true);
            //Sets the title of the stage.
            stage.setTitle("EasySplit");
            //Sets the scene of the stage.
            stage.setScene(new Scene(scrollPane2, 650, 700));
            //Adds the CSS stylesheet to the scene2VBox.
            scene2VBox.getStylesheets().add(css);

            Button nextButton = new Button("Next");
            nextButton.setOnAction(event -> {
                //As the user clicks "Next", it doesn't allow user to proceed if the number of participants inputted is less than 2.
                if (participantNames.size() < 2) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Insufficient Number of Participants");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter at least two participants.");
                    //Shows the alert and wait for the user to close it.
                    alert.showAndWait();
                } else {
                    try {
                        //If the input is two or more, it initializes the FXMLLoader to load the FXML file.
                        FXMLLoader fxmlLoader3 = new FXMLLoader(SplitBillApplication.class.getResource("Scene3.fxml"));
                        //Creates parent node to arrange all the different nodes we have.
                        Parent root3 = fxmlLoader3.load();
                        //Gets a VBox with the id "scene3VBox" from the FXML file.
                        VBox scene3VBox = (VBox) root3.lookup("#scene3VBox");
                        //Retrieves the controller associated with the loaded FXML file.
                        SplitBillController controller = fxmlLoader3.getController();
                        //Passes participantNames, itemDetails, service, and tax to the controller, allowing it to process this information.
                        controller.distributionOfBill(participantNames, itemDetails, parser.getService(), parser.getTax());
                        //Creates a ScrollPane to contain the scene3VBox and allow users to scroll if the content exceeds the space on the scene.
                        ScrollPane scrollPane3 = new ScrollPane(scene3VBox);
                        //Sets the ScrollPane to fit the width of its content.
                        scrollPane3.setFitToWidth(true);
                        //Sets the scene of the stage.
                        stage.setScene(new Scene(scrollPane3, 650, 700));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //Container for the "Next" button.
            VBox buttonContainer = new VBox(nextButton);
            //Adds the "Next" button to the scene2VBox.
            scene2VBox.getChildren().add(buttonContainer);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void distributionOfBill(List<String> participantNames, List<ItemDetailsParser.ItemDetail> itemDetails, double service, double tax) {
        try {
            //Initializes the FXMLLoader to load the FXML file.
            FXMLLoader fxmlLoader = new FXMLLoader(SplitBillApplication.class.getResource("Scene3.fxml"));
            //Creates parent node to arrange all the different nodes we have.
            Parent root = fxmlLoader.load();
            //Gets a VBox with the id "scene3VBox" from the FXML file.
            VBox scene3VBox = (VBox) root.lookup("#scene3VBox");
            //Initializes an Array List to store checkboxes for each participant.
            List<List<CheckBox>> allCheckBoxes = new ArrayList<>();

            //Counter for participant labels.
            int participantCounter = 1;
            //Loops through participant names.
            for (String name : participantNames) {
                //Creates label for participant name.
                Label participantsNamesLabel = new Label(participantCounter + ". " + name);
                //Adds CSS class to label.
                participantsNamesLabel.getStyleClass().add("regular-text");
                //Adds participant label to VBox.
                scene3VBox.getChildren().add(participantsNamesLabel);
                //Increments counter for every participant.
                participantCounter++;

                //Creates VBox to hold checkboxes for items.
                VBox itemBox = new VBox();
                //Creates an Array List to hold checkboxes for current participant.
                List<CheckBox> checkBoxes = new ArrayList<>();
                //Loops through item details.
                for (ItemDetailsParser.ItemDetail itemDetail : itemDetails) {
                    //Creates checkbox for item.
                    CheckBox itemCheckBox = new CheckBox(itemDetail.toString());
                    //Adds CSS class to checkbox.
                    itemCheckBox.getStyleClass().add("regular-text");
                    //Add checkbox to checkBoxes list.
                    checkBoxes.add(itemCheckBox);
                    //Adds checkbox to itemBox.
                    itemBox.getChildren().add(itemCheckBox);
                }
                //Stores checkboxes for every participant.
                allCheckBoxes.add(checkBoxes);
                //Adds itemBox to scene3VBox.
                scene3VBox.getChildren().addAll(itemBox);
            }
            //Creates 'Select All' button for the user to split the bill evenly.
            Button selectAll = new Button("Select All");
            selectAll.setOnAction(event -> {
                //Loops through checkboxes for each participant.
                for (List<CheckBox> checkBoxes : allCheckBoxes) {
                    //Checks or ticks all checkboxes.
                    for (CheckBox checkBox : checkBoxes) {
                        checkBox.setSelected(true);
                    }
                }
            });
            //Creates 'Calculate' button to generate the total amount that each participant needs to pay.
            Button calculateButton = new Button("Calculate");
            calculateButton.setOnAction(event -> {
                //Initializes boolean variables to track selection status.
                boolean allParticipantSelected = true;
                boolean allItemsAssigned = true;
                //Creates a Hash Set to store items that have been assigned to participant.
                Set<String> assignedItems = new HashSet<>();

                //Clears existing item to participants mapping.
                itemToParticipantsMap.clear();

                //Loops through the list of participant names.
                for (int i = 0; i < participantNames.size(); i++) {
                    //Obtains the participant name at index i.
                    String participantName = participantNames.get(i);
                    //Creates an Array List to store items that are selected by a participant.
                    List<ItemDetailsParser.ItemDetail> selectedItems = new ArrayList<>();
                    //Obtains checkboxes for the current participant.
                    List<CheckBox> checkBoxes = allCheckBoxes.get(i);
                    //Iterates over the list of checkboxes for the current participant.
                    for (CheckBox checkBox : checkBoxes) {
                        //Checks if the checkbox is ticked.
                        if (checkBox.isSelected()) {
                            //Iterates over the list of item details to find an item that corresponds to the selected checkbox.
                            for (ItemDetailsParser.ItemDetail itemDetail : itemDetails) {
                                //Checks if the text of the item detail corresponds to the checkbox.
                                if (itemDetail.toString().equals(checkBox.getText())) {
                                    //Adds item detail to the list of selected items for the current participant.
                                    selectedItems.add(itemDetail);
                                    //Adds the item detail to the list of assigned items.
                                    assignedItems.add(itemDetail.toString());
                                    //Maps every item detail to a list of participants who are assigned to them.
                                    itemToParticipantsMap.computeIfAbsent(itemDetail, k -> new ArrayList<>()).add(participantName);
                                    break;
                                }
                            }
                        }
                    }
                    //Checks if no items are selected for every participant.
                    if (selectedItems.isEmpty()) {
                        //If the participant is not assigned to any items, set to false.
                        allParticipantSelected = false;
                    }
                }
                //Checks if all items are assigned to at least one participant.
                for (ItemDetailsParser.ItemDetail itemDetail : itemDetails) {
                    //If an item is not allocated to any participants, set to false.
                    if (!assignedItems.contains(itemDetail.toString())) {
                        allItemsAssigned = false;
                        break;
                    }
                }
                //Calculates total amount for every participant if allParticipantSelected and allItemsAssigned are true.
                if (allParticipantSelected && allItemsAssigned) {
                    //Creates a hash map to store the total amount each participant owes.
                    HashMap<String, Double> totalPricePerParticipant = TotalPricePerParticipant.calculateTotalPricePerParticipant(itemToParticipantsMap, service, tax, participantNames.size());

                    //Creates a VBox that displays total amount below the "Calculate" button.
                    VBox totalPriceBox = new VBox();
                    //Adds a CSS class to style.
                    totalPriceBox.getStyleClass().add("total-amount-box");
                    //Iterates over every entry in the totalPricePerParticipant map.
                    //Every entry contains the participant's name and the total amount owed.
                    for (Map.Entry<String, Double> entry : totalPricePerParticipant.entrySet()) {
                        //Creates a number formatter to format the price
                        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
                        //Sets the minimum number of fraction digits to 2.
                        formatter.setMinimumFractionDigits(2);
                        //Sets the maximum number of fraction digits to 2.
                        formatter.setMaximumFractionDigits(2);
                        //Formats the total price into a string.
                        String formattedPrice = formatter.format(entry.getValue());
                        //Creates a label to display total amount.
                        Label totalLabel = new Label("Total amount for " + entry.getKey() + ": Rp" + formattedPrice);
                        //Adds label to totalPriceBox.
                        totalPriceBox.getChildren().add(totalLabel);
                    }

                    //Removes existing total amount box and add new one if there is any changes in the distribution of items among participants.
                    scene3VBox.getChildren().removeIf(node -> node.getStyleClass().contains("total-amount-box"));
                    //Adds totalPriceBox to scene3VBox.
                    scene3VBox.getChildren().add(totalPriceBox);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Incomplete Selections");
                    alert.setHeaderText(null);
                    //Warning when there is an incomplete selection for on or more item or participant.
                    if (!allParticipantSelected && !allItemsAssigned) {
                        alert.setContentText("Each participant must have at least one item and all items must be assigned to at least one participant.");
                    }
                    //Warning when there is one or more participants that doesn't have an item assigned to them.
                    else if (!allParticipantSelected) {
                        alert.setContentText("Each participant must have at least one item.");
                    }
                    //Warning when there is one or more items that is not assigned to any participant.
                    else {
                        alert.setContentText("All items must be assigned to at least one participant.");
                    }
                    alert.showAndWait();
                }
            });

            //Adds the "Select All" button.
            scene3VBox.getChildren().add(selectAll);
            //Adds the "Calculate" button.
            scene3VBox.getChildren().add(calculateButton);
            ScrollPane scrollPane3 = new ScrollPane(scene3VBox);
            scrollPane3.setFitToWidth(true);
            stage.setScene(new Scene(scrollPane3, 650, 700));
            scene3VBox.getStylesheets().add(css);
            stage.setTitle("EasySplit");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}