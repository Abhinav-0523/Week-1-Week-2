import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
}

class ParkingLot {

    private ParkingSpot[] table;
    private int size;
    private int occupied = 0;
    private int totalProbes = 0;

    ParkingLot(int capacity) {
        table = new ParkingSpot[capacity];
        size = capacity;
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % size;
    }

    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index] != null) {
            index = (index + 1) % size; // linear probing
            probes++;
        }

        ParkingSpot spot = new ParkingSpot();
        spot.licensePlate = plate;
        spot.entryTime = System.currentTimeMillis();

        table[index] = spot;

        occupied++;
        totalProbes += probes;

        System.out.println("Vehicle " + plate +
                " parked at spot #" + index +
                " (" + probes + " probes)");
    }

    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index] != null) {

            if (table[index].licensePlate.equals(plate)) {

                long duration = (System.currentTimeMillis() -
                        table[index].entryTime) / 1000;

                double fee = duration * 0.05;

                table[index] = null;
                occupied--;

                System.out.println("Vehicle exited from spot #" + index +
                        " Duration: " + duration +
                        "s Fee: $" + fee);

                return;
            }

            index = (index + 1) % size;
        }

        System.out.println("Vehicle not found");
    }

    public void getStatistics() {

        double occupancy = (occupied * 100.0) / size;
        double avgProbes = (occupied == 0) ? 0 : (double) totalProbes / occupied;

        System.out.println("Occupancy: " + occupancy + "%");
        System.out.println("Average probes: " + avgProbes);
    }
}

public class PS8 {
    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}