package nz.ac.vuw.ecs.swen225.gp22.persistency;

import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.domain.Pickup.KEYCOLOR;

/**
 * ArrayMaker class to separate the tile parsing from the XML reading
 */
public class ArrayMaker {

    public ArrayMaker() {

    }

    /**
     * Given a string and dimensions, makes an array of tiles
     * 
     * @param board Board string from XML
     * @param w     Level width from XML
     * @param h     Level height from XML
     * @return
     */
    public static Tile[][] makeArray(String board, int w, int h, String moves) {
        Tile[][] tiles = new Tile[w][h];
        char[] chars = board.toCharArray();
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                char c = chars[(i * h) + j];
                Point loc = new Point(j, i);
                switch (c) {
                    case 'W':
                        tiles[i][j] = Tile.wallTile(loc);
                        break;
                    case 'o':
                        tiles[i][j] = Tile.freeTile(loc);
                        break;
                    case 'b':
                        tiles[i][j] = Tile.keyTile(loc, KEYCOLOR.BLUE);
                        break;
                    case 'r':
                        tiles[i][j] = Tile.keyTile(loc, KEYCOLOR.RED);
                        break;
                    case 'g':
                        tiles[i][j] = Tile.keyTile(loc, KEYCOLOR.GREEN);
                        break;
                    case 'B':
                        tiles[i][j] = Tile.lockedDoorTile(loc, KEYCOLOR.BLUE);
                        break;
                    case 'R':
                        tiles[i][j] = Tile.lockedDoorTile(loc, KEYCOLOR.RED);
                        break;
                    case 'G':
                        tiles[i][j] = Tile.lockedDoorTile(loc, KEYCOLOR.GREEN);
                        break;
                    case 'i':
                        tiles[i][j] = Tile.infoTile(loc);
                        break;
                    case 't':
                        tiles[i][j] = Tile.treasureTile(loc);
                        break;
                    case 'l':
                        tiles[i][j] = Tile.exitLockTile(loc);
                        break;
                    case 'X':
                        tiles[i][j] = Tile.exitTile(loc);
                        break;
                    case 'C':
                        tiles[i][j] = Tile.chapTile(loc);
                        break;
                    case 'M':
                        tiles[i][j] = Tile.monsterTile(loc, moves);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid tile char");
                }
            }
        }
        return tiles;
    }
}
