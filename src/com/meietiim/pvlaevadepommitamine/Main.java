package com.meietiim.pvlaevadepommitamine;

import com.ydgames.mxe.Game;
import com.ydgames.mxe.GameContainer;
import processing.core.PConstants;

public class Main extends GameContainer {
    public static final Main MAIN = new Main();
    
    // ### RENDERING ###
    public static final String RENDERER = PConstants.P2D;
    
    // Make this class a singleton
    private Main() {
    
    }
    
    @Override
    public void setup() {
    
    }
    
    @Override
    public void update(double v) {
    
    }
    
    @Override
    public void updateTick() {
    
    }
    
    @Override
    public void render() {
    
    }
    
    
    
    
    
    
    
    
    
    
    @Override
    public void settings() {
    
    }
    
    @Override
    public void init() {
    
    }
    
    
    // Program's entry point
    public static void main(String[] args) {
        // Start our game
        Game.createGame(1280, 720, MAIN, 60f, RENDERER);
    }
}