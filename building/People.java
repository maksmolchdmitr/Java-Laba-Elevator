package building;

import color.Color;

import java.io.PrintStream;

import static color.Color.ANSI_RESET;
import static color.Color.ANSI_YELLOW;
import static java.lang.Math.random;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class People {
    private static final Color color = ANSI_YELLOW;
    private static final String INTERRUPT_REQUESTED_THREAD_MESSAGE = "Request thread was interrupted";
    private static final String WAKE_UP_PEOPLE_TEXT = "People are woken up";
    private static final String LAY_DOWN_PEOPLE_TEXT = "People are have laid down";
    private final int intervalInSeconds;
    private final Building building;
    private final PrintStream out;
    private boolean statusIsActive = false;
    private Thread requestThread;

    public People(int intervalInSeconds, Building building, PrintStream out) {
        this.intervalInSeconds = intervalInSeconds;
        this.building = building;
        this.out = out;
    }

    public void wakeUp() {
        out.printf(
                "%s%s%s\n",
                color,
                WAKE_UP_PEOPLE_TEXT,
                ANSI_RESET
        );
        if (!statusIsActive) {
            requestThread = getAndStartThread();
        }
        this.statusIsActive = true;
    }

    private Thread getAndStartThread() {
        Thread thread = new Thread(() -> {
            while (!currentThread().isInterrupted()) {
                building.sendRequest(getRandomFloor());
                try {
                    SECONDS.sleep(intervalInSeconds);
                } catch (InterruptedException e) {
                    break;
                }
            }
            out.printf(
                    "%s%s%s\n",
                    color,
                    INTERRUPT_REQUESTED_THREAD_MESSAGE,
                    ANSI_RESET
            );
        });
        thread.start();
        return thread;
    }

    private int getRandomFloor() {
        return (int) (random() * (building.floorCounts - 1)) + 1;
    }

    public static int getRandomFloor(Building building, PrintStream out) {
        int randomFloor = (int) (random() * (building.floorCounts - 1)) + 1;
        out.printf(
                "%sSome person choose floor %s to go there%s\n",
                color,
                randomFloor,
                ANSI_RESET
        );
        return randomFloor;
    }

    public void layDown() {
        out.printf(
                "%s%s%s\n",
                color,
                LAY_DOWN_PEOPLE_TEXT,
                ANSI_RESET
        );
        if (statusIsActive) {
            requestThread.interrupt();
        }
        this.statusIsActive = false;
    }
}
