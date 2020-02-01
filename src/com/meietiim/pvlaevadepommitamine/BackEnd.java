package com.meietiim.pvlaevadepommitamine;

import java.util.SplittableRandom;

import static com.meietiim.pvlaevadepommitamine.FrontEnd.*;


/**
 * @author Gregor Suurvarik
 */
public class BackEnd {
    // Makes the code shorter because Gregor is lazy
    int playerPlaceShipX = MAIN.playerPlaceShipX;
    int playerPlaceShipY = MAIN.playerPlaceShipY;
    int playerPlaceShipH = MAIN.playerPlaceShipH;
    int playerPlaceShipW = MAIN.playerPlaceShipW;
    int playerPlacedBombX = MAIN.playerPlaceBombX;
    int playerPlacedBombY = MAIN.playerPlaceBombY;

    // AI Data
    private boolean AiShipsPlaced = false;
    private int AiPlacedX = 0;
    private int AiPlacedY = 0;
    private int AiPlacedH = 0;
    private int AiPlacedW = 0;
    private int AiBombX = 0;
    private int AiBombY = 0;
    private boolean guessingOrientation = false;
    private int orientationAttempt = 0;
    // 1 up -1 down 2 right -2 left 0 not in guessing
    private int[][] AiBombData = new int[10][6];
    // AiBombData stores x y orientation length1 and 2, and if the case exists
    private int bombCase = 0;
    public static final int[] SHIPS = new int[]{5, 4, 3, 3, 2};
    // Ship orientation cases
    private final static int SHIP_UP = 1;
    private final static int SHIP_DOWN = -1;
    private final static int SHIP_RIGHT = 2;
    private final static int SHIP_LEFT = -2;

    // Random     <-- The most useful comment in the world
    private SplittableRandom random = new SplittableRandom();

