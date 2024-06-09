package com.example.javafx;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RubberBandSelection {
    //Stores the mouse drag context.
    final DragContext dragContext = new DragContext();
    //The rectangle used for rubber band selection.
    javafx.scene.shape.Rectangle rect = new Rectangle();

    public RubberBandSelection(Group group) {
        //Sets the visuals of the rectangle.
        rect.setStroke(Color.BLUE);
        rect.setStrokeWidth(1);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));
        //Adds event handlers to the group for mouse interaction.
        group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
        group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
        group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
        //Adds the rectangle to the group for display.
        group.getChildren().add(rect);
    }

    //Event handler for mouse pressed event.
    // Initializes the position and dimensions of the rectangle.
    EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //Checks if right mouse button is pressed, and return if true.
            if (event.isSecondaryButtonDown()) return;
            //Sets initial position and dimensions of the rectangle.
            rect.setX(event.getX());
            rect.setY(event.getY());
            rect.setWidth(0);
            rect.setHeight(0);
            //Store initial mouse anchor position
            dragContext.mouseAnchorX = event.getX();
            dragContext.mouseAnchorY = event.getY();
        }
    };
    //Event handler for mouse dragged event.
    //Adjusts the dimensions of the rectangle based on the movement of the mouse.
    EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //Checks if right mouse button is pressed, and return if true.
            if (event.isSecondaryButtonDown()) return;
            //Calculates horizontal distance from initial mouse position.
            double offsetX = event.getX() - dragContext.mouseAnchorX;
            //Calculates vertical distance from initial mouse position.
            double offsetY = event.getY() - dragContext.mouseAnchorY;
            //When mouse moved to positive X direction.
            if (offsetX > 0) {
                rect.setWidth(offsetX);
            }
            //When mouse moved to negative X direction.
            else {
                //Sets X coordinate of the rectangle to the current X coordinate of the mouse.
                rect.setX(event.getX());
                //Sets the width of the rectangle to the difference between the initial X
                // coordinate of the mouse anchor position and the current X coordinate of the mouse.
                rect.setWidth(dragContext.mouseAnchorX - event.getX());
            }
            if (offsetY > 0) {
                rect.setHeight(offsetY);
            }
            //When mouse moved to negative X direction.
            else {
                //Sets Y coordinate of the rectangle to the current Y coordinate of the mouse.
                rect.setY(event.getY());
                //Sets the height of the rectangle to the difference between the initial Y
                // coordinate of the mouse anchor position and the current Y coordinate of the mouse.
                rect.setHeight(dragContext.mouseAnchorY - event.getY());
            }
        }
    };
    //Event handler for mouse released event.
    EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //Checks if right mouse button is pressed, and return if true.
            if (event.isSecondaryButtonDown()) return;
        }
    };

    //Method to obtain the bounds of the rubber band selection.
    public Bounds getBounds() {
        return rect.getBoundsInParent();
    }

    //Inner class to store the initial position of the mouse.
    private static final class DragContext {
        public double mouseAnchorX;
        public double mouseAnchorY;
    }
}
