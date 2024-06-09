package com.example.javafx;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class BillParser {
    //Abstract method to parse the bill text and return a VBox containing item details.
    public abstract VBox parse(String billText, List<ItemDetailsParser.ItemDetail> itemDetails);

    //Abstract method to get the service charge.
    public abstract double getService();

    //Abstract method to get the tax.
    public abstract double getTax();
}

public class ItemDetailsParser extends BillParser {
    //Stores the service charge.
    private double service;
    //Stores the tax.
    private double tax;

    //Class representing an item detail.
    public static class ItemDetail {
        //Name of the item.
        String itemName;
        //Quantity of the item
        int quantity;
        //Price of the item.
        double price;

        //Constructor to initialize an item detail.
        public ItemDetail(String itemName, int quantity, double price) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
        }

        //Getter to retrieve the item name.
        public String getName() {
            return itemName;
        }

        //Getter to retrieve the item price.
        public double getPrice() {
            return price;
        }

        //Getter to retrieve the item quantity.
        public int getQuantity() {
            return quantity;
        }

        //Override toString method to provide a formatted string representation of the item detail.
        @Override
        public String toString() {
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            String formattedPrice = formatter.format(price);
            return itemName + " | Quantity: " + quantity + " | Price: Rp" + formattedPrice;
        }
    }

    //Override method to get the service charge.
    @Override
    public double getService() {
        return service;
    }

    //Override method to get the tax.
    @Override
    public double getTax() {
        return tax;
    }

    //Override method to parse the bill text and extract item details, service charge, and tax.
    @Override
    public VBox parse(String billText, List<ItemDetail> itemDetails) {
        //Creates a container to hold item details.
        VBox itemDetailsBox = new VBox();
        //Replaces dots in prices with commas.
        billText = billText.replaceAll("\\.(?=\\d{3})", ",");
        //Creates a pattern to match the item name, price, and quantity in the bill text.
        //'(\d+)': 1 or more digits, representing quantity.
        //'\s+': one or more whitespace
        //'([\w\s]+?)': one or more word characters or whitespace characters, representing item name.
        //'(\d{1,3}(?:,\d{3})*(?:\.\d{1,2})?)': numeric value with thousands separators and decimal places, representing price.
        Pattern itemPattern = Pattern.compile("(\\d+)\\s+([\\w\\s]+?)\\s+(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?)");
        //Creates a Matcher object to match the pattern with billText.
        Matcher itemMatcher = itemPattern.matcher(billText);
        //Iterates over all the matches.
        while (itemMatcher.find()) {
            //Extracts quantity as integer.
            int quantity = Integer.parseInt(itemMatcher.group(1));
            //Extracts item name as string and removes any unnecessary whitespaces before or after the item name.
            String itemName = itemMatcher.group(2).trim();
            //Extracts price as double.
            double price = Double.parseDouble(itemMatcher.group(3).replace(",", ""));
            //Creates an ItemDetail object to store extracted item details.
            ItemDetail itemDetail = new ItemDetail(itemName, quantity, price);
            //Adds the ItemDetail object to a list of item details
            itemDetails.add(itemDetail);
            //Creates a NumberFormat instance to format the price value as a string with two decimal places.
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            String formattedPrice = formatter.format(price);
            //Label to display item details.
            Label itemDetailsLabel = new Label(itemName + " | Quantity: " + quantity + " | Price: Rp" + formattedPrice);
            VBox itemBox = new VBox(itemDetailsLabel);
            itemBox.getStyleClass().add("item-details-box");
            itemDetailsBox.getChildren().add(itemBox);
        }

        //Extract service and tax
        //Creates a pattern to match service charge and tax in the bill text.
        //'\\s*': zero or more whitespace characters.
        //'\\(?': optional opening parenthesis '('.
        //'\\d{1,2}%': one or two digits followed by a percent sign (e.g., "5%", "10%").
        //'\\)?': optional closing parenthesis ')'.
        //'?': makes the entire non-capturing group optional.
        //'\\s*:?\\s+': zero or more whitespaces and an optional colon, followed by one or more whitespaces.
        //'(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?)': numeric value with optional thousands separators and optional decimal places.
        Pattern extraPattern = Pattern.compile("(SERVICE CHARGE|SERVICE|SC|PB1|PB|TAX)\\s*(?:\\(?\\d{1,2}%\\)?)?\\s*:?\\s+(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{1,2})?)", Pattern.CASE_INSENSITIVE);
        Matcher extraMatcher = extraPattern.matcher(billText);
        //Loops through matches and extract service charge and tax.
        while (extraMatcher.find()) {
            //Extracts label, such as "SERVICE CHARGE", "SERVICE", "SC", "PB1".
            String label = extraMatcher.group(1);
            //Extracts value of service charge or tax.
            double value = Double.parseDouble(extraMatcher.group(2).replace(",", ""));
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            String formattedValue = formatter.format(value);
            Label extraLabel;
            //Creates label for service charge or tax based on the label.
            if (label.equalsIgnoreCase("SERVICE CHARGE") || label.equalsIgnoreCase("SERVICE") || label.equalsIgnoreCase("SC")) {
                service = value;
                extraLabel = new Label("Service Charge: Rp" + formattedValue);
            } else if (label.equalsIgnoreCase("PB1") || label.equalsIgnoreCase("TAX") || label.equalsIgnoreCase("PB")) {
                tax = value;
                extraLabel = new Label("TAX: Rp" + formattedValue);
            } else {
                //Skips if labels for service charge and tax are not recognized.
                continue;
            }
            extraLabel.getStyleClass().add("regular-text");
            //Adds label to main container.
            itemDetailsBox.getChildren().add(extraLabel);
        }
        return itemDetailsBox;
    }
}