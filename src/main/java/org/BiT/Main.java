package org.BiT;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Random;

// Программа представляет собой аналог оптимизационного калькулятора на тематику "Герои меча и магии 3"
// Ознакомьтесь с меню и выберите вариант калькуляции
public class Main {
    // Technical part
    static Scanner scanner = new Scanner(System.in);
    static Task taskInteger;
    static Task taskFloat;

    public static void main(String[] args) throws InterruptedException {
        fillTasks();

        switch (menu()) {
            case 1:
                gameProceed(); // play the game and input your own data
                break;
            case 2:
                Simplex simplex = new Simplex(taskFloat); // choose taskInteger or taskFloat
                simplex.optimize();
                break;
            case 3:
                BranchAndBound branchAndBound = new BranchAndBound(taskFloat); // choose taskInteger or taskFloat
                branchAndBound.optimize();
                break;
            case 4:
                GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm(taskFloat); // choose taskInteger or taskFloat
                greedyAlgorithm.optimize();
                break;
            default:
                System.out.println("Such menu point does not exist");
        }
        System.out.println("\nEXIT");
    }

    public static int menu() {
        System.out.println("\nPlease choose the way to proceed:");
        System.out.println("1. Start the game");
        System.out.println("2. Testing simplex");
        System.out.println("3. Testing Branch and Bounds method");
        System.out.println("4. Testing Greedy Algorithm");

        int point = scanner.nextInt();
        scanner.nextLine();
        return point;
    }

    public static void fillTasks() {
        // Test data 1 (целочисленное симплекс решение)
        double[] unitsPower1 = {3, 1, 2}; // swordsmen, magicians and dragons
        double[][] costs1 = {
                {1, 1, 3},  // GOLD costs for swordsmen, magicians and dragons
                {2, 2, 5},  // PRECIOUS STONES costs for swordsmen, magicians and dragons
                {4, 1, 2}}; // CRYSTALS costs for swordsmen, magicians and dragons
        double[] resources1 = {30, 24, 36}; // Total resources for GOLD, PRECIOUS STONES and CRYSTALS
        taskInteger = new Task(costs1, resources1, unitsPower1);

        // Test data 2 (симплекс решение с плавающей точкой)
        double[] unitsPower2 = {3, 7, 27}; // swordsmen, magicians and dragons
        double[][] costs2 = {
                {4, 4, 22},  // GOLD costs for swordsmen, magicians and dragons
                {3, 7, 12},  // PRECIOUS STONES costs for swordsmen, magicians and dragons
                {3, 7, 10}}; // CRYSTALS costs for swordsmen, magicians and dragons
        double[] resources2 = {120, 170, 241}; // Total resources for GOLD, PRECIOUS STONES and CRYSTALS
        taskFloat = new Task(costs2, resources2, unitsPower2);
    }

    public static void gameProceed() throws InterruptedException {
        // Introduction
        String hommArt =
                        "  __    __  ________  __      __  __      __   _______  _______  _______    \n" +
                        " |  |  |  ||   __   ||  \\    /  ||  \\    /  | |_     _||_     _||_     _|  \n" +
                        " |  |__|  ||  |  |  ||   \\  /   ||   \\  /   |   |   |    |   |    |   |    \n" +
                        " |   __   ||  |  |  ||    \\/    ||    \\/    |   |   |    |   |    |   |    \n" +
                        " |  |  |  ||  |__|  ||  |\\__/|  ||  |\\__/|  |  _|   |_  _|   |_  _|   |_   \n" +
                        " |__|  |__||________||__|    |__||__|    |__| |_______||_______||_______|   \n";
        System.out.println(hommArt);
        System.out.println("Welcome to Heroes Might and Magic III");
        System.out.println("Press \"ENTER\" to continue...");
        scanner.nextLine();
        System.out.println("Your starting army can be swordsmen, magicians, and dragons");
        TimeUnit.SECONDS.sleep(3);
        System.out.println("But...");
        TimeUnit.SECONDS.sleep(2);
        // Getting sources info from player
        System.out.println("You have limited resources yet");
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Please look in your pockets and advise how much resources you have");
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Input amount of GOLD:");
        taskFloat.resources[0] = scanner.nextInt(); // GOLD
        System.out.println("Input amount of PRECIOUS STONES:");
        taskFloat.resources[1] = scanner.nextInt(); // PRECIOUS STONES
        System.out.println("Input amount of CRYSTALS:");
        taskFloat.resources[2] = scanner.nextInt(); // CRYSTALS
        // Getting power of units from player
        System.out.println("\nNow choose which fraction the creatures belong to and write their power");
        System.out.println("Input power of swordsman:");
        taskFloat.unitsPower[0] = scanner.nextInt(); // power of swordsman
        System.out.println("Input power of magician:");
        taskFloat.unitsPower[1] = scanner.nextInt(); // power of magician
        System.out.println("Input power of dragon:");
        taskFloat.unitsPower[2] = scanner.nextInt(); // power of dragon
        // Getting costs of units from player
        System.out.println("\nYour creatures also have costs per unit");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("For every creature you have to pay some GOLD and/or some PRECIOUS STONES and/or CRYSTALS");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Input costs of swordsman (Format: GOLD,PRECIOUS_STONES,CRYSTALS) :");
        String inputCostSwordsman = scanner.next();
        String[] splitInputCostSwordsman = inputCostSwordsman.split(",");
        for (int i = 0; i < splitInputCostSwordsman.length; i++){
            taskFloat.costs[i][0] = Integer.parseInt(splitInputCostSwordsman[i]); // swordsman costs
        }
        System.out.println("Input costs of magician (Format: GOLD,PRECIOUS_STONES,CRYSTALS) :");
        String inputCostMagician = scanner.next();
        String[] splitInputCostMagician = inputCostMagician.split(",");
        for (int i = 0; i < splitInputCostMagician.length; i++){
            taskFloat.costs[i][1] = Integer.parseInt(splitInputCostMagician[i]); // magician costs
        }
        System.out.println("Input costs of dragon (Format: GOLD,PRECIOUS_STONES,CRYSTALS) :");
        String inputCostDragon= scanner.next();
        String[] splitInputCostDragon = inputCostDragon.split(",");
        for (int i = 0; i < splitInputCostDragon.length; i++){
            taskFloat.costs[i][2] = Integer.parseInt(splitInputCostDragon[i]); // dragon costs
        }
        // Calculation
        System.out.println("\nGreat!");
        System.out.println("Here is calculation of your optimal set of army:\n");
        TimeUnit.SECONDS.sleep(2);
        System.out.println("------------------------------------------------");
        TimeUnit.SECONDS.sleep(1);
        BranchAndBound branchAndBound = new BranchAndBound(taskFloat);
        int ownPower = (int) branchAndBound.optimize();
        System.out.println("------------------------------------------------\n");
        // Comparison of strength
        System.out.println("Your power is: " + ownPower);
        TimeUnit.SECONDS.sleep(1);
        Random rnd = new Random(System.currentTimeMillis());
        int minEnemyPower = 30; // you can change the min power of enemy here
        int maxEnemyPower = 200; // you can change the max power of enemy here
        int enemyPower = minEnemyPower + rnd.nextInt(maxEnemyPower - minEnemyPower + 1);
        System.out.println("Power of your enemy is: " + enemyPower + "\n");
        TimeUnit.SECONDS.sleep(1);
        if (ownPower > enemyPower) {
            System.out.println("You WON this fight! You were stronger!");
        } else if (ownPower == enemyPower) {
            System.out.println("This game was played to a draw!");
        } else {
            System.out.println("You LOST this fight! Your enemy was stronger!");
        }
    }

}
