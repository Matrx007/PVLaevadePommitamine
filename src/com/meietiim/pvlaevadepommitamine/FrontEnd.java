package com.meietiim.pvlaevadepommitamine;

import com.ydgames.mxe.Game;
import com.ydgames.mxe.GameContainer;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Arrays;
import java.util.SplittableRandom;

/**
 * @author Rainis Randmaa
 */

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

    // ### RESOURCES ###
    private ImageManager imageManager;
    
    // ### GAME DATA ###
    private BackEnd backEnd;
    
    // Ships to be placed (by their length)
    public static final int[] SHIPS = new int[]{5, 4, 3, 3, 2};
    
    // Board state
    public boolean[][] playerShips = new boolean[10][10];
    public boolean[][] playerBombs = new boolean[10][10];
    public boolean[][] computerShips = new boolean[10][10];
    public boolean[][] computerBombs = new boolean[10][10];
    
    // row:ID - ship id, column:DATA - 0=x, 1=y, 2=w, 3=h, 4=bombs hit, 5=(1 - dead, 0 - alive)
    public int[][] playerShipData = new int[5][6];
    public int[][] computerShipData = new int[5][6];
    
    // The ID of the next ship
    public int placingShipID = 0;
    
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
    
    // ### GAME LOGIC ###
    
    // --- Placing a ship ---
    private boolean guideShipOrientation; // true - horizontal, false - vertical
    private boolean placingShip; // Show or hide the guide ship
    private int placingShipX, placingShipY;
    private int placingShipW, placingShipH; // The width and height of the guide ship
    
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

        // ### GAME ###
        game = getGame();

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

        imageManager.addToQueue("ships", "/res/ships.png");
    }
    
    @Override
    public void updateTick() {
        
        playerGrid.update();
        computerGrid.update();
        // If mouse inside the grid
        if (game.mouseX >= boardOffsetX && game.mouseY >= boardOffsetY &&
                game.mouseX <= boardOffsetX + 10 * (boardTileW + boardTileGap) &&
                game.mouseY <= boardOffsetY + 10 * (boardTileH + boardTileGap)) {
            
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
        if (game.input.isKeyDown('R')) {
            guideShipOrientation = !guideShipOrientation;
        }
        
        // Show if placing ship there is legal
        collision = true;
        if (placingShipID < 5) {
            
            int shipX = playerGrid.mouseSlotX;
            int shipY = playerGrid.mouseSlotY;
            int shipW = guideShipOrientation ? SHIPS[placingShipID] : 1;
            int shipH = guideShipOrientation ? 1 : SHIPS[placingShipID];
            
            collision = !isSpaceAroundFree(shipX, shipY, shipW, shipH, playerShips);
        }
        
        // Hide the guide ship by default
        placingShip = false;
        
        // Calculating the width and height of the ship
        /*if (placingShipID < 5) {
            // Make the guide ship visible
            placingShip = true;
            
            // Calculate the width and height of the ship
            int dimension = SHIPS[placingShipID];
            if (guideShipOrientation) {
                playerPlaceShipW = dimension;
                playerPlaceShipH = 1;
            } else {
                playerPlaceShipW = 1;
                playerPlaceShipH = dimension;
            }
            
            // Set guide ship parameters
            placingShipW = playerPlaceShipW;
            placingShipH = playerPlaceShipH;
        }*/
        
        /*if (game.input.isButtonDown(PConstants.LEFT)) {
            
            // Only place a ship if we have any ships left
            if (placingShipID < 5) {
                action = 0;
                playerPlaceShipX = mouseSlotX;
                playerPlaceShipY = mouseSlotY;
                
                // Check if the ship is out of bound
                boolean correctPlacement = isSpaceAroundFree(
                        playerPlaceShipX,
                        playerPlaceShipY,
                        playerPlaceShipW,
                        playerPlaceShipH,
                        playerShips
                );
                
                
                if (correctPlacement) {
                    playerShipData[placingShipID] = new int[]{
                            playerPlaceShipX,
                            playerPlaceShipY,
                            playerPlaceShipW,
                            playerPlaceShipH,
                            0,
                            0
                    };
                    placingShipID++;
                    
                    for (int i = playerPlaceShipX; i < playerPlaceShipX + playerPlaceShipW; i++) {
                        for (int j = playerPlaceShipY; j < playerPlaceShipY + playerPlaceShipH; j++) {
                            if (i >= 0 && j >= 0 && i < 10 && j < 10) {
                                playerShips[i][j] = true;
                            }
                        }
                    }
                }
            }
        }*/
    }
    
    @Override
    public void render() {
        // ### RENDERING ###
        
        screenBuffer.beginDraw();
        
        screenBuffer.fill(66, 164, 245);
        screenBuffer.noStroke();
        screenBuffer.rect(0, 0, game.pixelWidth, game.pixelHeight);

/*screenBuffer.noStroke();
for(int i = 0; i < 10; i++) {
for(int j = 0; j < 10; j++) {
float alpha = 32;

alpha = mouseSlotX == i && mouseSlotY == j ? 64 : alpha;
if(computerShips[i][j]) {
alpha = 255;
}

if(nextShipID < 5) {
if (i >= mouseSlotX && j >= mouseSlotY &&
i < mouseSlotX + (guideShipOrientation ? SHIPS[nextShipID] : 1) &&
j < mouseSlotY + (guideShipOrientation ? 1 : SHIPS[nextShipID])) {
alpha = 0;
}
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
boardOffsetX + playerShipData[i][0] * (boardTileW + boardTileGap),
boardOffsetY + playerShipData[i][1] * (boardTileH + boardTileGap),
(boardTileW + boardTileGap) * playerShipData[i][2] - boardTileGap,
(boardTileH + boardTileGap) * playerShipData[i][3] - boardTileGap
);
}*/
        
        playerGrid.render();
        computerGrid.render();
        
        // Ship placing guide
/*if(nextShipID < 5) {
screenBuffer.fill(collision ? 192 : 0, 0, 0, collision ? 192 : 64);
screenBuffer.rect(
64 + mouseSlotX * (boardTileW + boardTileGap),
64 + mouseSlotY * (boardTileH + boardTileGap),
(boardTileW + boardTileGap) * (guideShipOrientation ? guideShipW : 1) - boardTileGap,
(boardTileH + boardTileGap) * (guideShipOrientation ? 1 : guideShipH) - boardTileGap
);
}*/
        
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
                    
                    // The default transparency of a square
                    float alpha = 32;
                    
                    // If this is the square in which the mouse is
                    // in, make it a bit more opaque
                    alpha = mouseSlotX == i && mouseSlotY == j ? 64 : alpha;
                    
                    // If the given array contains tru at [i, j],
                    // make the square fully opaque
                    if (computerShips[i][j]) {
                        alpha = 255;
                    }
                    
                    screenBuffer.fill(0, 0, 0, alpha);
                    screenBuffer.rect(boardOffsetX + (boardTileW + boardTileGap) * i,
                            boardOffsetY + (boardTileH + boardTileGap) * j,
                            boardTileW, boardTileH);
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
    
            /*placingShip = false;
            if (placingShipID < 5) {
                // Make the guide ship visible
                placingShip = true;
        
                // Calculate the width and height of the ship
                int dimension = SHIPS[placingShipID];
                if (guideShipOrientation) {
                    placingShipW = dimension;
                    placingShipH = 1;
                } else {
                    placingShipW = 1;
                    placingShipH = dimension;
                }
            }*/
            
            // If placing a ship, change
            // placingShipX and placeShipY to mouse's location
            placingShip = placingShipID < 5;

            if (placingShip) {
                placingShipX = mouseSlotX;
                placingShipY = mouseSlotY;
                placingShipW = guideShipOrientation ? SHIPS[placingShipID] : 1;
                placingShipH = guideShipOrientation ? 1 : SHIPS[placingShipID];
            }

            // Place a ship when left clicked on the board
            if(placingShip) {
                if(game.input.isButtonDown(PConstants.LEFT)) {
                    
                    // If mouse is inside the boundaries
                    if(mouseSlotX != -1 && mouseSlotY != -1) {
                        
                        // Variable which tells if the boat can be placed there
                        boolean correctPlacement = isSpaceAroundFree(
                                placingShipX,
                                placingShipY,
                                placingShipW,
                                placingShipH,
                                playerShips
                        );
    
    
                        // If it is legal to place the boat there proceed
                        if (correctPlacement) {
                            
                            // Add the ship to the list of ships
                            playerShipData[placingShipID] = new int[]{
                                    placingShipX,
                                    placingShipY,
                                    placingShipW,
                                    placingShipH,
                                    0,
                                    0
                            };

                            // For each point in the ship mark the square as taken
                            for (int i = placingShipX; i < placingShipX + placingShipW; i++) {
                                for (int j = placingShipY; j < placingShipY + placingShipH; j++) {
                                    if (i >= 0 && j >= 0 && i < 10 && j < 10) {
                                        playerShips[i][j] = true;
                                    }
                                }
                            }

                            // Move onto the next ship
                            placingShipID++;
                        }
                    }
                }
            }
        }
        
        public void render() {
            screenBuffer.noStroke();

            System.out.println("De-bug-ing");
            // Draw ships
            for (int i = 0; i < playerShipData.length; i++) {

                // If ship's width is equal to zero, this ship the nor the
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
                    
                    // The default transparency of a square
                    float alpha = 32;
                    
                    // If this is the square in which the mouse is
                    // in, make it a bit more opaque
                    alpha = mouseSlotX == i && mouseSlotY == j ? 64 : alpha;
                    
                    // If the given array contains tru at [i, j],
                    // make the square fully opaque
                    if (playerShips[i][j]) {
                        alpha = 255;
                    }
                    
                    // Don't drawn squares on which the ship's overlay is
                    if (placingShip) {
                        if (i >= placingShipX &&
                                j >= placingShipY &&
                                i < placingShipX + placingShipW &&
                                j < placingShipY + placingShipH) {
                            alpha = 0;
                        }
                    }
                    
                    screenBuffer.fill(0, 0, 0, alpha);
                    screenBuffer.rect(boardOffsetX + (boardTileW + boardTileGap) * i,
                            boardOffsetY + (boardTileH + boardTileGap) * j,
                            boardTileW, boardTileH);
                }
            }
            
            if (placingShip) {
                screenBuffer.fill(collision ? 192 : 0, 0, 0, collision ? 192 : 64);
                screenBuffer.rect(
                        this.boardOffsetX + placingShipX * (this.boardTileW + this.boardTileGap),
                        this.boardOffsetY + placingShipY * (this.boardTileH + this.boardTileGap),
                        (this.boardTileW + this.boardTileGap) * placingShipW - boardTileGap,
                        (this.boardTileH + this.boardTileGap) * placingShipH - boardTileGap
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