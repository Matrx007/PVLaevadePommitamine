package com.meietiim.pvlaevadepommitamine;

import com.meietiim.pvlaevadepommitamine.gui.GUIComponent;
import com.meietiim.pvlaevadepommitamine.gui.GUIEngine;
import com.meietiim.pvlaevadepommitamine.gui.GUIScene;
import com.meietiim.pvlaevadepommitamine.gui.components.Button;
import com.meietiim.pvlaevadepommitamine.gui.constraints.ConstraintBuilder;
import com.meietiim.pvlaevadepommitamine.gui.constraints.FixedConstraint;
import com.meietiim.pvlaevadepommitamine.gui.constraints.LinkedConstraint;
import com.meietiim.pvlaevadepommitamine.gui.constraints.SubtractConstraint;
import com.ydgames.mxe.Game;
import com.ydgames.mxe.GameContainer;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.SplittableRandom;

/**
 * @author Rainis Randmaa
 */

public class FrontEnd extends GameContainer {
    public static final FrontEnd MAIN = new FrontEnd();
    
    // ### ENGINE ###
    public Game game;
    
    // ### GUI ###
    public GUIEngine guiEngine;
    
    // ### RENDERING ###
    public static final String RENDERER = PConstants.P2D;
    
    private PGraphics screenBuffer;
    private int boardTileW = 48;
    private int boardTileH = 48;
    private int boardTileGap = 8;
    private float boardOffsetX;  // Calculated automatically
    private float boardOffsetY; // Calculated automatically
    
    // ### RESOURCES ###
    private ImageManager imageManager;
    
    // ### GAME LOGIC ###
    private BackEnd backEnd;
    
    // Ships to be placed (by their length)
    public static final int[] SHIPS = new int[]{5, 4, 3, 3, 2};
    public int mouseSlotX, mouseSlotY;
    
    // ### Board state ###
    public boolean[][] playerShips = new boolean[10][10];
    public boolean[][] playerBombs = new boolean[10][10];
    public boolean[][] computerShips = new boolean[10][10];
    public boolean[][] computerBombs = new boolean[10][10];
    
    // row:ID - ship id, column:DATA - 0=x, 1=y, 2=w, 3=h, 4=bombs hit, 5=(1 - dead, 0 - alive)
    public int[][] playerShipData = new int[5][6];
    public int[][] computerShipData = new int[5][6];
    
    // ### Placing a ship ###
    
    // The ID of the next ship
    public int placingShipID = 0;
    
    // Boundaries of the guide ship
    public int playerPlaceShipX, playerPlaceShipY, playerPlaceShipW, playerPlaceShipH;
    
    private boolean guideShipOrientation; // true - horizontal, false - vertical
    private boolean placingShip; // Show or hide the guide ship
    
    // Placing a bomb
    public int playerPlaceBombX, playerPlaceBombY;
    
    // ### Game states ###
    
    public int gameState = STATE_PLAYER_PLACING_SHIPS;
    
    public static final int STATE_PLAYER_PLACING_SHIPS = 1;
    public static final int STATE_COMPUTER_PLACING_SHIPS = 2;
    public static final int STATE_PLAYER_PLACING_BOMBS = 3;
    public static final int STATE_COMPUTER_PLACING_BOMBS = 4;
    
    // ### BackEnd Input ###
    
    private int placingShipX, placingShipY;
    private int placingShipW, placingShipH; // The width and height of the guide ship
    
    public static final int ACTION_UNKNOWN = 0;
    public final static int ACTION_PLAYER_PLACE_SHIP = 1;
    public final static int ACTION_PLAYER_PLACE_BOMB = 2;
    public final static int ACTION_COMPUTER_PLACE_SHIP = 3;
    public final static int ACTION_COMPUTER_PLACE_BOMB = 4;
    
    public int action; // Use ACTION_... constants
    
    // ### BackEnd Feedback ###
    
    public int computerPlaceBombX, computerPlaceBombY;
    
    public final static int ERROR_NONE = 1;
    public final static int ERROR_UNKNOWN = 2;
    public final static int ERROR_INCORRECT_PLACEMENT = 3;
    public final static int ERROR_NO_FREE_SPOT = 4;
    
    public int error; // Use ERROR_... constants
    
    public final static int RESPONSE_EMPTY = 1;
    public final static int RESPONSE_HIT = 2;
    public final static int RESPONSE_DEAD = 3;
    public final static int RESPONSE_NEXT = 4;
    public final static int RESPONSE_FINISHED = 5;
    
    public int response; // Use RESPONSE_... constants
    
