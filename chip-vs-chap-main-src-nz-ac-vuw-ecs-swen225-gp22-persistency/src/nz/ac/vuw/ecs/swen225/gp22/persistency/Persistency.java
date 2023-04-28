package nz.ac.vuw.ecs.swen225.gp22.persistency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.jdom2.*;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.domain.Pickup.KEYCOLOR;

/**
 * Main class for the persistency package
 */
public class Persistency {

    /**
     * Method for getting tiles of level1 without parameters
     */
    public static Tile[][] level1() {
        return readXML("level1");
    }
    /**
     * Method for getting tiles of level2 without parameters
     */
    public static Tile[][] level2() {
        return readXML("level2");
    }

    /**
     * Method for getting tiles of saved level without parameters
     */
    public static Tile[][] levelSave() {
        return readXML("levelPers");
    }

    /**
     * Reads an XML path and returns a 2D array of tiles for level creation
     * Uses an array maker object to convert the board string from XML to tiles
     * 
     * @param level Level string to be converted to array
     * @return Array of tiles
     * @throws ArithmeticException
     */
    public static Tile[][] readXML(String level) {
        int wid = 0;
        int hei = 0;
        String board = "";
        String moves = "";
        String levelPath;
        try {
            if (level.contains(".")) {
                levelPath = level;
            } else {
                levelPath = "levels/" + level + ".xml";
            }
            File inputFile = new File(levelPath);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren();
            for (int i = 0; i < elements.size(); i++) {
                Element curr = elements.get(i);
                switch (curr.getName()) {
                    case "width":
                        wid = Integer.parseInt(curr.getText());
                        break;
                    case "height":
                        hei = Integer.parseInt(curr.getText());
                        break;
                    case "board":
                        board = curr.getText();
                        break;
                    case "desc":
                        break;
                    case "id":
                        break;
                    case "time":
                        break;
                    case "tres":
                        break;
                    case "moves":
                        moves = curr.getText();
                        break;
                    case "items":
                        break;
                    default:
                        throw new IllegalArgumentException("malformed xml, unexpected element: " + curr.getText());
                }
            }

            if (board.length() != wid * hei) {
                throw new JDOMException("Malformed xml, board size does not match width*height");

            } else {
               // System.out.println("check-passed\n");
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        // returns board string converted to 2D array
        return ArrayMaker.makeArray(board, wid, hei, moves);
    }

    /**
     * Creates a save file when no inventory is available
     * 
     * @param tiles current tiles of game
     */
    public static void createPXML(Tile[][] tiles, Pickup.Key[] inv) {
        createPXML(tiles, inv, 180);
    }

    /**
     * Creates an XML file of current state of game, acts as saving feature
     * 
     * @param tiles current tiles of game
     * @param inv inventory of current game
     */
    public static void createPXML(Tile[][] tiles, Pickup.Key[] inv, int time) {
        String board = strFromArray(tiles);
        String moves = getCurrentMoves(tiles);
        try {
            // root element
            Element levelElement = new Element("level");
            Document doc = new Document(levelElement);
            // width element
            Element widthElement = new Element("width").setText(String.valueOf(tiles.length));
            // height element
            Element heightElement = new Element("height").setText(String.valueOf(tiles[0].length));
            // board element
            Element boardElement = new Element("board").setText(board);
            // time element
            Element timeElement = new Element("time").setText(String.valueOf(time));
            // description element
            Element descElement = new Element("desc").setText("saved progress");
            // moves element
            Element movesElement = new Element("moves").setText(moves);
            // id element
            Element idElement = new Element("id").setText("levelSave");
            // inventory element
            String invString = "";
            for (Pickup.Key k : inv) {
                if (k != null) {
                    invString += k.toChar();
                    System.out.println(k.toChar());
                }
            }
            Element invElement = new Element("items").setText(invString);
            // add elems
            doc.getRootElement().addContent(widthElement);
            doc.getRootElement().addContent(heightElement);
            doc.getRootElement().addContent(boardElement);
            doc.getRootElement().addContent(descElement);
            doc.getRootElement().addContent(movesElement);
            doc.getRootElement().addContent(timeElement);
            doc.getRootElement().addContent(idElement);
            doc.getRootElement().addContent(invElement);
            XMLOutputter xmlOutput = new XMLOutputter();
            // setup printstream
            PrintStream writeLevel = new PrintStream(new FileOutputStream("levels/levelPers.xml", false));
            // write xml
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, writeLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a 2D array of tiles to a string for use in creating XML
     * 
     * @param tiles 2D array of tiles
     */
    public static String strFromArray(Tile[][] tiles) {
        char[] boardChars = new char[tiles.length * tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                Tile t = tiles[i][j];
                boardChars[(i * tiles[0].length) + j] = t.getChar();
            }
        }
        return String.valueOf(boardChars);
    }

    /**
     * Returns the moves (if present) of the monsters in the current tile set
     * 
     * @param tiles tile set
     */
    public static String getCurrentMoves(Tile[][] tiles){
        for(int i = 0; i < tiles.length ; i++){
            for(int j = 0; j < tiles[0].length; j++){
                Tile t = tiles[i][j];
                if(t.getChar()=='M'){
                    Monster m = (Monster) t.getEntity();
                    return m.movesToString();
                }
            }

        }
        return "";
    }

    /**
     * gets inventory of chap from the current saved level
     * 
     * @param level level to get inventory from
     */
    public static Pickup.Key[] getSavedInventory(){
        Pickup.Key[] keys = new Pickup.Key[8];
        try {
            File inputFile = new File("levels/levelPers.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren();
            for (int i = 0; i < elements.size(); i++) {
               Element curr = elements.get(i);
                //System.out.println(curr.getName()); //debug
                if(curr.getName().equals("items")){
                    //on to right XML element
                    String key = curr.getText();
                    for (int j = 0; j < key.toCharArray().length; j++) {
                        char c = key.charAt(j);
                        switch (c) {
                            case 'b':
                                keys[j] = (Pickup.Key) Tile.keyTile(new Point(0, 0), KEYCOLOR.BLUE).getEntity();
                                break;
                            case 'r':
                                keys[j] = (Pickup.Key) Tile.keyTile(new Point(0, 0), KEYCOLOR.RED).getEntity();
                                break;
                            case 'g':
                                keys[j] = (Pickup.Key) Tile.keyTile(new Point(0, 0), KEYCOLOR.GREEN).getEntity();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return keys;
    }

    /**
     * Returns the number of treasures remaining  in a level
     * 
     * @param level 
     */
    public static int getNumChips(String level){
        try {
            File inputFile = new File("levels/" + level + ".xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren();
            for (int i = 0; i < elements.size(); i++) {
               Element curr = elements.get(i);
                if(curr.getName().equals("board")){
                    //on to right XML element
                    String bs = curr.getText();
                    return (int) bs.chars().mapToObj(c -> (char) c).filter(t -> t == 't').count();
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return 1;
    }

    

    /**
     * Gets moves of a given level and monster
     * 
     * @param level the level identifier e.g. "level1"
     */
    public static String getMoves(String level){
        try {
            File inputFile = new File("levels/" + level + ".xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren();
            for (int i = 0; i < elements.size(); i++) {
               Element curr = elements.get(i);
                if(curr.getName().equals("moves")){
                    //on to right XML element
                    return curr.getText();
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return "";
    }

    /**
     * Gets moves of a given level and monster
     * 
     * @param level the level identifier e.g. "level1"
     */
    public static int getLevelTime(String level){
        try {
            File inputFile = new File("levels/" + level + ".xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren();
            for (int i = 0; i < elements.size(); i++) {
               Element curr = elements.get(i);
                if(curr.getName().equals("time")){
                    //on to right XML element
                    return Integer.parseInt(curr.getText());
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return 180;
    }
}
