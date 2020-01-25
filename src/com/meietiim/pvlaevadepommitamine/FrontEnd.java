package com.meietiim.pvlaevadepommitamine;

import com.ydgames.mxe.Game;
import com.ydgames.mxe.GameContainer;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.SplittableRandom;

/**
 *
 * @author Rainis Randmaa
 * */

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
    
    // Ships to be placed (by their length)
    public static final int[] SHIPS = new int[] {5, 4, 3, 3, 2};
    
    // Board state
    public boolean[][] playerShips = new boolean[10][10];
    public boolean[][] playerBombs = new boolean[10][10];
    public boolean[][] computerShips = new boolean[10][10];
    public boolean[][] computerBombs = new boolean[10][10];
    
    // row:ID - ship id, column:DATA - 0=x, 1=y, 2=w, 3=h, 4=bombs hit, 5=(1 - dead, 0 - alive)
    public int[][] playerShipData = new int[5][6];
    public int[][] computerShipData = new int[5][6];
    
    // The ID of the next ship
    public int nextShipID = 0;
    
    public boolean playersTurn = true; // Player's turn
    
    // Placing a ship
    public int playerPlaceShipX, playerPlaceShipY, playerPlaceShipW, playerPlaceShipH, playerPlaceShipS;
    
    // Placing a bomb
    public int playerPlaceBombX, playerPlaceBombY;
    public int computerPlaceBombX, computerPlaceBombY;
    
    // Game state
    public final static int ACTION_PLACE_SHIP = 1;
    public final static int ACTION_PLACE_BOMB = 2;
    public final static int ACTION_PLACE_COMPUTER = 3;
    
    public int action; // Use ACTION_... constants
    
    public final static int ERROR_NONE = 1;
    public final static int ERROR_UNKNOWN = 2;
    public final static int ERROR_INCORRECT_PLACEMENT = 3;
    public final static int ERROR_OUT_OF_SHIPS = 4;
    
    public int error; // Use ERROR_... constants
    
    public final static int RESPONSE_EMPTY = 1;
    public final static int RESPONSE_HIT = 2;
    public final static int RESPONSE_DEAD = 3;
    
    public int response; // Use RESPONSE_... constants
    
    // ### INPUT ###
    public int mouseSlotX, mouseSlotY;
    
    private boolean shipOrientation; // true - horizontal, false - vertical
    
    // ### A CREATIVE NAME ###
    public SplittableRandom random;
    
    // ### DEBUGGING ###
    private boolean collision;
    
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
    
    @Override
    public void updateTick() {
        // If mouse inside the grid
        if(game.mouseX >= boardOffsetX && game.mouseY >= boardOffsetY &&
                game.mouseX <= boardOffsetX+10*(boardTileW+boardTileGap) &&
                game.mouseY <= boardOffsetY+10*(boardTileH+boardTileGap)) {
            
            // Find the slot in which the mouse is in,
            // but only if the mouse is not in a gap
            if ((int) ((game.mouseX - boardOffsetX) / (boardTileW + boardTileGap)) <= boardTileW &&
                    (int) ((game.mouseY - boardOffsetY) / (boardTileH + boardTileGap)) <= boardTileH) {
                
                // Find a slot the mouse is in
                mouseSlotX = (int) ((game.mouseX - boardOffsetX) / (boardTileW + boardTileGap));
                mouseSlotY = (int) ((game.mouseY - boardOffsetY) / (boardTileH + boardTileGap));
            }
        }
        
        // Rotate ship
        if(game.input.isKeyDown('R')) {
            shipOrientation = !shipOrientation;
        }
    
        // Show if placing ship there is legal
        collision = true;
        if(nextShipID < 5) {
    
            int shipX = mouseSlotX;
            int shipY = mouseSlotY;
            int shipW = shipOrientation ? SHIPS[nextShipID] : 1;
            int shipH = shipOrientation ? 1 : SHIPS[nextShipID];
    
            collision = !isSpaceFree(shipX, shipY, shipW, shipH, playerShips);
        }
    
    
        // DEBUGGING ONLY, Place ships if any left
        if(game.input.isButtonDown(PConstants.LEFT) && nextShipID < 5) {
            action = 0;
            playerPlaceShipX = mouseSlotX;
            playerPlaceShipY = mouseSlotY;
    
            int dimension = SHIPS[nextShipID];
            if (shipOrientation) {
                playerPlaceShipW = dimension;
                playerPlaceShipH = 1;
            } else {
                playerPlaceShipW = 1;
                playerPlaceShipH = dimension;
            }
    
            // Check if the ship is out of bound
            boolean correctPlacement = isSpaceFree(
                    playerPlaceShipX,
                    playerPlaceShipY,
                    playerPlaceShipW,
                    playerPlaceShipH,
                    playerShips
            );
    
    
            if (correctPlacement) {
                playerShipData[nextShipID] = new int[]{
                        playerPlaceShipX,
                        playerPlaceShipY,
                        playerPlaceShipW,
                        playerPlaceShipH,
                        0,
                        0
                };
                nextShipID++;
        
                for (int i = playerPlaceShipX; i < playerPlaceShipX + playerPlaceShipW; i++) {
                    for (int j = playerPlaceShipY; j < playerPlaceShipY + playerPlaceShipH; j++) {
                        if (i >= 0 && j >= 0 && i < 10 && j < 10) {
                            playerShips[i][j] = true;
                        }
                    }
                }
            }
            
            //backEnd.update();
        }
    }
    
    @Override
    public void render() {
        // ### RENDERING ###
        
        screenBuffer.beginDraw();
        
        screenBuffer.fill(66, 164, 245);
        screenBuffer.noStroke();
        screenBuffer.rect(0, 0, game.pixelWidth, game.pixelHeight);
        
        screenBuffer.noStroke();
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                float alpha = 32;
                
                alpha = mouseSlotX == i && mouseSlotY == j ? 64 : alpha;
                if(computerBombs[i][j]) {
                    alpha = 255;
                }
                
                screenBuffer.fill(0, 0, 0, alpha);
                screenBuffer.rect(boardOffsetX + (boardTileW + boardTileGap) * i,
                        boardOffsetY + (boardTileH + boardTileGap) * j,
                        boardTileW, boardTileH);
            }
        }
        
        // Draw ships
        screenBuffer.fill(0, 0, 0);
        screenBuffer.noStroke();
        
        // Draw ships
        for(int i = 0; i < playerShipData.length; i++) {
            // Check weather this ship exists by checking if it's width is 0
            if(playerShipData[i][2] == 0) continue;
            
            // Draw a black rectangle as the ship
            screenBuffer.rect(
                    boardOffsetX + playerShipData[i][0] * (boardTileW + boardTileGap) + 4,
                    boardOffsetY + playerShipData[i][1] * (boardTileH + boardTileGap) + 4,
                    (boardTileW + boardTileGap) * playerShipData[i][2] - boardTileGap - 8,
                    (boardTileH + boardTileGap) * playerShipData[i][3] - boardTileGap - 8
            );
        }
        
        // Ship placing guide
        if(nextShipID < 5) {
            screenBuffer.fill(collision ? 192 : 0, 0, 0, collision ? 192 : 64);
            screenBuffer.rect(
                    boardOffsetX + mouseSlotX * (boardTileW + boardTileGap) + 4,
                    boardOffsetY + mouseSlotY * (boardTileH + boardTileGap) + 4,
                    (boardTileW + boardTileGap) * (shipOrientation ? SHIPS[nextShipID] : 1) - boardTileGap - 8,
                    (boardTileH + boardTileGap) * (shipOrientation ? 1 : SHIPS[nextShipID]) - boardTileGap - 8
            );
        }
        
        screenBuffer.endDraw();
        
        game.image(screenBuffer, 0, 0);
        
    }
    
    public boolean shipOverlap(int oneLX, int oneLY, int oneRX, int oneRY,
                               int twoLX, int twoLY, int twoRX, int twoRY) {
        if (oneLY > twoRY || twoLY > oneRY) {
            return false;
        }
        
        if (oneLX > twoRX || twoLX > oneRX) {
            return false;
        }
    
        return true;
    }
    
    public boolean overlap(int oneLX, int oneLY, int oneRX, int oneRY,
                           int twoLX, int twoLY, int twoRX, int twoRY) {
        if (oneLY > twoRY || twoLY > oneRY) {
            return false;
        }
        
        if (oneLX > twoRX || twoLX > oneRX) {
            return false;
        }
        
        return true;
    }
    
    public boolean isSpaceFree(int shipX, int shipY, int shipW, int shipH, boolean[][] shipGrid) {
        // Variable that will be returned
        boolean spaceFree = false;
        
        // Ship's collision mask boundaries (expanded by one in all directions)
        // It clamps the coordinates to the board's boundaries
        int shipMaskX1 = Math.max(0, shipX - 1);
        int shipMaskY1 = Math.max(0, shipY - 1);
        int shipMaskX2 = Math.min(9, shipX + shipW);
        int shipMaskY2 = Math.min(9, shipY + shipH);
    
        // Is the ship on the board
        if (shipX >= 0 &&
                shipY >= 0 &&
                shipX + shipW - 1 < 10 &&
                shipY + shipH - 1 < 10) {
            spaceFree = true;
        }
    
        // If the ship is within the boundaries,
        // check for collision with other ships
        if (spaceFree) {
            
            // Loop over the entire board
            for (int i = shipMaskX1; i <= shipMaskX2; i++) {
                for (int j = shipMaskY1; j <= shipMaskY2; j++) {
                
                    // Another ship is on our way, return false
                    if (shipGrid[i][j]) {
                        return false;
                    }
                }
            }
            
        }
        
        return spaceFree;
    }
    
    
    
    
    
    
    
    
    @Override
    public void settings() {
    
    }
    
    @Override
    public void init() {
    
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
    
    // Program's entry point
    public static void main(String[] args) {
        // Start our game
        Game.createGame(1280, 720, MAIN, 60f, RENDERER);
    }
    
    /*
    *
    * if(game.input.isButtonDown(PConstants.LEFT)) {
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
    *
    * */
    
    
    
    /*for (int i = 0; i < playerShipData.length; i++) {
                // Check collision
                if (shipOverlap(shipX1,
                        shipY1,
                        shipX2,
                        shipY2,
                        playerShipData[i][0],
                        playerShipData[i][1],
                        playerShipData[i][0] + playerShipData[i][2],
                        playerShipData[i][1] + playerShipData[i][3])) {
                    collision = true;
                    break;
                }
            }*/
}