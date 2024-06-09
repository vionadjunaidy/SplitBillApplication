package com.example.javafx;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ImageCropper {
    //Crops an image based on the specified bounds.
    public File crop(Bounds bounds, File originalImageFile, ImageView imageView, int scale) {
        //Gets the directory and filename from the original image file path.
        String originalFilePath = originalImageFile.getAbsolutePath();
        String originalDirectory = originalImageFile.getParent();
        String originalFileName = originalImageFile.getName();

        //Constructs the file path for the cropped image.
        //The cropped image should be saved in the same directory with the same file name followed by "_cropped".
        String croppedFileName = originalFileName.replaceFirst("[.][^.]+$", "_cropped.png");
        String croppedFilePath = Paths.get(originalDirectory, croppedFileName).toString();
        File croppedFile = new File(croppedFilePath);

        //Casts the minimum X-coordinate of the bounds in to an integer of the top-left corner of the selected region.
        int x = (int) bounds.getMinX();
        //Casts the minimum Y-coordinate of the bounds in to an integer of the top-left corner of the selected region
        int y = (int) bounds.getMinY();
        //Retrieves the width of the bounding box (bounds) and casts it to an integer to get the width of the selected region.
        int width = (int) bounds.getWidth();
        //Retrieves the height of the bounding box (bounds) and casts it to an integer to get the height of the selected region.
        int height = (int) bounds.getHeight();

        //Creates a new instance of SnapshotParameters to specify the components of the snapshot.
        SnapshotParameters parameters = new SnapshotParameters();
        //Sets the fill color of the snapshot to transparent, indicating that any areas not covered by the snapshot region will be transparent.
        parameters.setFill(Color.TRANSPARENT);
        //Defines the area for the snapshot using the specified X, Y coordinates, width, and height.
        parameters.setViewport(new Rectangle2D(x, y, width, height));
        //Creates a new WritableImage object with the specified width and height to represent the cropped region.
        WritableImage croppedImage = new WritableImage(width, height);
        //Takes a snapshot of the content displayed in the ImageView using the specified parameters and stores it in the croppedImage object.
        imageView.snapshot(parameters, croppedImage);

        //Converts the WritableImage to BufferedImage.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //Retrieves a PixelReader from the croppedImage, which allows reading of pixel data from the image.
        PixelReader pixelReader = croppedImage.getPixelReader();
        //Iterates over the Y-axis of the cropped region.
        for (int py = 0; py < height; py++) {
            //Iterates over the X-axis of the cropped region.
            for (int px = 0; px < width; px++) {
                //Reads the ARGB value of the pixel at (px, py) from the croppedImage and sets the corresponding pixel in the bufferedImage with the same color value.
                bufferedImage.setRGB(px, py, pixelReader.getArgb(px, py));
            }
        }

        //Scales the BufferedImage.
        bufferedImage = resizeImage(bufferedImage, width * scale, height * scale);
        try {
            //Writes the BufferedImage to the cropped file
            ImageIO.write(bufferedImage, "png", croppedFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        //Returns the cropped file.
        return croppedFile;
    }

    //Resizes the BufferedImage to the specified dimensions.
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        //Creates a new BufferedImage object.
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        //Creates a Graphics2D object to control the rendering hints.
        Graphics2D graphics2D = resizedImage.createGraphics();
        //Sets rendering hints for interpolation. This is done to improve quality and reduces pixelation.
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        //Resizes the original image to the target dimensions.
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        //Disposes the graphics object.
        graphics2D.dispose();
        //Return the resized image.
        return resizedImage;
    }
}
