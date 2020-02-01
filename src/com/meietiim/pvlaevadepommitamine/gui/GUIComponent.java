package com.meietiim.pvlaevadepommitamine.gui;

import com.ydgames.mxe.Game;

public abstract class GUIComponent {
    
    // Properties
    public boolean visible;
    
    // Component's bounds
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    
    // For easy access to methods
    protected GUIEngine engine;
    
    
    public GUIComponent(float x, float y, int width, int height, GUIEngine engine) {
        this.engine = engine;
    
        // Component's bounds
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        // Objects are invisible by default
        visible = false;
    }
    
    public abstract void update();
    public abstract void render();
}
