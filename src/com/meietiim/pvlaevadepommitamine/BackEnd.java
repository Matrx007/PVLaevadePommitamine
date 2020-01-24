package com.meietiim.pvlaevadepommitamine;

import static com.meietiim.pvlaevadepommitamine.FrontEnd.*;

public class BackEnd {
    int playerPlaceShipX = FrontEnd.MAIN.playerPlaceShipX;
    int playerPlaceShipY = FrontEnd.MAIN.playerPlaceShipY;
    int playerPlaceShipH = FrontEnd.MAIN.playerPlaceShipH;
    int playerPlaceShipW = FrontEnd.MAIN.playerPlaceShipW;
    public void update() {
        switch (FrontEnd.MAIN.action) {
            case ACTION_PLACE_SHIP; // Player placed a ship
                // Store placed ship in playerShip
                for(int x = playerPlaceShipX; x < playerPlaceShipX+playerPlaceShipW -1; x++) {
                    for(int y = playerPlaceShipY; y < playerPlaceShipY+playerPlaceShipH -1; y++) {
                        if(x > 0 && y > 0 && x < 10 && y < 10) playerShips[x][y] = true;
                        else FrontEnd.MAIN.ERROR_UNKNOWN = 2;
                    }
                }
                break;
            case ACTION_PLACE_BOMB; // Player placed a bomb
                if ()
                    break;
            case ACTION_PLACE_COMPUTER; // Computer's turn

                break;
            default:
                FrontEnd.MAIN.error = ERROR_UNKNOWN;
        }
    }
}
}