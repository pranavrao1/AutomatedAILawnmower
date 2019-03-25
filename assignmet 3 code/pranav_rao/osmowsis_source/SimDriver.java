import java.util.*;
import java.io.*;

public class SimDriver {
    private static Random randGenerator;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 100;

    private Integer lawnHeight;
    private Integer lawnWidth;
    private Integer[][] lawnInfo;
    private Integer mowerX, mowerY;
    private String mowerDirection;
    private HashMap<String, Integer> xDIR_MAP;
    private HashMap<String, Integer> yDIR_MAP;
    private int numCraters;

    private String trackAction;
    private Integer trackMoveDistance;
    private String trackNewDirection;
    private String trackMoveCheck;
    private String trackScanResults;
    private Integer[][] internalMowerLawn;
    private Integer internalMowerX, internalMowerY;

    private final int EMPTY_CODE = 0;
    private final int GRASS_CODE = 1;
    private final int CRATER_CODE = 2;
    private final int FENCE_CODE = 3;

    public SimDriver() {
        randGenerator = new Random();

        lawnHeight = 0;
        lawnWidth = 0;
        lawnInfo = new Integer[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        mowerX = -1;
        mowerY = -1;
        mowerDirection = "North";

        xDIR_MAP = new HashMap<>();
        xDIR_MAP.put("North", 0);
        xDIR_MAP.put("Northeast", 1);
        xDIR_MAP.put("East", 1);
        xDIR_MAP.put("Southeast", 1);
        xDIR_MAP.put("South", 0);
        xDIR_MAP.put("Southwest", -1);
        xDIR_MAP.put("West", -1);
        xDIR_MAP.put("Northwest", -1);

        yDIR_MAP = new HashMap<>();
        yDIR_MAP.put("North", 1);
        yDIR_MAP.put("Northeast", 1);
        yDIR_MAP.put("East", 0);
        yDIR_MAP.put("Southeast", -1);
        yDIR_MAP.put("South", -1);
        yDIR_MAP.put("Southwest", -1);
        yDIR_MAP.put("West", 0);
        yDIR_MAP.put("Northwest", 1);

        internalMowerLawn = new Integer[2 * DEFAULT_WIDTH][2 * DEFAULT_HEIGHT];
        internalMowerX = DEFAULT_WIDTH;
        internalMowerY = DEFAULT_HEIGHT;

    }

    public void uploadStartingFile(String testFileName) {
        final String DELIMITER = ",";

        try {
            Scanner takeCommand = new Scanner(new File(testFileName));
            String[] tokens;
            int i, j, k;

            // read in the lawn information
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnWidth = Integer.parseInt(tokens[0]);
            tokens = takeCommand.nextLine().split(DELIMITER);
            lawnHeight = Integer.parseInt(tokens[0]);

            // generate the lawn information
            lawnInfo = new Integer[lawnWidth][lawnHeight];
            for (i = 0; i < lawnWidth; i++) {
                for (j = 0; j < lawnHeight; j++) {
                    lawnInfo[i][j] = GRASS_CODE;
                }
            }

            // read in the lawnmower starting information
            tokens = takeCommand.nextLine().split(DELIMITER);
            int numMowers = Integer.parseInt(tokens[0]);
            for (k = 0; k < numMowers; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);
                mowerX = Integer.parseInt(tokens[0]);
                mowerY = Integer.parseInt(tokens[1]);
                mowerDirection = tokens[2];

                // mow the grass at the initial location
                lawnInfo[mowerX][mowerY] = EMPTY_CODE;
            }

            // read in the crater information
            tokens = takeCommand.nextLine().split(DELIMITER);
            this.numCraters = Integer.parseInt(tokens[0]);
            for (k = 0; k < numCraters; k++) {
                tokens = takeCommand.nextLine().split(DELIMITER);

                // place a crater at the given location
                lawnInfo[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])] = CRATER_CODE;
            }

