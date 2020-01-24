package com.meietiim.pvlaevadepommitamine;

import static com.meietiim.pvlaevadepommitamine.FrontEnd.*;

public class BackEnd {
    int playerPlaceShipX = MAIN.playerPlaceShipX;
    int playerPlaceShipY = MAIN.playerPlaceShipY;
    int playerPlaceShipH = MAIN.playerPlaceShipH;
    int playerPlaceShipW = MAIN.playerPlaceShipW;
    
    public void update() {
        switch (MAIN.action) {
            case ACTION_PLACE_SHIP: // Player placed a ship
                // Store placed ship in playerShip
                
                // Check if ship inside bounds
                boolean shipInsideBounds = true;
                if(playerPlaceShipX > 0 && playerPlaceShipY > 0 &&
                        playerPlaceShipX+playerPlaceShipW < 10 &&
                        playerPlaceShipY+playerPlaceShipH < 10) {
                    shipInsideBounds = false;
                }
                
                // Ship is inside the bounds, proceed to process the ship
                if(shipInsideBounds) {
                    for (int x = playerPlaceShipX; x < playerPlaceShipX + playerPlaceShipW - 1; x++) {
                        for (int y = playerPlaceShipY; y < playerPlaceShipY + playerPlaceShipH - 1; y++) {
                            if (x > 0 && y > 0 && x < 10 && y < 10)
                                MAIN.playerShips[x][y] = true;
                            // TODO: IDK
                            else MAIN.error = ERROR_UNKNOWN;
                        }
                    }
                } else {
                    MAIN.error = ERROR_INCORRECT_PLACEMENT;
                }
                break;
            case ACTION_PLACE_BOMB: // Player placed a bomb
                //if ()
                    break;
            case ACTION_PLACE_COMPUTER: // Computer's turn

                break;
            default:
                MAIN.error = ERROR_UNKNOWN;
        }
    }
}