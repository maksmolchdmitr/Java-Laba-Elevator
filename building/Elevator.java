package building;

import color.Color;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static building.Direction.DOWN;
import static building.Direction.UP;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Elevator {
    private final static int DOOR_HOLDING_TIME_IN_SECONDS = 10;
    private final String name;
    private int currentFloor;
    private Direction currentDirection = null;
    private final Building building;
    private final Set<Integer> floorsToStayOn = new HashSet<>();
    private final PrintStream out;
    private final Color color;

    public Elevator(String name, int currentFloor, Building building, PrintStream out, Color color) {
        this.name = name;
        this.currentFloor = currentFloor;
        this.building = building;
        this.out = out;
        this.color = color;
    }

    public void move() throws InterruptedException {
        if (this.currentDirection != null) {
            int newFloor = currentFloor + (currentDirection == DOWN ? -1 : 1);
            if (newFloor > building.floorCounts || newFloor < 1) {
                newFloor = currentFloor;
            }
            this.currentFloor = newFloor;
        }
        final boolean statusIsStayed = this.floorsToStayOn.contains(this.currentFloor);
        if (statusIsStayed) {
            openDoor(out);
            if (!this.floorsToStayOn.remove(this.currentFloor)) {
                throw new RuntimeException("There is not the floor %s in %s"
                        .formatted(this.currentFloor, this.floorsToStayOn));
            }
        }
        thinkAboutWhereGo();
        printCurrentState(out);
        if (statusIsStayed) {
            SECONDS.sleep(DOOR_HOLDING_TIME_IN_SECONDS);
            closeDoor(out);
        }
    }

    private void thinkAboutWhereGo() {
        if (this.floorsToStayOn.isEmpty()) {
            this.currentDirection = null;
        } else {
            calculateNewDirection();
        }
    }

    private void calculateNewDirection() {
        int countFloorBelow = (int) this.floorsToStayOn
                .stream()
                .filter(floor -> floor < this.currentFloor)
                .count();
        int countFloorAbove = (int) this.floorsToStayOn
                .stream()
                .filter(floor -> floor > this.currentFloor)
                .count();
        this.currentDirection = (countFloorBelow > countFloorAbove ? DOWN : UP);
    }

    private void closeDoor(PrintStream out) {
        out.printf(
                "%s%s\tClose door%s\n",
                color,
                this.name,
                Color.ANSI_RESET
        );
    }

    private void openDoor(PrintStream out) {
        out.printf(
                "%s%s\tOpen door%s\n",
                color,
                this.name,
                Color.ANSI_RESET
        );
    }

    public synchronized void printCurrentState(PrintStream out) {
        out.printf(
                "%s%s\tCurrentFloorNumber=%d\tCurrentDirection=%s\tFloorsToStayOn=%s%s\n",
                color,
                this.name,
                this.currentFloor,
                this.currentDirection,
                this.floorsToStayOn,
                Color.ANSI_RESET
        );
    }

    public synchronized int getDiffFromFloor(int floor) {
        return Math.abs(floor - this.currentFloor);
    }

    public synchronized void assignDirectionWithFloor(int floor) {
        if (this.currentFloor - floor > 0) {
            this.currentDirection = DOWN;
        } else {
            this.currentDirection = UP;
        }
        this.floorsToStayOn.add(floor);
    }
}
