import building.Building;
import building.People;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var out = System.out;
        Building building = new Building(20, out, 2);
        People people = new People(5, building, out);
        people.wakeUp();
        SECONDS.sleep(10);
        people.layDown();
        building.waitEnd();
    }
}
