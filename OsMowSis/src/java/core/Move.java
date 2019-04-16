package core;

public class Move {
    private String direction;
    private int step;

    public Move(String direction, int step) {
        this.direction = direction;
        this.step = step;
    }

    public String getDirection() {
        return direction;
    }

    public int getStep() {
        return step;
    }

}
