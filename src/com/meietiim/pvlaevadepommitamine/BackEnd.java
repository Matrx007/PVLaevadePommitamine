package com.meietiim.pvlaevadepommitamine;

import java.util.Arrays;

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
    
    public void update() {
        MAIN.response = RESPONSE_EMPTY;
        MAIN.error = ERROR_UNKNOWN;
        
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
                // TODO
                break;
        }
    }
}