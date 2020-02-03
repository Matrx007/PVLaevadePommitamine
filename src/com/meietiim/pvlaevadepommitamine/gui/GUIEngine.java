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
    // Scene add queue
    private HashMap<String, GUIScene> scenesAdd;
    // Scene remove queue
    private HashMap<String, GUIScene> scenesRemove;

    // To identify currently active scene
    private GUIScene currentScene;
    private String currentSceneName;

    public void setup(Game game) {
        
        // ### ENGINE STUFF ###
        
        this.game = game;
        
        // ### COMPONENTS ###
        
        // Set up array lists
        scenes = new HashMap<>();
        scenesAdd = new HashMap<>();
        scenesRemove = new HashMap<>();

        // Default scene is null
        currentScene = null;
        currentSceneName = null;
        
        // ### RENDERING ###
        
        // Initialize drawingSurface
        drawingSurface = game.createGraphics(game.pixelWidth,
                game.pixelHeight,
                FrontEnd.RENDERER);
    }
    
    // Make all objects visible
    public void show() {
        for(GUIComponent component : components) {
            component.visible = true;
        }
    }
    
    // Make all objects invisible
    public void hide() {
        for(GUIComponent component : components) {
            component.visible = false;
        }
    }
    
    // Add and remove all components in queue
    public void flush() {
        scenes.putAll(scenesAdd);
        scenes.remove(componentsRemove);
        
        componentsAdd.clear();
        componentsRemove.clear();
    }
    
    // Add a scene
    public void add(GUIScene scene, String name) {
        scenesAdd.put(name, scene);
    }
    
    // Remove a scene
    public void remove(GUIScene scene, String name) {
        scenesRemove.put(name, scene);
    }
    
    // Get all scenes
    public HashMap<String, GUIComponent> getScenes() {
        return scenes;
    }
    
    // Render all visible objects
    public void render() {
        // Initialize the surface
        drawingSurface.beginDraw();
        drawingSurface.clear();
        
        // Render all visible components
        for(GUIComponent component : components) {
            if(component.visible) {
                component.render();
            }
        }
        
        // Flush surface
        drawingSurface.endDraw();
    }
    
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
    
        // Update all the components
        for(GUIComponent component : components) {
            component.update();
        }
    }
}
