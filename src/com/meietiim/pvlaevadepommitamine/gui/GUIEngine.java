package com.meietiim.pvlaevadepommitamine.gui;

import com.meietiim.pvlaevadepommitamine.FrontEnd;
import com.ydgames.mxe.Game;
import processing.core.PGraphics;

import java.util.ArrayList;

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
    
    // ### COMPONENTS ###
    
    // All components currently in the scene
    private ArrayList<GUIComponent> components;
    // Component add queue
    private ArrayList<GUIComponent> componentsAdd;
    // Component remove queue
    private ArrayList<GUIComponent> componentsRemove;
    
    public void setup(Game game) {
        
        // ### ENGINE STUFF ###
        
        this.game = game;
        
        // ### COMPONENTS ###
        
        // Set up array lists
        components = new ArrayList<>();
        componentsAdd = new ArrayList<>();
        componentsRemove = new ArrayList<>();
        
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
        components.addAll(componentsAdd);
        components.removeAll(componentsRemove);
        
        componentsAdd.clear();
        componentsRemove.clear();
    }
    
    // Add an object
    public void add(GUIComponent component) {
        componentsAdd.add(component);
    }
    
    // Remove an object
    public void remove(GUIComponent component) {
        componentsRemove.add(component);
    }
    
    // Add objects
    public void add(ArrayList<GUIComponent> components) {
        componentsAdd.addAll(components);
    }
    
    // Remove objects
    public void remove(ArrayList<GUIComponent> components) {
        componentsRemove.addAll(components);
    }
    
    // Get all components
    public ArrayList<GUIComponent> getComponents() {
        return components;
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
