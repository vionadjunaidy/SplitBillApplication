package com.example.javafx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TotalPricePerParticipant {
    //Method to calculate the total amount every participant needs to pay.
    public static HashMap<String, Double> calculateTotalPricePerParticipant(HashMap<ItemDetailsParser.ItemDetail,
            List<String>> itemToParticipantsMap, double service, double tax, int numberOfParticipants) {
        //Initializes a hash map to store total amount for every participant
        HashMap<String, Double> totalPricePerParticipant = new HashMap<>();

        //Iterates over every entry in the itemToParticipantsMap.
        for (Map.Entry<ItemDetailsParser.ItemDetail, List<String>> entry : itemToParticipantsMap.entrySet()) {
            //Obtains item detail.
            ItemDetailsParser.ItemDetail itemDetail = entry.getKey();
            //Obtains participant names for the item.
            List<String> participantNames = entry.getValue();
            //Calculates price per participant for the item by dividing the price by the number of participants assigned to the item.
            double pricePerParticipant = itemDetail.getPrice() / participantNames.size();
            //Distributes the price per participant among all participants for the item.
            for (String participant : participantNames) {
                totalPricePerParticipant.put(participant,
                        totalPricePerParticipant.getOrDefault(participant, 0.0) + pricePerParticipant);
            }
        }

        //Divides service charge and tax equally for all participants.
        double servicePerParticipant = service / numberOfParticipants;
        double taxPerParticipant = tax / numberOfParticipants;

        //Iterates over every entry in the totalPricePerParticipant map.
        for (Map.Entry<String, Double> entry : totalPricePerParticipant.entrySet()) {
            //Obtains participant name.
            String participant = entry.getKey();
            //Obtains total price for the participant.
            double totalPrice = entry.getValue();

            //Adds service charge and tax to the total price for the participant.
            totalPricePerParticipant.put(participant, totalPrice + servicePerParticipant + taxPerParticipant);
        }
        //Returns the map containing total price per participant.
        return totalPricePerParticipant;
    }
}
