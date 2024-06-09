package com.example.javafx;

import java.io.*;

public class OCRProcessor {
    //Path to the Tesseract installation directory.
    private String tesseractInstallPath;

    //Constructor to initialize the Tesseract installation path.
    public OCRProcessor(String tesseractInstallPath) {
        this.tesseractInstallPath = tesseractInstallPath;
    }

    //Method to perform OCR on an image file and extract text.
    public String performOCR(File imageFile) {
        try {
            //Creates a command to execute Tesseract OCR via command prompt using "cmd".
            String outputFile = imageFile.getAbsolutePath().replaceFirst("[.][^.]+$", "");
            String[] command = {"cmd"};
            Process p = Runtime.getRuntime().exec(command);
            //Starts threads to handle process output streams.
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            //Setups communication with the command prompt.
            BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //Consumes initial command prompt output.
            stdin.readLine();
            //Consumes secondary command prompt output
            stdin.readLine();
            PrintWriter stdinWriter = new PrintWriter(p.getOutputStream());
            //Executes Tesseract command to perform OCR.
            stdinWriter.println("\"" + tesseractInstallPath + "\" \"" + imageFile.getAbsolutePath() + "\" \"" + outputFile + "\" -l eng");
            stdinWriter.close();
            p.waitFor();

            //Reads the extracted text from the output file.
            return Read_File.read_a_file(outputFile + ".txt");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}