    // ### A CREATIVE NAME ###
    public SplittableRandom random;
    
    // ### DEBUGGING ###
    private boolean collision;
    
    // ### GAME ###
    private Grid playerGrid, computerGrid;
    
    // Make this class a singleton
    private FrontEnd() {
    
    }
    
    @Override
    public void setup() {

        // ### ENGINE ###
        game = getGame();
        
        game.getSurface().setResizable(true);
        
        // ### GUI ###
        guiEngine = new GUIEngine();
        guiEngine.setup(game);
        
        // Test scenes
        GUIScene testScene = new GUIScene();
    
        // Test objects
        GUIComponent testButton = new Button(
                new ConstraintBuilder(0).add(() -> game.pixelWidth).sub(8+256).get(),
                new ConstraintBuilder(0).add(() -> game.pixelHeight).sub(8+64).get(),
                new FixedConstraint(256),
                new FixedConstraint(64),
                () -> System.out.println("Hello world!"),
                "YEs",
                guiEngine
        );
        
        // Add objects onto the screen
        testScene.add(testButton);
        
        // Add scenes to the GUI engine
        guiEngine.add(testScene, "test");
        
        // Make a scene active
        guiEngine.show("test");

        // ### GAME LOGIC ¤¤¤
        backEnd = new BackEnd();

        random = new SplittableRandom();

        // ### RENDERING ####
        screenBuffer = game.createGraphics(game.pixelWidth, game.pixelHeight, RENDERER);
        
        boardOffsetX = (game.pixelWidth - boardTileW * 10 - boardTileGap * 9) / 2f;
        boardOffsetY = (game.pixelHeight - boardTileH * 10 - boardTileGap * 9) / 2f;
        
        // ### UI ###
        playerGrid = new PlayerGrid(
                64, 64,
                48, 48, 8, playerShips
        );
        computerGrid = new Grid(
                64 + 64 + (48 + 8) * 10, 64,
                48, 48, 8, playerBombs
        );

        // ### RESOURCES ###
        imageManager = new ImageManager(game);
    
        // Load images & icons
        imageManager.addToQueue("attacked_EmptySquare", "/res/attacked_EmptySquare.png");
        imageManager.addToQueue("attacked_HitSquare", "/res/attacked_HitSquare.png");
        
        // Load ship sprite sheet
        PImage shipImageSheet = imageManager.loadImmediately(
                "ships", "/res/ships.png");
        
        // Extract individual sprites from sprite sheet
        imageManager.addImage("ship2",
                shipImageSheet.get(64, 61, 48, 109));
        imageManager.addImage("ship2sunk",
                
                shipImageSheet.get(176, 61, 48, 109));
        
        imageManager.addImage("ship3",
                shipImageSheet.get(54, 224, 68, 176));
        imageManager.addImage("ship3sunk",
                shipImageSheet.get(166, 224, 68, 176));
        
        imageManager.addImage("ship4",
                shipImageSheet.get(64, 448, 48, 232));
        imageManager.addImage("ship4sunk",
                shipImageSheet.get(176, 448, 48, 232));
        
        imageManager.addImage("ship5",
                shipImageSheet.get(275, 51, 74, 298));
        imageManager.addImage("ship5sunk",
                shipImageSheet.get(338, 51, 74, 298));
    }
    