            takeCommand.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    public void pollMowerForAction() {
        if (isMowerNearUnkown()) {
            trackAction = "scan";
        } else if (grassInDirection(mowerDirection) > 0) {
            trackAction = "move";
            trackNewDirection = mowerDirection;
            trackMoveDistance = grassInDirection(mowerDirection);
            updateInternalMowerMap();
        } else if (mowerAdjacentTowardsGrass() != null) {
            trackAction = "move";
            trackNewDirection = mowerAdjacentTowardsGrass();
            trackMoveDistance = 0;
        } else {
            boolean validMoveCommand = false;
            while (!validMoveCommand) {
                int newDistance = randGenerator.nextInt(3);
                Set<String> filteredList = filteredDirectionList();
                int newDirectionIndex = randGenerator.nextInt(filteredList.size());
                String newDirection = (String) filteredList.toArray()[newDirectionIndex];
                if (isValidMove(newDistance)) {
                    trackAction = "move";
                    validMoveCommand = true;
                    trackNewDirection = newDirection;
                    trackMoveDistance = newDistance;
                    updateInternalMowerMap();
                }
            }

        }
    }

    private void updateInternalMowerMap() {
        int xOrientation = xDIR_MAP.get(mowerDirection);
        int yOrientation = yDIR_MAP.get(mowerDirection);
        internalMowerX += trackMoveDistance * xOrientation;
        internalMowerY += trackMoveDistance * yOrientation;
        internalMowerLawn[internalMowerX][internalMowerY] = EMPTY_CODE;
        if (trackMoveDistance > 1) {
            internalMowerLawn[internalMowerX - xOrientation][internalMowerY - yOrientation] = EMPTY_CODE;
        }
    }

    public void validateMowerAction() {
        int xOrientation, yOrientation;

        if (trackAction.equals("scan")) {
            trackScanResults = scan();

        } else if (trackAction.equals("move")) {
            // in the case of a move, ensure that the move doesn't cross craters or fences
            xOrientation = xDIR_MAP.get(mowerDirection);
            yOrientation = yDIR_MAP.get(mowerDirection);

            // just for this demonstration, allow the mower to change direction
            // even if the move forward causes a crash
            mowerDirection = trackNewDirection;

            int newSquareX = mowerX + trackMoveDistance * xOrientation;
            int newSquareY = mowerY + trackMoveDistance * yOrientation;

            if (newSquareX >= 0 & newSquareX < lawnWidth & newSquareY >= 0 & newSquareY < lawnHeight) {
                if (trackMoveDistance > 1) {
                    int intermSqaureX = mowerX + xOrientation;
                    int intermSquareY = mowerY + yOrientation;
                    if (lawnInfo[intermSqaureX][intermSquareY] != CRATER_CODE) {
                        lawnInfo[intermSqaureX][intermSquareY] = EMPTY_CODE;
                        mowerX = newSquareX;
                        mowerY = newSquareY;
                        trackMoveCheck = "ok";

                        // update lawn status
                        lawnInfo[mowerX][mowerY] = EMPTY_CODE;
                    } else {
                        trackMoveCheck = "crash";
                    }

                } else {
                    mowerX = newSquareX;
                    mowerY = newSquareY;
                    trackMoveCheck = "ok";

                    // update lawn status
                    lawnInfo[mowerX][mowerY] = EMPTY_CODE;
                }
            } else {
                trackMoveCheck = "crash";
            }

        } else if (trackAction.equals("turn_off")) {
            trackMoveCheck = "ok";
        }
    }

    public void displayActionAndResponses() {
        // display the mower's actions
        System.out.print(trackAction);
        if (trackAction.equals("move")) {
            System.out.println("," + trackMoveDistance + "," + trackNewDirection);
        } else {
            System.out.println();
        }

        // display the simulation checks and/or responses
        if (trackAction.equals("move") | trackAction.equals("turn_off")) {
            System.out.println(trackMoveCheck);
        } else if (trackAction.equals("scan")) {
            System.out.println(trackScanResults);
        } else {
            System.out.println("action not recognized");
        }
    }

