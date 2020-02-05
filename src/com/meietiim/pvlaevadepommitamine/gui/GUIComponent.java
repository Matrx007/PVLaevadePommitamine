package com.meietiim.pvlaevadepommitamine.gui;

import com.meietiim.pvlaevadepommitamine.gui.constraints.Constraint;
import com.ydgames.mxe.Game;

public abstract class GUIComponent {
    
    // Properties
    public boolean visible;
    
    // Component's bounds
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    
    // Constraints
    protected Constraint xConstraint;
    protected Constraint yConstraint;
    protected Constraint widthConstraint;
    protected Constraint heightConstraint;
    
    // For easy access to methods
    protected GUIEngine engine;
    
    // Creating an object
    public GUIComponent(Constraint xConstraint,
                        Constraint yConstraint,
                        Constraint widthConstraint,
                        Constraint heightConstraint,
                        GUIEngine engine) {
        this.engine = engine;
        
        // Constraints
        this.xConstraint = xConstraint;
        this.yConstraint = yConstraint;
        this.widthConstraint = widthConstraint;
        this.heightConstraint = heightConstraint;
        
        // Objects are invisible by default
        visible = false;
    }
    
    // Runs every tick cycle, implemented by the developer
    public abstract void tick();
    
    // Runs every tick cycle
    public void update() {
        // Calculate components location and size
        x = xConstraint.calculate();
        y = yConstraint.calculate();
        width = (int)widthConstraint.calculate();
        height = (int)heightConstraint.calculate();
        
        tick();
    }
    
    // Runs every frame, implemented by the developer
    public abstract void render();
}