    @Override
    public void updateTick() {
        
        playerGrid.update();
        computerGrid.update();
        
        // Rotate ship
        if (game.input.isKeyDown('R')) {
            guideShipOrientation = !guideShipOrientation;
        }
        
        placingShip = placingShipID < 5;
        
        // Show if placing ship there is legal
        collision = true;
        if (placingShipID < 5) {
            
            int shipX = playerGrid.mouseSlotX;
            int shipY = playerGrid.mouseSlotY;
            int shipW = guideShipOrientation ? SHIPS[placingShipID] : 1;
            int shipH = guideShipOrientation ? 1 : SHIPS[placingShipID];
            
            collision = !isSpaceAroundFree(shipX, shipY, shipW, shipH, playerShips);
        }
    
        
        // ### GAME LOGIC ###
        
        action = ACTION_UNKNOWN;
        error = ERROR_NONE;
        response = RESPONSE_EMPTY;
        
        switch (gameState) {
            case STATE_PLAYER_PLACING_SHIPS:
                placingShip = true;
                
                // If left clicked place a ship
                if(game.input.isButtonDown(PConstants.LEFT)) {
                    // Return if the ship can't be placed there
                    if(collision) break;
                    
                    // Forward corresponding action
                    action = ACTION_PLAYER_PLACE_SHIP;
    
                    // Forward the ship's size and location
                    playerPlaceShipX = placingShipX;
                    playerPlaceShipY = placingShipY;
                    playerPlaceShipW = placingShipW;
                    playerPlaceShipH = placingShipH;
    
                    // Update BackEnd
                    backEnd.update();
    
                    // Read returned errors if there are any
                    if(error == ERROR_INCORRECT_PLACEMENT) {
                        // TODO: Show error
                        System.out.println("Incorrect placement");
                        
                        // Ship incorrectly placed, try again
                        gameState = STATE_PLAYER_PLACING_SHIPS;
                        break;
                    }
                    
                    // Count already placed ships
                    int placedShips = 0;
                    for(int i = 0; i < playerShipData.length; i++) {
                        if(playerShipData[i][2] != 0 && playerShipData[i][3] != 0) {
                            placedShips++;
                        }
                    }
    
                    // If all the ships are placed, it's computer's turn
                    if(placedShips == playerShipData.length) {
                        gameState = STATE_COMPUTER_PLACING_SHIPS;
                    } else {
                        // There are more ships to be placed,
                        // give the player one more turn
                        gameState = STATE_PLAYER_PLACING_SHIPS;
                    }
                }
                break;
            case STATE_COMPUTER_PLACING_SHIPS:
                
                // TODO: Repeat until all ships are placed
                
                for(int i = 0; i < 5; i++) {
                    // Forward the corresponding action
                    action = ACTION_COMPUTER_PLACE_SHIP;
    
                    // No ore information needed, just call the BackEnd
                    backEnd.update();
                }
                
                gameState = STATE_PLAYER_PLACING_BOMBS;
    
                /*// Read the response
                if (response == RESPONSE_FINISHED) {
                    // If all ships have been placed, it's player's turn
                    gameState = STATE_PLAYER_PLACING_BOMBS;
                } else if (response == RESPONSE_NEXT) {
                    // If there are more ships to be placed,
                    // it's again computer's turn
                    gameState = STATE_COMPUTER_PLACING_SHIPS;
                }*/
                
                break;
                
            case STATE_PLAYER_PLACING_BOMBS:
    
                // Forward the corresponding action
                action = ACTION_PLAYER_PLACE_BOMB;
                
                // Place a bomb where the mouse is, but only if
                // the mouse is inside the board
                if(game.input.isButtonDown(PConstants.LEFT)) {
                    if (computerGrid.mouseSlotX != -1 &&
                            computerGrid.mouseSlotY != -1) {
                        playerPlaceBombX = computerGrid.mouseSlotX;
                        playerPlaceBombY = computerGrid.mouseSlotY;
        
                        // And finally call the BackEnd
                        backEnd.update();
                        
                        // Read the errors
                        if(error == ERROR_INCORRECT_PLACEMENT) {
                            gameState = STATE_PLAYER_PLACING_BOMBS;
                            break;
                        }
        
                        // Read the response
                        if (response == RESPONSE_HIT) {
                            // If player hit a ship, give the player a new turn 
                            gameState = STATE_PLAYER_PLACING_BOMBS;
                        } else if (response == RESPONSE_EMPTY) {
                            // If no response, it's computer's turn
                            gameState = STATE_COMPUTER_PLACING_BOMBS;
                        }
        
                        // TODO: Show error
                    }
                }
                
                break;
            
            case STATE_COMPUTER_PLACING_BOMBS:
                
                // Forward the corresponding action
                action = ACTION_COMPUTER_PLACE_BOMB;
                
                // Let the BackEnd do it's magic
                backEnd.update();
                
                // Read the response
                if(response == RESPONSE_HIT) {
                    // If hit, give the computer a new turn
                    gameState = STATE_COMPUTER_PLACING_BOMBS;
                } else if(response == RESPONSE_EMPTY) {
                    // If no response, it's player's turn
                    gameState = STATE_PLAYER_PLACING_BOMBS;
                }
                
                break;
        }
        
        // ### GUI ###
        guiEngine.update();
    }
    
