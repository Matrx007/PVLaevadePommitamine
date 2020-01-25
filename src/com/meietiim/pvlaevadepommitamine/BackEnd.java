package com.meietiim.pvlaevadepommitamine;

import java.util.Arrays;
import java.util.SplittableRandom;

import static com.meietiim.pvlaevadepommitamine.FrontEnd.*;


/**
 * @author Gregor Suurvarik
 */
public class BackEnd {
    // Makes code shorter because Gregor is lazy
    int playerPlaceShipX = MAIN.playerPlaceShipX;
    int playerPlaceShipY = MAIN.playerPlaceShipY;
    int playerPlaceShipH = MAIN.playerPlaceShipH;
    int playerPlaceShipW = MAIN.playerPlaceShipW;
    int playerPlacedBombX = MAIN.playerPlaceBombX;
    int playerPlacedBombY = MAIN.playerPlaceBombY;

    // AI Data storage
    private boolean AiShipsPlaced = false;
    private int AiPlacedX = 0;
    private int AiPlacedY = 0;
    private int AiPlacedH = 0;
    private int AiPlacedW = 0;
    private int AiBombdX = 0;
    private int AiBombdY = 0;
    public static final int[] SHIPS = new int[] {5, 4, 3, 3, 2};

    //Random
    private SplittableRandom random = new SplittableRandom();
    public void update() {
        MAIN.response = RESPONSE_EMPTY;
        MAIN.error = ERROR_NONE;
        switch (MAIN.action) {
            case ACTION_PLACE_SHIP: // Player placed a ship
                // Store placed ship in playerShip
                
                // Check if the ship is placed within the bounds
                boolean correctPlacement = false;
                if(playerPlaceShipX >= 0 && playerPlaceShipY >= 0 &&
                        playerPlaceShipX+playerPlaceShipW-1 < 10 &&
                        playerPlaceShipY+playerPlaceShipH-1 < 10) {
                    correctPlacement = true;
                }
    
                // Ship is placed correctly, add the changes to board's state
                if(correctPlacement) {
                    // Add the new ship to playerShipData
                    MAIN.playerShipData[MAIN.nextShipID] = new int[] {
                            playerPlaceShipX, playerPlaceShipY,
                            playerPlaceShipW, playerPlaceShipH,
                            0, 0};
                    MAIN.nextShipID++;
                    
                    // Add each piece onto playerShips[][]
                    for(int x = playerPlaceShipX; x < playerPlaceShipX+playerPlaceShipW -1; x++) {
                        for(int y = playerPlaceShipY; y < playerPlaceShipY+playerPlaceShipH -1; y++) {
                            MAIN.playerShips[x][y] = true;
                        }
                    }
                } else {
                    // Ship is placed incorrectly
                    MAIN.error = ERROR_INCORRECT_PLACEMENT;
                }
                
                break;
            case ACTION_PLACE_BOMB: // Player placed a bomb
    
                // Checks if a bomb is placed on a ship
                if (MAIN.computerShips[playerPlacedBombX][playerPlacedBombY]) {
                    MAIN.response = RESPONSE_HIT;
    
                    // Checks if the bomb hit a ship
                    for (int n = 0; n < MAIN.computerShipData.length; n++) {
                        
                        // Check collision
                        if (playerPlacedBombX > MAIN.computerShipData[n][0] &&
                                playerPlacedBombX <= MAIN.computerShipData[n][0] + MAIN.playerShipData[n][2] - 1 &&
                                playerPlacedBombY >= MAIN.computerShipData[n][1] &&
                                playerPlacedBombY <= MAIN.computerShipData[n][1] + MAIN.playerShipData[n][3] - 1) {
    
                            // Increase the ship's bomb counter
                            MAIN.computerShipData[n][4]++;
                            
                            // If the bomb amount equals the ship's length [= max(w, h)],
                            // then the ship has sunk
                            if (Math.max(MAIN.computerShipData[n][2], MAIN.computerShipData[n][3]) == MAIN.computerShipData[n][4]) {
                                // Ship is dead
                                MAIN.computerShipData[n][5] = 1;
                                // Notifies FrontEnd that a ship has sunken
                                MAIN.response = RESPONSE_DEAD;
                            }
                        }
                    }
                }
            
                break;
            case ACTION_PLACE_COMPUTER: // Computer's turn
                if (AiShipsPlaced){ // If true then ships are placed otherwise place ships
                    // TODO Pommitamine

                }
                else {
                    int i = 0;
                    while (i < MAIN.computerShipData.length) {
                        if (MAIN.computerShipData[i][3] == 0){
                            // AI places ships
                            // Gets X and Y for that ship to fit inn the area
                            if (random.nextBoolean()) { // If true ship will be placed horizontally
                                                        // Otherwise ship will be placed vertically
                                AiPlacedX = random.nextInt(10 - SHIPS[i]);
                                AiPlacedY = random.nextInt(10);
                                AiPlacedH = 1;
                                AiPlacedW = SHIPS[i];
                            }
                            else {
                                AiPlacedX = random.nextInt(10);
                                AiPlacedY = random.nextInt(10 - SHIPS[i]);
                                AiPlacedH = SHIPS[i];
                                AiPlacedW = 1;
                            }
                            // Check if ship fits
                            if (MAIN.isSpaceFree(AiPlacedX, AiPlacedY, AiPlacedW, AiPlacedH, MAIN.computerShips));{
                                for(int x = AiPlacedX; x < AiPlacedX+AiPlacedW -1; x++) {
                                    for(int y = AiPlacedY; y < AiPlacedY+AiPlacedH -1; y++) {
                                        MAIN.computerShips[x][y] = true;
                                    }
                                } // Adds ships to computerShipData
                                MAIN.computerShipData[i] = new int[] {AiPlacedX, AiPlacedY,
                                        AiPlacedW, AiPlacedH, 0, 0};
                                i++; // tells to to next ship
                            }
                            //If all ships are placed it marks placing ships done
                            if (i == 5) {AiShipsPlaced = true;}
                        }
                        else { // If ship exists then moves to another ship
                            i++;
                        }
                    }
                }
                break;
        }
    }
}