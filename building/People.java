package building;

import java.io.PrintStream;

public final class People {
    private static final String WAKE_UP_PEOPLE_TEXT = "People are woken up";
    private static final String LAY_DOWN_PEOPLE_TEXT = "People are have laid down";
    private final int interval;
    private final int count;
    private final Building building;
    private final PrintStream out;
    private boolean statusIsActive = false;

    public People(int interval, int count, Building building, PrintStream out) {
        this.interval = interval;
        this.count = count;
        this.building = building;
        this.out = out;
    }

    public void wakeUp() {
        out.println(WAKE_UP_PEOPLE_TEXT);
        this.statusIsActive = true;
    }

    public void layDown() {
        out.println(LAY_DOWN_PEOPLE_TEXT);
        this.statusIsActive = false;
    }
}