    @Override
    public void render() {
        // ### RENDERING ###
        
        screenBuffer.beginDraw();
        
        // Clear background
        screenBuffer.fill(66, 164, 245);
        screenBuffer.noStroke();
        screenBuffer.rect(0, 0, game.pixelWidth, game.pixelHeight);
        
        // ### GAME ###
        
        playerGrid.render();
        computerGrid.render();
    
        // ### GUI ###
        guiEngine.render();
    
        screenBuffer.image(guiEngine.drawingSurface, 0, 0);
        
        // Flush
        screenBuffer.endDraw();
        
        // Draw screenBuffer onto the screen
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
    
    public boolean isSpaceAroundFree(int shipX, int shipY, int shipW, int shipH, boolean[][] shipGrid) {
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
    
    public void fillNeighbouring(int shipX, int shipY, int shipW, int shipH, boolean value, boolean[][] array) {
        // Ship's collision mask boundaries (expanded by one in all directions)
        // It clamps the coordinates to the board's boundaries
        int shipMaskX1 = Math.max(0, shipX - 1);
        int shipMaskY1 = Math.max(0, shipY - 1);
        int shipMaskX2 = Math.min(9, shipX + shipW);
        int shipMaskY2 = Math.min(9, shipY + shipH);
        
        // Loop thru the region and fill it with 'value'
        for (int i = shipMaskX1; i <= shipMaskX2; i++) {
            for (int j = shipMaskY1; j <= shipMaskY2; j++) {
                array[i][j] = value;
            }
        }
    }
    
    public void fillArea(int areaX, int areaY, int areaW, int areaH, boolean value, boolean[][] array) {
        // Clamp the coordinates to the board's boundaries
        int areaMaskX1 = Math.max(0, areaX);
        int areaMaskY1 = Math.max(0, areaY);
        int areaMaskX2 = Math.min(9, areaX + areaW - 1);
        int areaMaskY2 = Math.min(9, areaY + areaH - 1);
        
        // Loop thru the region and fill it with 'value'
        for (int i = areaMaskX1; i <= areaMaskX2; i++) {
            for (int j = areaMaskY1; j <= areaMaskY2; j++) {
                array[i][j] = value;
            }
        }
    }
    
    /**
     * @param searchX  The location to search
     * @param searchY  The location to search.
     * @param shipData The array to search for the ship
     * @return Return's the index of found the found ship
     */
    public int shipAt(int searchX, int searchY, int[][] shipData) {
        
        // Is the target point within the board
        if (searchX >= 0 &&
                searchY >= 0 &&
                searchX < 10 &&
                searchY < 10) {
            return -1;
        }
        
        // Search for the ship
        for (int i = 0; i < shipData.length; i++) {
            // Another ship is on our way, return false
            if (searchX >= shipData[i][0] &&
                    searchY >= shipData[i][1] &&
                    searchX < shipData[i][0] + shipData[i][2] - 1 &&
                    searchY < shipData[i][1] + shipData[i][3] - 1) {
                return i;
            }
        }
        
        return -1;
    }
    
    
    private class Grid {
        
        // Board properties
        protected int boardTileW = 48;
        protected int boardTileH = 48;
        protected int boardTileGap = 8;
        protected float boardOffsetX;
        protected float boardOffsetY;
        
        // The mouse slot in which the mouse is in
        public int mouseSlotX, mouseSlotY;
        
        public Grid(float boardOffsetX, float boardOffsetY,
                    int boardTileW, int boardTileH,
                    int boardTileGap, boolean[][] array) {
            this.boardTileW = boardTileW;
            this.boardTileH = boardTileH;
            this.boardOffsetX = boardOffsetX;
            this.boardOffsetY = boardOffsetY;
            this.boardTileGap = boardTileGap;
        }
        
        public void update() {
            // If mouse inside the grid
            if (game.mouseX >= boardOffsetX && game.mouseY >= boardOffsetY &&
                    game.mouseX <= boardOffsetX + 10 * (boardTileW + boardTileGap) &&
                    game.mouseY <= boardOffsetY + 10 * (boardTileH + boardTileGap)) {
                
                // Find the slot in which the mouse is in,
                // but only if the mouse is not in a gap
                if ((int) ((game.mouseX - boardOffsetX) /
                        (boardTileW + boardTileGap)) <= boardTileW &&
                        (int) ((game.mouseY - boardOffsetY) /
                                (boardTileH + boardTileGap)) <= boardTileH) {
                    
                    // Find a slot the mouse is in
                    mouseSlotX = (int) ((game.mouseX - boardOffsetX) /
                            (boardTileW + boardTileGap));
                    mouseSlotY = (int) ((game.mouseY - boardOffsetY) /
                            (boardTileH + boardTileGap));
                }
            } else {
                // If the mouse is not inside the grid
                mouseSlotX = -1;
                mouseSlotY = -1;
            }
        }
        
        public void render() {
            
            // Draw the grid
            screenBuffer.noStroke();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    
                    // If there's a bomb in this square, draw it
                    if (playerBombs[i][j]) {
                        screenBuffer.image(imageManager.getImage("attackedSquare"),
                                boardOffsetX + (boardTileW + boardTileGap) * i,
                                boardOffsetY + (boardTileH + boardTileGap) * j,
                                boardTileW,
                                boardTileH);
                    }
    
                    // If the square is empty, draw it
                    // The square is darker when the mouse is on top of it
                    screenBuffer.fill(0, 0, 0,
                            mouseSlotX == i && mouseSlotY == j ? 64 : 32);
                    screenBuffer.rect(
                            boardOffsetX + (boardTileW + boardTileGap) * i,
                            boardOffsetY + (boardTileH + boardTileGap) * j,
                            boardTileW,
                            boardTileH);
                }
            }
        }
    }
    