    private String scan() {
        Integer[] scaneddata = new Integer[8];

        scaneddata[0] = validteSquare(mowerX, mowerY + 1);
        scaneddata[1] = validteSquare(mowerX + 1, mowerY + 1);
        scaneddata[2] = validteSquare(mowerX + 1, mowerY);
        scaneddata[3] = validteSquare(mowerX + 1, mowerY - 1);
        scaneddata[4] = validteSquare(mowerX, mowerY - 1);
        scaneddata[5] = validteSquare(mowerX - 1, mowerY - 1);
        scaneddata[6] = validteSquare(mowerX - 1, mowerY);
        scaneddata[7] = validteSquare(mowerX - 1, mowerY + 1);

        internalMowerLawn[internalMowerX + 1][internalMowerY] = scaneddata[2];
        internalMowerLawn[internalMowerX + 1][internalMowerY + 1] = scaneddata[1];
        internalMowerLawn[internalMowerX][internalMowerY + 1] = scaneddata[0];
        internalMowerLawn[internalMowerX - 1][internalMowerY + 1] = scaneddata[7];
        internalMowerLawn[internalMowerX - 1][internalMowerY] = scaneddata[6];
        internalMowerLawn[internalMowerX - 1][internalMowerY - 1] = scaneddata[5];
        internalMowerLawn[internalMowerX][internalMowerY - 1] = scaneddata[4];
        internalMowerLawn[internalMowerX + 1][internalMowerY - 1] = scaneddata[3];

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            stringBuilder.append(returnNameForCode(scaneddata[i]));
            if (i != 7) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private Integer validteSquare(Integer x, Integer y) {
        if (x >= 0 && x < lawnWidth && y >= 0 && y < lawnHeight) {
            return lawnInfo[x][y];
        } else {
            return FENCE_CODE;
        }
    }

    private String returnNameForCode(Integer code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case GRASS_CODE:
                return "grass";
            case FENCE_CODE:
                return "fence";
            case EMPTY_CODE:
                return "empty";
            case CRATER_CODE:
                return "crater";
            default:
                return null;
        }
    }

