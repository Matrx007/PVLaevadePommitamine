package com.meietiim.pvlaevadepommitamine;

import com.ydgames.mxe.Game;
import com.ydgames.mxe.GameContainer;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.SplittableRandom;

public class FrontEnd extends GameContainer {
    public static final FrontEnd MAIN = new FrontEnd();
    
    // ### ENGINE ###
    public Game game;
    
    // ### RENDERING ###
    public static final String RENDERER = PConstants.P2D;
    
    private PGraphics screenBuffer;
    private int boardTileW = 48;
    private int boardTileH = 48;
    private int boardTileGap = 8;
    private float boardOffsetX;  // Calculated automatically
    private float boardOffsetY; // Calculated automatically
    
    // ### GAME DATA ###
    private BackEnd backEnd;
    
    // Board state
    public boolean[][] playerShips = new boolean[10][10];
    public boolean[][] playerBombs = new boolean[10][10];
    public boolean[][] computerShips = new boolean[10][10];
    public boolean[][] computerBombs = new boolean[10][10];
    
    // Placing a ship
    public int playerPlaceShipX, playerPlaceShipY, playerPlaceShipW, playerPlaceShipH;
    
    // Game state
    public int action; // 0 - Player placed a ship
                       // 1 - Player placed a bomb
                       // 2 - Computer's turn
    public int error;  // 0 - Wrong ship placement
    
    // ### INPUT ###
    public int mouseSlotX, mouseSlotY;
    
    // ### A CREATIVE NAME ###
    public SplittableRandom random;
    
    // Make this class a singleton
    private FrontEnd() {
    
    }
    
    @Override
    public void setup() {
        game = getGame();
        
        backEnd = new BackEnd();
        
        random = new SplittableRandom();
        
        screenBuffer = game.createGraphics(game.pixelWidth, game.pixelHeight, RENDERER);
        
        boardOffsetX =  (game.pixelWidth - boardTileW*10 - boardTileGap*9) / 2f;
        boardOffsetY = (game.pixelHeight - boardTileH*10 - boardTileGap*9) / 2f;
    }
    
    double deltaTime = 0;
    @Override
    public void update(double v) {
        deltaTime += v;
        if(deltaTime >= 1f/60f) {
            deltaTime -= 1f/60f;
            updateTick();
        }
    }
    
    @Override
    public void updateTick() {
        mouseSlotX = (int)((game.mouseX - boardOffsetX) / (boardTileW + boardTileGap));
        mouseSlotY = (int)((game.mouseY - boardOffsetY) / (boardTileH + boardTileGap));
        
        if(game.input.isButtonDown(PConstants.LEFT)) {
            action = 0;
            playerPlaceShipX = mouseSlotX;
            playerPlaceShipY = mouseSlotY;
            
            int dimension = random.nextInt(3)+1;
            if(random.nextBoolean()) {
                playerPlaceShipW = dimension;
                playerPlaceShipH = 1;
            } else {
                playerPlaceShipW = 1;
                playerPlaceShipH = dimension;
            }
    
            for(int i = playerPlaceShipX; i < playerPlaceShipX+playerPlaceShipW; i++) {
                for(int j = playerPlaceShipY; j < playerPlaceShipY+playerPlaceShipH; j++) {
                    if(i > 0 && j > 0 && i < 10 && j < 10) playerShips[i][j] = true;
                }
            }
            
            backEnd.update();
        }
    }
    
    @Override
    public void render() {
        // ### RENDERING ###
        
        screenBuffer.beginDraw();
        
        screenBuffer.fill(204, 126, 10);
        screenBuffer.noStroke();
        screenBuffer.rect(0, 0, game.pixelWidth, game.pixelHeight);
        
        screenBuffer.noStroke();
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                float alpha = 32;
                
                alpha = mouseSlotX == i && mouseSlotY == j ? 64 : alpha;
                if(playerShips[i][j]) {
                    alpha = 255;
                }
                
                screenBuffer.fill(0, 0, 0, alpha);
                screenBuffer.rect(boardOffsetX + (boardTileW + boardTileGap) * i,
                        boardOffsetY + (boardTileH + boardTileGap) * j,
                        boardTileW, boardTileH);
            }
        }
        
        screenBuffer.endDraw();
        
        game.image(screenBuffer, 0, 0);
        
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