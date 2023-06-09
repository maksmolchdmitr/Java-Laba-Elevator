package building;

import color.Color;

import java.io.PrintStream;

public final class Building {
    private final static String FIRST_ELEVATOR_NAME = "First_elevator";
    private final static String SECOND_ELEVATOR_NAME = "Second_elevator";
    public final int floorCounts;
    private final Elevator firstElevator, secondElevator;
    private final Engines engines;
    private final PrintStream out;

    public Building(int floorCounts, PrintStream out, int elevatorSpeedInSeconds) {
        this.floorCounts = floorCounts;
        this.firstElevator = new Elevator(
                FIRST_ELEVATOR_NAME,
                1,
                this,
                out, Color.ANSI_GREEN
        );
        this.secondElevator = new Elevator(
                SECOND_ELEVATOR_NAME,
                floorCounts,
                this,
                out, Color.ANSI_BLUE
        );
        engines = new Engines(firstElevator, secondElevator, elevatorSpeedInSeconds, out);
        this.out = out;
    }

    public void sendRequest(int floorNumber) {
        out.printf("Request to floor %d was sent\n", floorNumber);
        if (floorNumber > floorCounts) {
            throw new RuntimeException("Max floor value is %s".formatted(this.floorCounts));
        }
        if (firstElevator.getDiffFromFloor(floorNumber) < secondElevator.getDiffFromFloor(floorNumber)) {
            firstElevator.assignDirectionWithFloor(floorNumber);
            out.printf("FirstElevator was assigned to floor %d\n", floorNumber);
        } else {
            secondElevator.assignDirectionWithFloor(floorNumber);
            out.printf("SecondElevator was assigned to floor %d\n", floorNumber);
        }
    }

    public void waitEnd() throws InterruptedException {
        engines.waitEnd();
    }

    public void stopEngines() {
        this.engines.stop();
    }
}
