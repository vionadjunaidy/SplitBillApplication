package com.example.javafx;

import java.io.BufferedReader;
import java.io.FileReader;

//Utility class for reading the contents of a file.
public class Read_File {
    //Method to read the contents of a file and return as a single string.
    public static String read_a_file(String file_name) {
        //Initializes a BufferedReader object to read the text from the file.
        BufferedReader br = null;
        //Initializes an empty string to hold the contents of the file.
        String read_string = "";
        try {
            //Initializes variables for reading lines from the file.
            String sCurrentLine;
            //Creates a BufferedReader that reads the specified file.
            br = new BufferedReader(new FileReader(file_name));
            //Reads each line from the file.
            while ((sCurrentLine = br.readLine()) != null) {
                //Appends each line to the read_string.
                read_string = read_string + sCurrentLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //Closes the BufferedReader if it's not null.
                if (br != null) br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //Return the contents of the file as a single string
        return read_string;
    }
}