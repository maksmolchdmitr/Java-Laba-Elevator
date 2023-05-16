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
    private final Set<Floor> floorsToStayOn = new HashSet<>();
    private final PrintStream out;
    private final Color color;

    private record Floor(
            int floorNumber,
            boolean isCall
    ) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Floor floor = (Floor) o;
            return floorNumber == floor.floorNumber && isCall == floor.isCall;
        }
    }

    public Elevator(
            String name,
            int currentFloor,
            Building building,
            PrintStream out,
            Color color
    ) {
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
        final boolean statusIsStayedFromCall = this.floorsToStayOn.contains(new Floor(this.currentFloor, true));
        final boolean statusIsStayed = this.floorsToStayOn.contains(new Floor(this.currentFloor, false));
        if (statusIsStayed || statusIsStayedFromCall) {
            openDoor(out);
            if (!this.floorsToStayOn.remove(new Floor(this.currentFloor, true)) &&
                    !this.floorsToStayOn.remove(new Floor(this.currentFloor, false))) {
                throw new RuntimeException("There is not the floor %s in %s"
                        .formatted(this.currentFloor, this.floorsToStayOn));
            }
        }
        if (statusIsStayedFromCall) {
            addPeopleFloor();
        }
        thinkAboutWhereGo();
        printCurrentState(out);
        if (statusIsStayed) {
            SECONDS.sleep(DOOR_HOLDING_TIME_IN_SECONDS);
            closeDoor(out);
        }
    }

    private void addPeopleFloor() {
        this.floorsToStayOn.add(new Floor(People.getRandomFloor(building, out), false));
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
                .filter(floor -> floor.floorNumber < this.currentFloor)
                .count();
        int countFloorAbove = (int) this.floorsToStayOn
                .stream()
                .filter(floor -> floor.floorNumber > this.currentFloor)
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
                this.floorsToStayOn
                        .stream()
                        .map(Floor::floorNumber)
                        .toList(),
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
        this.floorsToStayOn.add(new Floor(floor, true));
    }
}