    private boolean isMowerNearUnkown() {
        return returnNameForCode(internalMowerLawn[internalMowerX + 1][internalMowerY]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX + 1][internalMowerY + 1]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX][internalMowerY + 1]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX - 1][internalMowerY + 1]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX - 1][internalMowerY]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX - 1][internalMowerY - 1]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX][internalMowerY - 1]) == null ||
                returnNameForCode(internalMowerLawn[internalMowerX + 1][internalMowerY - 1]) == null;
    }

    private int grassInDirection(String direction) {
        switch (direction) {
            case "South":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX][internalMowerY - 1], internalMowerLawn[internalMowerX][internalMowerY - 2]);
            case "Southwest":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX - 1][internalMowerY - 1], internalMowerLawn[internalMowerX - 2][internalMowerY - 2]);
            case "West":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX - 1][internalMowerY], internalMowerLawn[internalMowerX - 2][internalMowerY]);
            case "Northwest":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX - 1][internalMowerY + 1], internalMowerLawn[internalMowerX - 2][internalMowerY + 2]);
            case "Southeast":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX + 1][internalMowerY - 1], internalMowerLawn[internalMowerX + 2][internalMowerY - 2]);
            case "North":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX][internalMowerY + 1], internalMowerLawn[internalMowerX][internalMowerY + 2]);
            case "Northeast":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX + 1][internalMowerY + 1], internalMowerLawn[internalMowerX + 2][internalMowerY + 2]);
            case "East":
                return compareTwoSequenceSquares(internalMowerLawn[internalMowerX + 1][internalMowerY], internalMowerLawn[internalMowerX + 2][internalMowerY]);
            default:
                return 0;
        }
    }

    private int scoreOfGrassInDirection(String direction) {
        switch (direction) {
            case "South":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX][internalMowerY - 1], internalMowerLawn[internalMowerX][internalMowerY - 2]);
            case "Southwest":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX - 1][internalMowerY - 1], internalMowerLawn[internalMowerX - 2][internalMowerY - 2]);
            case "West":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX - 1][internalMowerY], internalMowerLawn[internalMowerX - 2][internalMowerY]);
            case "Northwest":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX - 1][internalMowerY + 1], internalMowerLawn[internalMowerX - 2][internalMowerY + 2]);
            case "Southeast":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX + 1][internalMowerY - 1], internalMowerLawn[internalMowerX + 2][internalMowerY - 2]);
            case "North":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX][internalMowerY + 1], internalMowerLawn[internalMowerX][internalMowerY + 2]);
            case "Northeast":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX + 1][internalMowerY + 1], internalMowerLawn[internalMowerX + 2][internalMowerY + 2]);
            case "East":
                return scoreTwoSequenceSquares(internalMowerLawn[internalMowerX + 1][internalMowerY], internalMowerLawn[internalMowerX + 2][internalMowerY]);
            default:
                return 0;
        }
    }

    private String mowerAdjacentTowardsGrass() {
        Set<String> directions = new HashSet<>(xDIR_MAP.keySet());
        directions.remove(mowerDirection);
        int maxScore = 0;
        String bestDirection = null;
        for (String direction : directions) {
            if (scoreOfGrassInDirection(direction) > maxScore) {
                bestDirection = direction;
                maxScore = scoreOfGrassInDirection(direction);
            }
        }
        return bestDirection;
    }

    private boolean isValidMove(Integer distance) {
        int xOrientation = xDIR_MAP.get(mowerDirection);
        int yOrientation = yDIR_MAP.get(mowerDirection);

        int newSquareX = internalMowerX + distance * xOrientation;
        int newSquareY = internalMowerY + distance * yOrientation;
        if (distance == 1) {
            return internalMowerLawn[newSquareX][newSquareY] != null && internalMowerLawn[newSquareX][newSquareY] != CRATER_CODE && internalMowerLawn[newSquareX][newSquareY] != FENCE_CODE;
        } else if (distance == 2) {
            return internalMowerLawn[newSquareX][newSquareY] != null && internalMowerLawn[newSquareX][newSquareY] != CRATER_CODE && internalMowerLawn[newSquareX][newSquareY] != FENCE_CODE && internalMowerLawn[newSquareX - xOrientation][newSquareY - yOrientation] != null && internalMowerLawn[newSquareX - xOrientation][newSquareY - yOrientation] != CRATER_CODE && internalMowerLawn[newSquareX - xOrientation][newSquareY - yOrientation] != FENCE_CODE;
        } else {
            return true;
        }
    }

    private int compareTwoSequenceSquares(Integer firstSquareResult, Integer secondSquareResult) {
        int distance = 0;
        if (firstSquareResult != null) {
            if (firstSquareResult == GRASS_CODE) {
                distance = 1;
                if (secondSquareResult != null && secondSquareResult == GRASS_CODE) {
                    distance = 2;
                }
            } else if (firstSquareResult == EMPTY_CODE) {
                if (secondSquareResult != null && secondSquareResult == GRASS_CODE) {
                    distance = 2;
                }
            }
        }
        return distance;
    }

    private int scoreTwoSequenceSquares(Integer firstSquareResult, Integer secondSquareResult) {
        int score = 0;
        if (firstSquareResult != null) {
            if (firstSquareResult == GRASS_CODE) {
                score = 2;
                if (secondSquareResult != null && secondSquareResult == GRASS_CODE) {
                    score = 3;
                }
            } else if (firstSquareResult == EMPTY_CODE) {
                if (secondSquareResult != null && secondSquareResult == GRASS_CODE) {
                    score = 1;
                }
            }
        }
        return score;
    }

    private boolean invalidSqaure(int value) {
        if (value == GRASS_CODE || value == EMPTY_CODE) {
            return false;
        } else {
            return true;
        }
    }

    private Set<String> filteredDirectionList() {
        Set<String> directions = new HashSet<>(xDIR_MAP.keySet());
        if (invalidSqaure(internalMowerLawn[internalMowerX][internalMowerY - 1])) {
            directions.remove("South");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX][internalMowerY + 1])) {
            directions.remove("North");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX + 1][internalMowerY + 1])) {
            directions.remove("Northeast");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX + 1][internalMowerY])) {
            directions.remove("East");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX + 1][internalMowerY - 1])) {
            directions.remove("Southeast");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX-1][internalMowerY - 1])) {
            directions.remove("Southwest");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX - 1][internalMowerY])) {
            directions.remove("West");
        }

        if (invalidSqaure(internalMowerLawn[internalMowerX - 1][internalMowerY + 1])) {
            directions.remove("Northwest");
        }
        return directions;
    }

    public String getTrackAction() {
        return trackAction;
    }

    public int getMapSize() {
        return lawnWidth*lawnHeight;
    }

    public int numberOfGreenSquares() {
        return getMapSize() - numCraters;
    }

    public int numberOfCutGreenSquares() {
        int unCutGrass = 0;
        for (int i = 0; i < lawnHeight; i++) {
            for (int j = 0; j < lawnWidth; j++) {
                if (lawnInfo[j][i] == GRASS_CODE) {
                    unCutGrass++;
                }
            }
        }
        return numberOfGreenSquares() - unCutGrass;
    }

}