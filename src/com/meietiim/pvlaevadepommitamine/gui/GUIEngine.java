package com.meietiim.pvlaevadepommitamine.gui;

import com.meietiim.pvlaevadepommitamine.FrontEnd;
import com.ydgames.mxe.Game;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIEngine {
    
    // ### ENGINE STUFF ###
    
    // Provides easy access to Game methods
    public Game game;
    
    // ### RENDERING ###
    
    // A surface that all the objects are drawn onto
    public PGraphics drawingSurface;
    
    // The current and previous dimensions of drawingSurface
    public int width, previousWidth;
    public int height, previousHeight;

    // ### SCENES ###

    // All scenes by their nickname
    private HashMap<String, GUIScene> scenes;

    // To identify currently active scene
    private GUIScene currentScene;
    private String currentSceneName;

    public void setup(Game game) {
        
        // ### ENGINE STUFF ###
        
        this.game = game;
        
        // ### COMPONENTS ###
        
        // Set up array lists
        scenes = new HashMap<>();

        // Default scene is null
        currentScene = null;
        currentSceneName = null;
        
        // ### RENDERING ###
        
        // Initialize drawingSurface
        drawingSurface = game.createGraphics(game.pixelWidth,
                game.pixelHeight,
                FrontEnd.RENDERER);
    }
    
    // Activate a scene
    public void show(String scene) {
        // Find the scene
        GUIScene foundScene = scenes.get(scene);
        
        // If the scene exists, active it
        if(foundScene != null) {
            currentScene = foundScene;
            currentSceneName = scene;
        }
    }
    
    // Deactivate current scene
    public void hide() {
        currentScene = null;
        currentSceneName = null;
    }
    
    // Add a scene
    public void add(GUIScene scene, String name) {
        scenes.put(name, scene);
    }
    
    // Remove a scene
    public void remove(String name) {
        scenes.remove(name);
    }
    
    // Return all scenes
    public HashMap<String, GUIScene> getScenes() {
        return scenes;
    }
    
    // Render current scene
    public void render() {
        // Initialize the surface
        drawingSurface.beginDraw();
        drawingSurface.clear();
        
        // Render only if a scene is active
        if(currentScene != null) {
            
            // Render the scene
            currentScene.render();
        }
        
        // Flush surface
        drawingSurface.endDraw();
    }
    
    // Update current scene
    public void update() {
        // Resize drawingSurface if necessary
        if(previousWidth != width ||
                previousHeight != height) {
            // Update variables
            previousWidth = width;
            previousHeight = height;
            width = game.pixelWidth;
            height = game.pixelHeight;
            
            // Create a new surface with correct size
            drawingSurface = game.createGraphics(
                    width, height, FrontEnd.RENDERER);
        }
    
        // Only update elements if a scene is active
        if(currentScene != null) {
            // Update the current scene
            currentScene.update();
        }
    }
}
