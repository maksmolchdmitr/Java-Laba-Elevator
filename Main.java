import building.Building;
import building.People;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var out = System.out;
        Building building = new Building(20, out, 2);
        People people = new People(5, building, out);
        people.wakeUp();
        TimeUnit.SECONDS.sleep(10);
        people.layDown();
//        building.waitEnd();
    }
}
