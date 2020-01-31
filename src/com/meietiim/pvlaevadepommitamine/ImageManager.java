package com.meietiim.pvlaevadepommitamine;

/**
 * Loads, stores and manages images.
 *
 * @author Rainis Randmaa
 */

import com.ydgames.mxe.Game;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
public class ImageManager {

    // Store 'Game' in order to load images with built'in methods
    private Game game;

    // Requested images [image name, relative file path]
    private ArrayList<String[]> requestedImages;

    // Storing loaded images
    private HashMap<String, PImage> loadedImages;

    // Initialize required variables and load requested assets
    public ImageManager(Game game) {
        this.game = game;

        // Initialize stuff
        requestedImages = new ArrayList<>();
        loadedImages = new HashMap<>();
    }

    public void loadRequestedImages() {
        for(String[] image : requestedImages) {

            // Attempt to read the image
            PImage loadedImage = game.loadImage(image[1]);

            // If image fails to load, print an error and skip to next one
            if(loadedImage == null) {
                System.out.println("Failed to load image ");
                continue;
            }

            // Image load successfully
            // Store the loaded image
            loadedImages.put(image[0], loadedImage);
        }

        // Finally remove all image from requestedImages list
        // to avoid re-loading them repeatedly
        requestedImages.clear();
    }

    public PImage getImage(String image) {
        return loadedImages.get(image);
    }

    public void addToQueue(String image, String file) {
        requestedImages.add(new String[]{image, file});
    }
}
