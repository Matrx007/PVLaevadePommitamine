package com.meietiim.pvlaevadepommitamine.gui.components;

import com.meietiim.pvlaevadepommitamine.gui.GUIComponent;
import com.meietiim.pvlaevadepommitamine.gui.GUIEngine;
import processing.core.PConstants;

public class Button extends GUIComponent {
    
    // ### RENDERING ###
    
    // The color of the button, changes when hovering
    private float color = 0;
    
    // The text on the button
    private String buttonText;
    
    // ### BEHAVIOUR ###
    
    // Executed when left clicked on the button
    private Action onClick;
    
    public Button(float x, float y, int width, int height,
                  Action onClick, String buttonText, GUIEngine engine) {
        super(x, y, width, height, engine);
    
        // ### RENDERING ###
        this.buttonText = buttonText;
        
        // ### BEHAVIOUR ###
        this.onClick = onClick;
    }
    
    @Override
    public void update() {
        
        // If mouse hovering
        if(engine.game.mouseX >= x &&
                engine.game.mouseY >= y &&
                engine.game.mouseX <= x + engine.game.width &&
                engine.game.mouseY <= y + engine.game.height) {
            // Fade the color to 1
            color += (1f - color) * 0.5f;
            
            // If left clicked, execute onClick action
            if(engine.game.input.isButtonDown(PConstants.LEFT)) {
                onClick.action();
            }
        } else {
            // If mouse not hovering, fade the color to 0
            color += (0f - color) * 0.5f;
        }
    }
    
    @Override
    public void render() {
        // ### Button's body ###
        
        // Body has no outline and is black or dark grey when hovered
        engine.drawingSurface.noStroke();
        engine.drawingSurface.fill(color*64f, color*64f, color*64f);
    
        // Draw button's body
        engine.drawingSurface.rect(x, y, width, height);
    
        // ### Button's text ###
        
        // Text is centered, has no outline and is white
        engine.drawingSurface.textAlign(PConstants.CENTER, PConstants.CENTER);
        engine.drawingSurface.noStroke();
        engine.drawingSurface.fill(255);
        
        // Draw button's text
        engine.drawingSurface.text(buttonText, x+width/2f, y+height/2f);
    }
}
