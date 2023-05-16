import building.Building;
import building.People;

import static building.Direction.UP;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var out = System.out;
        Building building = new Building(20, out, 2);
        People people = new People(1, 10, building, out);
        people.wakeUp();
        building.sendRequest(11, UP);
        building.sendRequest(3, UP);
        building.waitEnd();
    }
}
