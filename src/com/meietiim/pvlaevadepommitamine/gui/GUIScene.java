package com.meietiim.pvlaevadepommitamine.gui;

import java.util.ArrayList;

public class GUIScene {
    
    // All components currently in the scene
    private ArrayList<GUIComponent> components;
    // Component add queue
    private ArrayList<GUIComponent> componentsAdd;
    // Component remove queue
    private ArrayList<GUIComponent> componentsRemove;
    
    public GUIScene() {
        components = new ArrayList<>();
        componentsAdd = new ArrayList<>();
        componentsRemove = new ArrayList<>();
    }

    // Render all the visible components
    public void render() {
        for(GUIComponent component : components) {
            if(component.visible) {
                component.render();
            }
        }
    }

    // Update all the components
    public void update() {
        flush();
        
        for(GUIComponent component : components) {
            component.update();
        }
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
    
    // Returns all components
    public ArrayList<GUIComponent> getComponents() {
        return components;
    }
}