    public void update() {
        MAIN.response = RESPONSE_EMPTY;
        MAIN.error = ERROR_NONE;
        switch (MAIN.action) {
            case ACTION_PLAYER_PLACE_SHIP: // Player placed a ship
                // Store placed ship in playerShip
                
                // Continue if the ship's ID is valid
                if(MAIN.placingShipID == -1) break;
    
                // Check if a ship can fit there
                boolean correctPlacement = MAIN.isSpaceAroundFree(
                        playerPlaceShipX,
                        playerPlaceShipY,
                        playerPlaceShipW,
                        playerPlaceShipH,
                        MAIN.playerShips
                );
    
    
                // Ship is placed correctly, add the changes to board's state
                if (correctPlacement) {
                    // Add the new ship to playerShipData
                    int[] newShip = new int[]{
                            MAIN.playerPlaceShipX, MAIN.playerPlaceShipY,
                            MAIN.playerPlaceShipW, MAIN.playerPlaceShipH,
                            0, 0};
                    MAIN.playerShipData[MAIN.placingShipID] = newShip;
    
                    // Add each piece into playerShips[][]
                    for (int x = playerPlaceShipX; x < playerPlaceShipX + playerPlaceShipW - 1; x++) {
                        for (int y = playerPlaceShipY; y < playerPlaceShipY + playerPlaceShipH - 1; y++) {
                            MAIN.playerShips[x][y] = true;
                        }
                    }
    
                    // Move onto the next ship
                    MAIN.placingShipID++;
                } else {
                    // Ship is placed incorrectly
                    MAIN.error = ERROR_INCORRECT_PLACEMENT;
                }
                break;
            case ACTION_PLAYER_PLACE_BOMB: // Player placed a bomb
                
                // If there isn't already a bomb
                if(!MAIN.playerBombs[playerPlacedBombX][playerPlacedBombY]) {
                    // Marks the bomb on the field
                    MAIN.playerBombs[playerPlacedBombX][playerPlacedBombY] = true;
    
                    // If the bomb hit a ship
                    if (MAIN.computerShips[playerPlacedBombX][playerPlacedBombY]) {
                        
                        // Send a corresponding response
                        MAIN.response = RESPONSE_HIT;
                        
                        // Find the ship's ID
                        int shipID = getShipID(playerPlacedBombX, playerPlacedBombY, MAIN.computerShipData);
                        
                        // Increment hit counter on that ship
                        MAIN.computerShipData[shipID][4]++;
                        
                        // Calculate the ship's length
                        int shipLength = Math.max(
                                MAIN.computerShipData[shipID][2],
                                MAIN.computerShipData[shipID][3]
                        );
                        
                        // If bombs hit == ship's length, the ship has sunk
                        if(MAIN.computerShipData[shipID][4] == shipLength) {
                            // Mark ship as dead
                            MAIN.computerShipData[shipID][5] = 1;
                            
                            // Return a corresponding response
                            MAIN.response = RESPONSE_DEAD;
                        }
                    }
                } else {
                    // There's a bomb already, return an error
                    MAIN.error = ERROR_INCORRECT_PLACEMENT;
                }
    
                break;
            case ACTION_COMPUTER_PLACE_BOMB:
                
                //#################################################   Advanced AI™   ####################################################
    
                // If that case exists
                if (AiBombData[bombCase][5] == 1) {
                    
                    //
                    int x = AiBombData[bombCase][0];
                    int y = AiBombData[bombCase][1];
    
                    // If we don't know the orientation, try to guess it
                    if (guessingOrientation = true) {
                        
                        
                        // If bomb has been placed below
                        if (MAIN.computerBombs[x][y + 1]) {
                            // If there's a ship below
                            if (MAIN.playerShips[x][y + 1]) {
                                
                                // The ship is facing upwards
                                AiBombData[bombCase][2] = SHIP_UP;
                                AiBombData[bombCase][3]++;
    
                                // We know orientation now, exit guessing mode
                                guessingOrientation = false;
                            } else if (MAIN.computerBombs[x][y - 1]) {
                                if (MAIN.playerShips[x][y - 1]) {
                                    // We put the ship upwards because its relevant later
                                    AiBombData[bombCase][2] = SHIP_UP;
                                    AiBombData[bombCase][4]++;
                                    // We know orientation now, exit guessing mode
                                    guessingOrientation = false;
                                }
                            }
                        } else {
                            if (MAIN.computerBombs[x][y + 1] = false) { // when it hasn't asked that point now it will
                                MAIN.computerBombs[x][y + 1] = true; // Makes a move
                                return; // Ends AIs turn
                            } else {
                                if (MAIN.computerBombs[x][y - 1] = false) { // When it hasn't  asked that point it will now
                                    MAIN.computerBombs[x][y - 1] = true; // makes a move
                                    return; // Ends AIs turn
                                } else {
                                    AiBombData[bombCase][2] = SHIP_RIGHT; // When the orientation is not vertical must be horizontal
                                    guessingOrientation = false;
                                }
                            }
                        }
                    } else {
                        
                        // We know the orientation
                        // "where them ships at, drop da' bombs"
                        switch (AiBombData[bombCase][2]) { // Switches to suitable case
                            case SHIP_LEFT:// If ship goes left
                                if (MAIN.computerBombs[x - AiBombData[bombCase][4]][y]) { // Have we bombed that place
                                    if (MAIN.playerShips[x - AiBombData[bombCase][4]][y]) { // If we had a ship
                                        AiBombData[bombCase][4]++; // extends the ship length to AI
                                        // Didn't Respond that because it must have been responded before
                                    } else {
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                    }
                                } else {
                                    MAIN.computerBombs[x + AiBombData[bombCase][4]][y] = true; // Bombs next spot
                                    if (MAIN.playerShips[x + AiBombData[bombCase][4]][y]) {
                                        AiBombData[bombCase][4]++; // Extends ship length to AI
                                        MAIN.response = RESPONSE_HIT; // Responds Hit
                                        // finds the ship and increases the bomb count
                                        MAIN.playerShipData[getShipID(x - AiBombData[bombCase][4], y,
                                                MAIN.playerShipData)][4]++;
                                        // Checks if ship has sunken
                                        if (hasShipSunk(MAIN.computerBombs, MAIN.playerShipData[getShipID(
                                                x - AiBombData[bombCase][4], y, MAIN.playerShipData)])) {
                                            // Marks ship dead
                                            MAIN.playerShipData[getShipID(x - AiBombData[bombCase][4], y,
                                                    MAIN.playerShipData)][5] = 1;
                                            //Responds "Ship is down on the ocean floor"
                                            MAIN.response = RESPONSE_DEAD;
                                            // Following 4 lines of code mark the area around the ship as bombed,
                                            // it comes from the rules that ships cant be side by side and have a common corner
                                            for (int i = AiBombData[bombCase][0] - 1; i < AiBombData[bombCase][0] +
                                                    AiBombData[bombCase][3] + AiBombData[bombCase][4] - 2 + 1; i++) {
                                                for (int j = AiBombData[bombCase][1] - 1; j < AiBombData[bombCase][1] + 1; j++) {
                                                    if (i >= 0 && i <= 9 && j >= 0 && j <= 9) {
                                                        MAIN.computerBombs[i][j] = true;
                                                    }
                                                }
                                            }
                                            bombCase++; // Ends cycle
                                            return; // Ends AIs turn
                                        }
                                    } else { // If there wasn't a ship it should continue to next direction
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                        return; // Ends AIs turn
                                    }
                                }
                            case SHIP_RIGHT: // If ship goes right
                                if (MAIN.computerBombs[x + AiBombData[bombCase][3]][y]) { // Have we bombed that place
                                    if (MAIN.playerShips[x + AiBombData[bombCase][3]][y]) { // If we had a ship
                                        AiBombData[bombCase][3]++; // extends the ship length to AI
                                        // Didn't Respond that because it must have been responded before
                                    } else {
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                    }
                                } else {
                                    MAIN.computerBombs[x + AiBombData[bombCase][3]][y] = true; // Bombs next spot
                                    if (MAIN.playerShips[x + AiBombData[bombCase][3]][y]) {
                                        AiBombData[bombCase][3]++; // Extends ship length to AI
                                        MAIN.response = RESPONSE_HIT; // Responds Hit
                                        // finds the ship and increases the bomb count
                                        MAIN.playerShipData[getShipID(x + AiBombData[bombCase][3], y,
                                                MAIN.playerShipData)][4]++;
                                        // Checks if ship has sunken
                                        if (hasShipSunk(MAIN.computerBombs, MAIN.playerShipData[getShipID(
                                                x + AiBombData[bombCase][4], y, MAIN.playerShipData)])) {
                                            // Marks ship dead
                                            MAIN.playerShipData[getShipID(x + AiBombData[bombCase][3], y,
                                                    MAIN.playerShipData)][5] = 1;
                                            //Responds "Ship is down on the ocean floor"
                                            MAIN.response = RESPONSE_DEAD;
                                            // Following 4 lines of code mark the area around the ship as bombed,
                                            // it comes from the rules that ships cant be side by side and have a common corner
                                            for (int i = AiBombData[bombCase][0] - 1; i < AiBombData[bombCase][0] +
                                                    AiBombData[bombCase][3] + AiBombData[bombCase][4] - 2 + 1; i++) {
                                                for (int j = AiBombData[bombCase][1] - 1; j < AiBombData[bombCase][1] + 1; j++) {
                                                    if (i >= 0 && i <= 9 && j >= 0 && j <= 9) {
                                                        MAIN.computerBombs[i][j] = true;
                                                    }
                                                }
                                            }
                                            bombCase++; // Ends cycle
                                            return; // Ends AIs turn
                                        }
                                    } else { // If there wasn't a ship it should continue to next direction
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                        return; // Ends AIs turn
                                    }
                                }
                            case SHIP_DOWN: // If ship goes Down
                                if (MAIN.computerBombs[x][y - AiBombData[bombCase][4]]) { // Have we bombed that place
                                    if (MAIN.playerShips[x][y - AiBombData[bombCase][4]]) { // If we had a ship
                                        AiBombData[bombCase][4]++; // extends the ship length to AI
                                        // Didn't Respond that because it must have been responded before
                                    } else {
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                    }
                                } else {
                                    MAIN.computerBombs[x][y - AiBombData[bombCase][4]] = true; // Bombs next spot
                                    if (MAIN.playerShips[x][y - AiBombData[bombCase][4]]) {
                                        AiBombData[bombCase][4]++; // Extends ship length to AI
                                        MAIN.response = RESPONSE_HIT; // Responds Hit
                                        // finds the ship and increases the bomb count
                                        MAIN.playerShipData[getShipID(x, y - AiBombData[bombCase][4],
                                                MAIN.playerShipData)][4]++;
                                        // Checks if ship has sunken
                                        if (hasShipSunk(MAIN.computerBombs, MAIN.playerShipData[getShipID(x,
                                                y - AiBombData[bombCase][3], MAIN.playerShipData)])) {
                                            // Marks ship dead
                                            MAIN.playerShipData[getShipID(x, y - AiBombData[bombCase][4],
                                                    MAIN.playerShipData)][5] = 1;
                                            //Responds "Ship is down on the ocean floor"
                                            MAIN.response = RESPONSE_DEAD;
                                            // Following 4 lines of code mark the area around the ship as bombed,
                                            // it comes from the rules that ships cant be side by side and have a common corner
                                            for (int i = AiBombData[bombCase][0] - 1; i < AiBombData[bombCase][0] + 1; i++) {
                                                for (int j = AiBombData[bombCase][1] - 1; j < AiBombData[bombCase][1] +
                                                        AiBombData[bombCase][3] + AiBombData[bombCase][4] - 2 + 1; j++) {
                                                    if (i >= 0 && i <= 9 && j >= 0 && j <= 9) {
                                                        MAIN.computerBombs[i][j] = true;
                                                    }
                                                }
                                            }
                                            bombCase++; // Ends cycle
                                            return; // Ends AIs turn
                                        }
                                    } else { // If there wasn't a ship it should continue to next direction
                                        MAIN.error = ERROR_UNKNOWN;
                                    }
                                }
                            case SHIP_UP: // If ship goes right
                                if (MAIN.computerBombs[x][y + AiBombData[bombCase][3]]) { // Have we bombed that place
                                    if (MAIN.playerShips[x][y + AiBombData[bombCase][3]]) { // If we had a ship
                                        AiBombData[bombCase][3]++; // extends the ship length to AI
                                        // Didn't Respond that because it must have been responded before
                                    } else {
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                    }
                                } else {
                                    MAIN.computerBombs[x][y + AiBombData[bombCase][3]] = true; // Bombs next spot
                                    if (MAIN.playerShips[x][y + AiBombData[bombCase][3]]) {
                                        AiBombData[bombCase][3]++; // Extends ship length to AI
                                        MAIN.response = RESPONSE_HIT; // Responds Hit
                                        // finds the ship and increases the bomb count
                                        MAIN.playerShipData[getShipID(x, y + AiBombData[bombCase][3],
                                                MAIN.playerShipData)][4]++;
                                        // Checks if ship has sunken
                                        if (hasShipSunk(MAIN.computerBombs, MAIN.playerShipData[getShipID(x,
                                                y + AiBombData[bombCase][3], MAIN.playerShipData)])) {
                                            // Marks ship dead
                                            MAIN.playerShipData[getShipID(x, y + AiBombData[bombCase][3],
                                                    MAIN.playerShipData)][5] = 1;
                                            //Responds "Ship is down on the ocean floor"
                                            MAIN.response = RESPONSE_DEAD;
                                            for (int i = AiBombData[bombCase][0] - 1; i < AiBombData[bombCase][0] + 1; i++) {
                                                for (int j = AiBombData[bombCase][1] - 1; j < AiBombData[bombCase][1] +
                                                        AiBombData[bombCase][3] + AiBombData[bombCase][4] - 2 + 1; j++) {
                                                    if (i >= 0 && i <= 9 && j >= 0 && j <= 9) {
                                                        MAIN.computerBombs[i][j] = true;
                                                    }
                                                }
                                            }
                                            bombCase++; // Ends cycle
                                            return; // Ends AIs turn
                                        }
                                    } else { // If there wasn't a ship it should continue to next direction
                                        AiBombData[bombCase][2] = AiBombData[bombCase][2] * -1;
                                        return; // Ends AIs turn
                                    }
                                }
                        }
                    }
                    //###########################################  End of Advanced AI™  ##################################################
                } else {
                    // Bomb's initial location
                    AiBombX = random.nextInt(10);
                    AiBombY = random.nextInt(10);
                    
                    // Search a for free spot, id necessary
                    int attempts = 0;
                    while (MAIN.computerBombs[AiBombX][AiBombY]) {
                        
                        // If there's already bomb, try a different location
                        AiBombX = random.nextInt(10);
                        AiBombY = random.nextInt(10);
                        
                        // Count attempts
                        attempts++;
                        
                        // If all the squares already have a bomb,
                        // there is no free space left
                        if(attempts > 100) {
                            MAIN.error = ERROR_NO_FREE_SPOT;
                            return;
                        }
                    }
                    
                    // Plant a bomb
                    MAIN.computerBombs[AiBombX][AiBombY] = true;

                    // If the bomb hit a player
                    if (MAIN.playerShips[AiBombX][AiBombY]) {

                        // Set the corresponding respond
                        MAIN.response = RESPONSE_HIT;

                        // Store X and Y for next turn
                        AiBombData[bombCase][0] = AiBombX;
                        AiBombData[bombCase][1] = AiBombY;

                        // Go into orientation checking mode
                        guessingOrientation = true;
                        
                        // Find the hit ship's ID
                        int shipID = getShipID(AiBombX, AiBombY, MAIN.playerShipData);
                        
                        // Increment ship's hit counter
                        MAIN.playerShipData[shipID][4]++;
                        
                        // Calculate the ship's length
                        int shipLength = Math.max(
                                MAIN.playerShipData[shipID][2],
                                MAIN.playerShipData[shipID][3]
                        );
                        
                        // If the number of hits is equal to the
                        // ship's length, the ship has sunk
                        if (MAIN.playerShipData[shipID][4] == shipLength) {
                            
                            // Mark the ship dead
                            MAIN.playerShipData[shipID][5] = 1;
                            
                            // Set the corresponding response
                            MAIN.response = RESPONSE_DEAD;
                        }
                    }
                }
                break;
            case ACTION_COMPUTER_PLACE_SHIP:
                
                // Places all required ships in one function call.
                
                int t = 0;
                while (t < MAIN.computerShipData.length) {
                    if (MAIN.computerShipData[t][3] == 0) {
                        // AI places ships
                        // Gets X and Y for that ship to fit inn the area
                        if (random.nextBoolean()) { // If true ship will be placed horizontally
                            // Otherwise ship will be placed vertically
                            AiPlacedX = random.nextInt(10 - SHIPS[t]);
                            AiPlacedY = random.nextInt(10);
                            AiPlacedH = 1;
                            AiPlacedW = SHIPS[t];
                        } else {
                            AiPlacedX = random.nextInt(10);
                            AiPlacedY = random.nextInt(10 - SHIPS[t]);
                            AiPlacedH = SHIPS[t];
                            AiPlacedW = 1;
                        }
                        // Check if ship fits
                        if (MAIN.isSpaceAroundFree(AiPlacedX, AiPlacedY, AiPlacedW, AiPlacedH, MAIN.computerShips)) {
                            for (int x = AiPlacedX; x <= AiPlacedX + AiPlacedW - 1; x++) {
                                for (int y = AiPlacedY; y <= AiPlacedY + AiPlacedH - 1; y++) {
                                    MAIN.computerShips[x][y] = true;
                                }
                            } // Adds ships to computerShipData
                            MAIN.computerShipData[t] = new int[]{AiPlacedX, AiPlacedY,
                                    AiPlacedW, AiPlacedH, 0, 0};
                            t++; // tells to to next ship
                        }
                        //If all ships are placed it marks placing ships done
                        if (t == 5) {
                            AiShipsPlaced = true;
                        }
                    } else { // If ship exists then moves to another ship
                        t++;
                    }
                }
                break;
        }
    }

