import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
// possible exceptions
import java.io.IOException;

public class Map {
    private ArrayList<Room> rooms;
    private boolean mapLoading = false;
    private boolean loadingScreenDrawn = false;
    private int numberOfEnemies = 2;
    private BufferedImage loadingImage;

    Map(int x, int y, ArrayList<Entity> entities, int mapSize) {
        rooms = new ArrayList<Room>();
        try {
            loadingImage = ImageIO.read(new File("images/backgrounds/loadingPage.png"));
        } catch (IOException ex) {
            System.out.println(ex);
            System.out.println("failed to load wall");
        }

        loadMapFile(entities, x, y, mapSize);
    }

    public void loadMapFile(ArrayList<Entity> entities, int x, int y, int mapSize) {
        int numberOfCustomRooms = 0;
        File mapFile = new File("");
        Scanner mapScanner = new Scanner("");
        try {
            mapFile = new File("rooms.txt");
            mapScanner = new Scanner(mapFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        mapScanner = readRoom("rooms.txt", 0);
        while (mapScanner.hasNext()) {
            if (mapScanner.nextLine().equals("---")) {
                numberOfCustomRooms++;
            }
        }

        int xPos = x;
        int yPos = y;
        int lineNum = 0;
        int startX = -1;
        int startY = -1;
        ArrayList<Integer> exitX = new ArrayList<Integer>();
        ArrayList<Integer> exitY = new ArrayList<Integer>();
        // int exitX = -1;
        // int exitY = -1;
        int roomLength = 0;
        int roomWidth = 0;
        int roomsSpawned = 0;
        String[] wallTypes = { "tiledWall", "woodenWall", "stoneWall", "cobbleWall", "brickWall" };

        while (roomsSpawned < mapSize) {
            roomsSpawned++;

            int roomToSpawn = randint(0, numberOfCustomRooms - 1);
            String wallTypeToSpawn = wallTypes[randint(0, wallTypes.length - 1)];
            // Set the map scanner to the position where the room is in the file
            boolean roomFits = false;
            boolean loadRoom = true;
            int timesTriedToLoad = 0;
            // Create a default room
            rooms.add(new Room());
            do {
                mapScanner = readRoom("rooms.txt", roomToSpawn);
                loadRoom = true;
                while (loadRoom) {
                    String text = mapScanner.nextLine();
                    if (text.equals("---")) {
                        loadRoom = false;
                    } else {
                        roomLength = text.length() * 50;
                        for (int i = 0; i < text.length(); i++) {
                            String subsection = text.substring(i, i + 1);
                            if (subsection.equals("S")) {
                                startX = xPos + i * 50;
                                startY = lineNum * 50;
                            }
                        }
                        yPos += 50;
                        lineNum++;
                    }
                    roomWidth = lineNum * 50;
                }

                mapScanner = readRoom("rooms.txt", roomToSpawn);
                loadRoom = true;
                lineNum = 0;
                if (!exitX.isEmpty() && !exitY.isEmpty()) {
                    yPos = exitY.get(0) - startY;
                    xPos = exitX.get(0) + 50;
                } else {
                    xPos = x;
                    yPos = y;
                }
                // Adjust last room
                rooms.get(rooms.size() - 1).setX(xPos);
                rooms.get(rooms.size() - 1).setY(yPos);
                rooms.get(rooms.size() - 1).setLength(roomLength);
                rooms.get(rooms.size() - 1).setWidth(roomWidth);

                if (rooms.get(rooms.size() - 1).overlaps(rooms)) {
                    roomFits = false;
                    // select a different room if it doesnt fit
                    roomToSpawn = randint(0, numberOfCustomRooms - 1);
                    timesTriedToLoad++;
                } else {
                    roomFits = true;
                    // remove the exit locations when the room is successfully built
                    if (!exitX.isEmpty()) {
                        exitX.remove(0);
                        exitY.remove(0);
                    }
                }
                // check if the new room clips any other room
            } while (!roomFits && timesTriedToLoad < 100);

            if (timesTriedToLoad == 100) {
                entities.add(new Wall(exitX.get(0), exitY.get(0), 50, 50, wallTypeToSpawn));
                entities.add(new Wall(exitX.get(0), exitY.get(0) + 50, 50, 50, wallTypeToSpawn));
                removeEntityAt(exitX.get(0) + 10, exitY.get(0), entities);
                removeEntityAt(exitX.get(0) + 10, exitY.get(0) + 50, entities);
                exitX.remove(0);
                exitY.remove(0);
                rooms.remove(rooms.size() - 1);
                loadRoom = false;
                System.out.println("Triggered wall");
            }
            if (loadRoom) {
                loadWalls(xPos, yPos, mapScanner, entities, exitX, exitY, wallTypeToSpawn, roomsSpawned);
            }

        }

        // make sure to close all of the exits in the end after the max number of levels
        // have been loaded
        while (!exitX.isEmpty() && !exitY.isEmpty()) {
            String wallTypeToSpawn = wallTypes[randint(0, wallTypes.length - 1)];
            // Set the map scanner to the position where the room is in the file
            boolean roomFits = false;
            boolean loadRoom = true;
            // Create a default room
            rooms.add(new Room());
            mapScanner = readRoom("endRooms.txt", 0);
            loadRoom = true;
            while (loadRoom) {
                String text = mapScanner.nextLine();
                if (text.equals("---")) {
                    loadRoom = false;
                } else {
                    roomLength = text.length() * 50;
                    for (int i = 0; i < text.length(); i++) {
                        String subsection = text.substring(i, i + 1);
                        if (subsection.equals("S")) {
                            startX = xPos + i * 50;
                            startY = lineNum * 50;
                        }
                    }
                    yPos += 50;
                    lineNum++;
                }
                roomWidth = lineNum * 50;
            }

            mapScanner = readRoom("endRooms.txt", 0);
            loadRoom = true;
            lineNum = 0;
            if (!exitX.isEmpty() && !exitY.isEmpty()) {
                yPos = exitY.get(0) - startY;
                xPos = exitX.get(0) + 50;
            } else {
                xPos = x;
                yPos = y;
            }
            // Adjust last room
            rooms.get(rooms.size() - 1).setX(xPos);
            rooms.get(rooms.size() - 1).setY(yPos);
            rooms.get(rooms.size() - 1).setLength(roomLength);
            rooms.get(rooms.size() - 1).setWidth(roomWidth);

            if (rooms.get(rooms.size() - 1).overlaps(rooms)) {
                roomFits = false;
            } else {
                roomFits = true;
            }

            if (!roomFits) {
                entities.add(new Wall(exitX.get(0), exitY.get(0), 50, 50, wallTypeToSpawn));
                entities.add(new Wall(exitX.get(0), exitY.get(0) + 50, 50, 50, wallTypeToSpawn));
                removeEntityAt(exitX.get(0) + 10, exitY.get(0), entities);
                removeEntityAt(exitX.get(0) + 10, exitY.get(0) + 50, entities);
                exitX.remove(0);
                exitY.remove(0);
                rooms.remove(rooms.size() - 1);
                loadRoom = false;
            } else if (!exitX.isEmpty() && !exitY.isEmpty()) {
                xPos = exitX.get(0) + 50;
                yPos = exitY.get(0) - startY;
                exitX.remove(0);
                exitY.remove(0);
                mapScanner = readRoom("endRooms.txt", 0);
                loadWalls(xPos, yPos, mapScanner, entities, exitX, exitY, "tiledWall", roomsSpawned);
            }
        }

        mapScanner.close();
    }

    public void loadWalls(int xPos, int yPos, Scanner mapScanner,
            ArrayList<Entity> entities, ArrayList<Integer> exitX,
            ArrayList<Integer> exitY, String wallTypeToSpawn, int roomsSpawned) {
        boolean loadRoom = true;
        boolean noEnemies = false;
        while (loadRoom) {
            String text = mapScanner.nextLine();
            if (text.equals("---")) {
                loadRoom = false;
            } else {
                for (int i = 0; i < text.length(); i++) {
                    String subsection = text.substring(i, i + 1);
                    if (subsection.equals("#")) {
                        // this part extends a block instead of adding another
                        if (entities.get(entities.size() - 1).getType().equals("Wall")
                                && entities.get(entities.size() - 1).getY() == yPos &&
                                (xPos + i * 50) - (entities.get(entities.size() - 1).getX()
                                        + entities.get(entities.size() - 1).getLength()) == 0) {
                            entities.get(entities.size() - 1)
                                    .setLength(entities.get(entities.size() - 1).getLength() + 50);
                        } else {
                            entities.add(new Wall(xPos + i * 50, yPos, 50, 50, wallTypeToSpawn));
                        }
                    } else if (subsection.equals("|")) {
                        entities.add(new Door(xPos + i * 50 + 10, yPos, 30, 50, "door"));
                    } else if (subsection.equals("-")) {
                        entities.add(new Platform(xPos + i * 50 + 10, yPos + 20, 30, 10, "platform"));
                    } else if (subsection.equals("B")) {
                        entities.add(new Box(xPos + i * 50, yPos, 50, 50, "box", 10));
                    } else if (subsection.equals("S")) {
                        if (roomsSpawned != 1) {
                            entities.add(new Door(xPos + i * 50 + 10, yPos, 30, 50, "door"));
                        } else {
                            entities.add(new Wall(xPos + i * 50, yPos, 50, 50, wallTypeToSpawn));
                            entities.add(new Wall(xPos + i * 50, yPos + 50, 50, 50, wallTypeToSpawn));
                        }
                    } else if (subsection.equals("E")) {
                        entities.add(new Door(xPos + i * 50 + 10, yPos, 30, 50, "door"));
                        exitX.add(xPos + i * 50);
                        exitY.add(yPos);
                    } else if (subsection.equals("$")) {
                        entities.add(new Shop(xPos + i * 50 + 10, yPos, 100, 100, "shop", 5));
                        noEnemies = true;
                    } else if (subsection.equals("$")) {
                        entities.add(new Shop(xPos + i * 50 + 10, yPos, 100, 100, "shop", 5));
                        noEnemies = true;
                    } else if (subsection.equals("L")) {
                        entities.add(new Button(xPos + i * 50 + 10, yPos, 50, 50, "lever"));
                        noEnemies = true;
                    }
                }
                yPos += 50;
            }
        }

        if (roomsSpawned != 1 && !noEnemies) {
            int min = 0;
            int max = 0;
            if (numberOfEnemies < 2) {
                min = 0;
                max = 4;
            } else {
                min = numberOfEnemies - 2;
                max = numberOfEnemies + 2;
            }
            for (int i = 0; i < randint(min, max); i++) {
                addEnemy(rooms.get(rooms.size() - 1), entities);
            }
        }
    }

    private boolean removeEntityAt(int x, int y, ArrayList<Entity> entities) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).getX() == x && entities.get(i).getY() == y) {
                entities.remove(i);
                return true;
            }
        }
        return false;
    }

    public void addEnemy(Room room, ArrayList<Entity> entities) {
        int spawnX = randint(room.getX(), room.getX() + room.getLength() - 10);
        int spawnY = randint(room.getY(), room.getY() + room.getWidth() - 10);

        File enemyFile = new File("");
        Scanner enemyScanner = new Scanner("");
        try {
            enemyFile = new File("enemyChances.txt");
            enemyScanner = new Scanner(enemyFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        int numberOfUniqueEnemies = 0;
        while (enemyScanner.hasNext()) {
            enemyScanner.nextLine();
            numberOfUniqueEnemies++;
        }

        String enemyName = null;
        while (enemyName == null) {
            enemyScanner = loadEnemy("enemyChances.txt", randint(0, numberOfUniqueEnemies - 1));
            if (enemyScanner.nextInt() > randint(0, 100)) {
                enemyName = enemyScanner.next();
            }
        }

        if (enemyName.equals("RocketGuard")) {
            entities.add(new RocketGuard(spawnX, spawnY, 34, 44, "Guard/"));
        } else if (enemyName.equals("Guard")) {
            entities.add(new Guard(spawnX, spawnY, 34, 44, "Guard/"));
        } else if (enemyName.equals("SwordGuard")) {
            entities.add(new SwordGuard(spawnX, spawnY, 34, 44, "Guard/"));
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < entities.size(); i++) {
                if (rectRectDetect(entities.get(entities.size() - 1),
                        entities.get(i)) && entities.size() - 1 != i) {
                    entities.get(entities.size() - 1).setX(randint(room.getX(), room.getX() + room.getLength() - 10));
                    entities.get(entities.size() - 1).setY(randint(room.getY(), room.getY() + room.getWidth() - 10));
                    changed = true;
                }
            }
        }
    }

    public Scanner loadEnemy(String fileName, int enemyNumber) {
        Scanner enemyScanner = new Scanner("");
        File enemyFile;
        try {
            enemyFile = new File(fileName);
            enemyScanner = new Scanner(enemyFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        int enemiesPassed = 0;
        while (enemiesPassed < enemyNumber) {
            enemyScanner.nextLine();
            enemiesPassed++;
        }
        return enemyScanner;
    }

    public Scanner readRoom(String fileName, int roomNumber) {
        Scanner mapScanner = new Scanner("");
        File mapFile;
        try {
            mapFile = new File(fileName);
            mapScanner = new Scanner(mapFile);
        } catch (Exception e) {
            System.out.println("File could not be found");
        }

        int roomsPassed = 0;
        while (roomsPassed < roomNumber) {
            String text = mapScanner.nextLine();
            if (text.equals("---")) {
                roomsPassed++;
            }
        }
        return mapScanner;
    }

    public void recreate(int x, int y, int mapSize, ArrayList<Entity> entities) {
        rooms.clear();
        numberOfEnemies += 2;
        loadMapFile(entities, x, y, mapSize);
    }

    public boolean rectRectDetect(Entity rect, Entity rect2) {
        double leftSide = rect.getX();
        double rightSide = rect.getX() + rect.getLength();
        double topSide = rect.getY();
        double botSide = rect.getY() + rect.getWidth();
        if (rect2.getX() + rect2.getLength() > leftSide && rect2.getX() < rightSide &&
                rect2.getY() + rect2.getWidth() > topSide
                && rect2.getY() < botSide) {
            return true;
        }
        return false;
    }

    public int randint(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public void drawLoadingScreen(Graphics g) {
        g.drawImage(loadingImage, 0, 0, null);
    }

    public void emptyRooms() {
        rooms.clear();
    }

    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    public boolean getMapLoading() {
        return mapLoading;
    }

    public boolean getLoadingScreenDrawn() {
        return this.loadingScreenDrawn;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    public void setMapLoading(boolean mapLoading) {
        this.mapLoading = mapLoading;
    }

    public void setLoadingScreenDrawn(boolean loadingScreenDrawn) {
        this.loadingScreenDrawn = loadingScreenDrawn;
    }

    public void setNumberOfEnemies(int numberOfEnemies) {
        this.numberOfEnemies = numberOfEnemies;
    }
}