    private class PlayerGrid extends Grid {
        
        public PlayerGrid(float boardOffsetX, float boardOffsetY,
                          int boardTileW, int boardTileH,
                          int boardTileGap, boolean[][] array) {
            super(boardOffsetX, boardOffsetY,
                    boardTileW, boardTileH, boardTileGap, array);
        }
        
        public void update() {
            super.update();
            
            // If placing a ship, change
            // placingShipX and placeShipY to mouse's location
            placingShip = placingShipID < 5;

            if (placingShip) {
                placingShipX = mouseSlotX;
                placingShipY = mouseSlotY;
                placingShipW = guideShipOrientation ? SHIPS[placingShipID] : 1;
                placingShipH = guideShipOrientation ? 1 : SHIPS[placingShipID];
            }
        }
        
        public void render() {
            screenBuffer.noStroke();
            
            // Draw ships
            for (int i = 0; i < playerShipData.length; i++) {

                // If ship's width is equal to zero, this ship nor the
                // next ones exist so we will exit the loop
                if (playerShipData[i][2] == 0 && playerShipData[i][3] == 0) {
                    break;
                }
                
                // This ship exists, so let's draw it
                screenBuffer.fill(0, 0, 0, 255);
                screenBuffer.rect(
                        this.boardOffsetX + playerShipData[i][0] * (this.boardTileW + this.boardTileGap),
                        this.boardOffsetY + playerShipData[i][1] * (this.boardTileH + this.boardTileGap),
                        (this.boardTileW + this.boardTileGap) * playerShipData[i][2] - boardTileGap,
                        (this.boardTileH + this.boardTileGap) * playerShipData[i][3] - boardTileGap
                );
            }
    
            // Draw the grid
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    
                    // If there's a bomb in this square, draw it
                    if (computerBombs[i][j]) {
                        String image = playerShips[i][j] ? "attackedSquareInverse" : "attackedSquare";
                        screenBuffer.image(imageManager.getImage(image),
                                boardOffsetX + (boardTileW + boardTileGap) * i,
                                boardOffsetY + (boardTileH + boardTileGap) * j,
                                boardTileW,
                                boardTileH);
                    }
                    
                    // Don't drawn squares the guide ship is covering
                    if (placingShip) {
                        if (i >= placingShipX &&
                                j >= placingShipY &&
                                i < placingShipX + placingShipW &&
                                j < placingShipY + placingShipH) {
                            continue;
                        }
                    }
                    
                    // If the square is empty, draw it
                    // The square is darker when the mouse is on top of it
                    screenBuffer.fill(0, 0, 0,
                            mouseSlotX == i && mouseSlotY == j ? 64 : 32);
                    screenBuffer.rect(
                            boardOffsetX + (boardTileW + boardTileGap) * i,
                            boardOffsetY + (boardTileH + boardTileGap) * j,
                            boardTileW,
                            boardTileH);
                }
            }
            
            if (placingShip) {
                // Don't let the guide appear outside of the grid
                int guideWidth = Math.min(10, placingShipX+placingShipW)-placingShipX;
                int guideHeight = Math.min(10, placingShipY+placingShipH)-placingShipY;
                
                // Draw the guide
                screenBuffer.fill(collision ? 192 : 0, 0, 0, collision ? 192 : 64);
                screenBuffer.rect(
                        this.boardOffsetX + placingShipX * (this.boardTileW + this.boardTileGap),
                        this.boardOffsetY + placingShipY * (this.boardTileH + this.boardTileGap),
                        (this.boardTileW + this.boardTileGap) * guideWidth - boardTileGap,
                        (this.boardTileH + this.boardTileGap) * guideHeight - boardTileGap
                );
            }
        }
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
        if (deltaTime >= 1f / 60f) {
            deltaTime -= 1f / 60f;
            updateTick();
        }
    }
    
    // Program's entry point
    public static void main(String[] args) {
        // Start our game
        Game.createGame(1280, 720, MAIN, 60f, RENDERER);
    }
}