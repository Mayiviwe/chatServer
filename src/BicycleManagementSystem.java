import java.util.*;

class Bicycle {
    String name, make, type;
    int availableCount;

    Bicycle(String name, String make, String type, int availableCount) {
        this.name = name;
        this.make = make;
        this.type = type;
        this.availableCount = availableCount;
    }

    public String toString() {
        return name + " (" + make + ", " + type + ") - Available: " + availableCount;
    }
}

class User {
    String name;
    int id;
    Bicycle[] borrowedBicycles = new Bicycle[2];

    User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    // Count how many bicycles the user has borrowed
    int getBorrowedCount() {
        int count = 0;
        for (Bicycle b : borrowedBicycles) {
            if (b != null) count++;
        }
        return count;
    }
}

public class BicycleManagementSystem {
    static Bicycle[][] bicycles = new Bicycle[2][10];
    static int bicycleCount = 0; // Track total number of bicycles added
    static Map<Integer, User> users = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        prepopulate(); // Prepopulate bicycles

        while (true) {
            System.out.println("\nE-Hailing Bicycle Management System");
            System.out.println("1. Add Bicycle");
            System.out.println("2. View All Bicycles");
            System.out.println("3. Borrow Bicycle");
            System.out.println("4. Return Bicycle");
            System.out.println("5. View Borrowed Bicycles");
            System.out.println("6. Search Bicycle");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addBicycle();
                case 2 -> viewBicycles();
                case 3 -> borrowBicycle();
                case 4 -> returnBicycle();
                case 5 -> displayBorrowedBicycles();
                case 6 -> searchBicycle();
                case 7 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void prepopulate() {
        // Add default bicycles to the array
        addToArray(new Bicycle("Marlin 7", "Trek", "Mountain Bike", 10));
        addToArray(new Bicycle("Legion L100", "Mongoose", "BMX Bike", 10));
        addToArray(new Bicycle("RadCity 5 Plus", "Rad Power Bikes", "Electric Bike", 10));
        addToArray(new Bicycle("Escape 3", "Giant", "Hybrid Bike", 10));
        addToArray(new Bicycle("Do mane AL 2", "Trek", "Road Bike", 10));
    }

    static void addBicycle() {
        // Add a new bicycle to the system
        System.out.print("Enter bicycle name: ");
        String name = scanner.nextLine();
        System.out.print("Enter make: ");
        String make = scanner.nextLine();
        System.out.print("Enter type: ");
        String type = scanner.nextLine();
        System.out.print("Enter quantity available: ");
        int count = scanner.nextInt();
        scanner.nextLine();

        Bicycle newBike = new Bicycle(name, make, type, count);
        if (addToArray(newBike)) {
            System.out.println("Bicycle added successfully!");
        } else {
            System.out.println("No space left to add new bicycle.");
        }
    }

    // Adds a bicycle to the 2D array if there's space
    static boolean addToArray(Bicycle b) {
        if (bicycleCount >= 20) return false;

        int row = bicycleCount / 10;
        int col = bicycleCount % 10;
        bicycles[row][col] = b;
        bicycleCount++;
        return true;
    }

    static void viewBicycles() {
        System.out.println("\nAll Bicycles:");
        for (int i = 0; i < bicycles.length; i++) {
            for (int j = 0; j < bicycles[i].length; j++) {
                if (bicycles[i][j] != null) {
                    System.out.println(bicycles[i][j]);
                }
            }
        }
    }

    static void borrowBicycle() {
        // Prompt user for their name
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();

        // Find existing user or create new one
        User user = getOrCreateUser(userName);

        if (user.getBorrowedCount() >= 2) {
            System.out.println("You have already borrowed the maximum number of bicycles.");
            return;
        }

        System.out.println("\nAvailable Bicycles:");
        for (int i = 0; i < bicycles.length; i++) {
            for (int j = 0; j < bicycles[i].length; j++) {
                Bicycle b = bicycles[i][j];
                if (b != null && b.availableCount > 0) {
                    System.out.println("- " + b.name + " (" + b.availableCount + " available)");
                }
            }
        }

        System.out.print("Enter bicycle name to borrow: ");
        String name = scanner.nextLine();

        System.out.print("How many bicycles would you like to borrow (1 or 2)? ");
        int borrowCount = scanner.nextInt();
        scanner.nextLine();

        if (borrowCount < 1 || borrowCount > 2) {
            System.out.println("Invalid number of bicycles to borrow.");
            return;
        }

        int borrowed = 0;
        for (int i = 0; i < bicycles.length && borrowed < borrowCount; i++) {
            for (int j = 0; j < bicycles[i].length && borrowed < borrowCount; j++) {
                Bicycle b = bicycles[i][j];
                if (b != null && b.name.equalsIgnoreCase(name) && b.availableCount > 0) {
                    for (int k = 0; k < 2; k++) {
                        if (user.borrowedBicycles[k] == null) {
                            user.borrowedBicycles[k] = b;
                            b.availableCount--;
                            borrowed++;
                            break;
                        }
                    }
                }
            }
        }

        if (borrowed > 0) {
            System.out.println("You borrowed " + borrowed + " bicycle(s) successfully!");
        } else {
            System.out.println("Unable to borrow. Bicycle may be unavailable.");
        }
    }

    static void returnBicycle() {
        // Return a borrowed bicycle
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();

        User user = getUserByName(userName);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("Borrowed Bicycles:");
        for (int i = 0; i < 2; i++) {
            if (user.borrowedBicycles[i] != null) {
                System.out.println((i + 1) + ". " + user.borrowedBicycles[i].name);
            }
        }

        System.out.print("Enter the number of the bicycle to return: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice >= 1 && choice <= 2 && user.borrowedBicycles[choice - 1] != null) {
            user.borrowedBicycles[choice - 1].availableCount++;
            user.borrowedBicycles[choice - 1] = null;
            System.out.println("Bicycle returned successfully!");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // Retrieve an existing user or create one if not found
    static User getOrCreateUser(String userName) {
        for (User u : users.values()) {
            if (u.name.equalsIgnoreCase(userName)) return u;
        }

        int id = 1000 + new Random().nextInt(9000);
        User newUser = new User(userName, id);
        users.put(id, newUser);
        System.out.println("User created with ID: " + id);
        return newUser;
    }

    static User getUserByName(String userName) {
        for (User u : users.values()) {
            if (u.name.equalsIgnoreCase(userName)) {
                return u;
            }
        }
        return null;
    }

    static void displayBorrowedBicycles() {
        // Display all users who have borrowed bicycles
        for (User user : users.values()) {
            boolean hasBikes = false;
            for (Bicycle b : user.borrowedBicycles) {
                if (b != null) {
                    if (!hasBikes) {
                        System.out.println(user.name + " (ID: " + user.id + ") has borrowed:");
                        hasBikes = true;
                    }
                    System.out.println(" - " + b.name);
                }
            }
        }
    }

    static void searchBicycle() {
        // Search for a bicycle by name
        System.out.print("Enter bicycle name to search: ");
        String name = scanner.nextLine();

        for (int i = 0; i < bicycles.length; i++) {
            for (int j = 0; j < bicycles[i].length; j++) {
                Bicycle b = bicycles[i][j];
                if (b != null && b.name.equalsIgnoreCase(name)) {
                    System.out.println(b);
                    return;
                }
            }
        }

        System.out.println("Bicycle not found.");
    }
}