    /**
     * @return Returns a ship that contains given point. If
     * not found, -1 will be returned.
     */
    public int getShipID(int checkX, int checkY, int[][] shipData) {

        // Iterates thru all the ships
        for (int i = 0; i < shipData.length; i++) {

            // If the given point lies in the boundaries of that ship
            // return the ship's ID.
            if (checkX >= shipData[i][0] &&
                    checkY >= shipData[i][1] &&
                    checkX < shipData[i][0] + shipData[i][2] &&
                    checkY < shipData[i][1] + shipData[i][3]) {
                return i;
            }
        }

        // If none of the ships contained the point, return -1
        return -1;
    }

    /**
     * @return Returns a ship that contains given point. If
     * not found, -1 will be returned.
     */
    public boolean hasShipSunk(boolean[][] bombs, int[] targetShipData) {

        // Bomb counter
        int foundBombs = 0;

        // Iterate thru each point in the ship and count all the bombs in it
        for (int i = targetShipData[0]; i < targetShipData[0] + targetShipData[2]; i++) {
            for (int j = targetShipData[1]; j < targetShipData[1] + targetShipData[3]; j++) {
                if (bombs[i][j]) {
                    foundBombs++;
                }
            }
        }

        // If the number of found bombs on the ship is equal with the
        // length of the ship, the ship has sunk
        return foundBombs == Math.max(targetShipData[2], targetShipData[3]);
    }
}
