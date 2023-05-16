package building;

import java.io.PrintStream;

import static java.lang.Thread.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Engines {
    private final static String FIRST_ENGINE_NAME = "first_engine";
    private final static String SECOND_ENGINE_NAME = "second_engine";
    private final Thread firstEngine, secondEngine;

    public Engines(
            Elevator firstElevator,
            Elevator secondElevator,
            int elevatorSpeedInSeconds,
            PrintStream out
    ) {
        firstEngine = getAndStartThread(FIRST_ENGINE_NAME, firstElevator, elevatorSpeedInSeconds, out);
        secondEngine = getAndStartThread(SECOND_ENGINE_NAME, secondElevator, elevatorSpeedInSeconds, out);
    }

    private Thread getAndStartThread(
            String engineName,
            Elevator firstElevator,
            int elevatorSpeedInSeconds,
            PrintStream out
    ) {
        final Thread firstEngine;
        firstEngine = createEngine(engineName, firstElevator, elevatorSpeedInSeconds, out);
        firstEngine.start();
        return firstEngine;
    }

    private static Thread createEngine(
            String engineName,
            Elevator elevator,
            int elevatorSpeedInSeconds,
            PrintStream out
    ) {
        return new Thread(() -> {
            while (!currentThread().isInterrupted()) {
                try {
                    SECONDS.sleep(elevatorSpeedInSeconds);
                    elevator.move();
                } catch (InterruptedException e) {
                    out.printf("Engine '%s' was stopped!\n", engineName);
                    break;
                }
            }
        });
    }

    public void waitEnd() throws InterruptedException {
        this.firstEngine.join();
        this.secondEngine.join();
    }

    public void stop() {
        this.firstEngine.interrupt();
        this.secondEngine.interrupt();
    }
}
