package com.meietiim.pvlaevadepommitamine;

import static com.meietiim.pvlaevadepommitamine.FrontEnd.*;


/**
 * @author Gregor Suurvarik
 */
public class BackEnd {
    int playerPlaceShipX = MAIN.playerPlaceShipX;
    int playerPlaceShipY = MAIN.playerPlaceShipY;
    int playerPlaceShipH = MAIN.playerPlaceShipH;
    int playerPlaceShipW = MAIN.playerPlaceShipW;
    int playerPlacedBombX = MAIN.playerPlaceBombX;
    int playerPlacedBombY = MAIN.playerPlaceBombY;
    public void update() {
        MAIN.response = 1;
        switch (MAIN.action) {
            case ACTION_PLACE_SHIP: // Player placed a ship
                // Store placed ship in playerShip
                for(int x = playerPlaceShipX; x < playerPlaceShipX+playerPlaceShipW -1; x++) {
                    for(int y = playerPlaceShipY; y < playerPlaceShipY+playerPlaceShipH -1; y++) {
                        if(x > 0 && y > 0 && x < 10 && y < 10) {
                            MAIN.playerShips[x][y] = true; //Adds ships to ship array
                            playerShipData[MAIN.nextShipID] = new int[] {x, y, playerPlaceShipW, playerPlaceShipH, 0, 0};
                        }
                        else MAIN.ERROR_UNKNOWN = 2;
                    }
                }
                break;
            case ACTION_PLACE_BOMB: // Player placed a bomb
                if (MAIN.computerShips[playerPlacedBombX][playerPlacedBombY]) { //Checks if bomb is placed on a ship
                MAIN.response = 2;
                for (int n = 1; n < 5; n++){ //Checks all 5 ships if bomb is placed on that ship
                    if (playerPlacedBombX > MAIN.computerShipData[n, 1] &&
                    playerPlacedBombX <= MAIN.computerShipData[n, 1]+MAIN.playerShipData[n, 3] - 1 &&
                            playerPlacedBombY >= MAIN.computerShipData[n, 2] &&
                    playerPlacedBombY <= MAIN.computerShipData[n, 2] + MAIN.playerShipData[n, 4] - 1 ){
                        MAIN.computerShipData[n, 5]++; //Marks ship to have been bombed one mor time
                        if (Math.max(MAIN.computerShipData[n, 3], Math.max(MAIN.computerShipData[n, 4]) == MAIN.computerShipData[n, 5]){
                            MAIN.computerShipData[n, 6] = 1;
                            MAIN.response = 3;
                        }
                    }
                }
            }
            
            break;
            case ACTION_PLACE_COMPUTER: // Computer's turn
                
                break;
            default:
                MAIN.error = ERROR_UNKNOWN;
        }
    }
}
}