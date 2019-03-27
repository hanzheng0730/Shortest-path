

import java.io.*;
import java.util.Scanner;

public class Airline{
    private static Scanner reader;  // read system.in
    private static RouteGraph rg;   // the edge weighted graph storing all routes information
  
    /* Show the entire list of direct routes, distances and prices. */
    private static void showRoutes(){
        System.out.println("---------- All Direct Routes ------------");
        rg.list();
        System.out.print("Press Enter to continue...");
        reader.nextLine();
    }

    /* Display a minimum spanning tree for the service routes based on distances. */
    private static void showMST(){
        System.out.println("---------- Minimum Spanning Tree ------------");
        rg.getMST();
        System.out.print("Press Enter to continue...");
        reader.nextLine();
    }
    
    /* Search for "shortest path" based on distance, price and number of hops. */
    private static void showSP(){
        System.out.println("---------- Shortest Path ------------");
        System.out.println("Please enter source city (e.g. Pittsburgh):");
        String scity = reader.nextLine();
        System.out.println("Please enter destination city (e.g. NYC):");
        String dcity = reader.nextLine();
        System.out.println("Shortest path based on:");
        System.out.println("1. Total miles");
        System.out.println("2. Price");
        System.out.println("3. Number of hops");
        System.out.println("Please enter your option number [1-3]:");

        // read user input
        int option = 0;
        try{
            option = Integer.parseInt(reader.nextLine());
            if(rg.hasCity(scity) && rg.hasCity(dcity)){
                switch (option){
                    case 1: System.out.println("\nShortest path by distance:\n");
                            rg.getDistanceSP(scity,dcity); break;
                    case 2: System.out.println("\nShortest path by price:\n");
                            rg.getPriceSP(scity,dcity); break;
                    case 3: System.out.println("\nShortest path by hops:\n");
                            rg.getHopsSP(scity,dcity); break;
                    default: System.out.println("\nInvalid input!\n"); break;
                }
                System.out.print("Press Enter to continue...");
                reader.nextLine();
            } else {
                System.out.print("No city matched. Press Enter to continue...");
                reader.nextLine();
            }
        } catch(NumberFormatException e){
            System.out.print("Please input a number! Press Enter to continue...");
            reader.nextLine();
        }
    }
    
    /* Print out all trips cost X or less, where X is entered by the user */
    private static void showAffordable(){
        System.out.println("---------- Affordable trips ------------");
        System.out.println("Please enter your a dollar amount (e.g. 500):");
        
        // read user input
        double x = 0;
        try{
            x = Double.parseDouble(reader.nextLine());
            System.out.println("Trips cost less than or equal to $"+x+":");
            rg.getAffordable(x);
            System.out.print("Press Enter to continue...");
            reader.nextLine();
        } catch(NumberFormatException e){
            System.out.print("Please input a number! Press Enter to continue...");
            reader.nextLine();
        }
        
    }
    
    /* Add a new route to the schedule. Assume that both cities already exist, and the user enters the vertices, distance, and price for the new route. */
    
    private static void addRoute(){
        System.out.println("---------- Add Route ------------");
        System.out.println("Please enter source city (e.g. Pittsburgh):");
        String scity = reader.nextLine();
        System.out.println("Please enter destination city (e.g. NYC):");
        String dcity = reader.nextLine();
        
        try{
            System.out.println("Please enter distance in miles (e.g. 666):");
            double distance = Double.parseDouble(reader.nextLine());
            System.out.println("Please enter price (e.g. 500):");
            double price = Double.parseDouble(reader.nextLine());
            if(rg.hasCity(scity) && rg.hasCity(dcity)){
                if(rg.add(scity,dcity,distance,price))
                    System.out.println("Successfully added a route from "+scity+" to "+dcity+"!");
                else System.out.println("Failed: a route from "+scity+" to "+dcity+" already exists!");
                System.out.print("Press Enter to continue...");
                reader.nextLine();
            } else {
                System.out.print("No city matched. Press Enter to continue...");
                reader.nextLine();
            }
        } catch(Exception e){
            System.out.print("Please input a number! Press Enter to continue...");
            reader.nextLine();
        }
    }

    /* Remove a route from the schedule. */
    private static void removeRoute(){
        System.out.println("---------- Remove Route ------------");
        System.out.println("Please enter source city (e.g. Pittsburgh):");
        String scity = reader.nextLine();
        System.out.println("Please enter destination city (e.g. NYC):");
        String dcity = reader.nextLine();
        
        if(rg.hasCity(scity) && rg.hasCity(dcity)){
            if(rg.remove(scity,dcity))
                System.out.println("Successfully removed a route from "+scity+" to "+dcity+"!");
            else System.out.println("Failed: a route from "+scity+" to "+dcity+" does exist!");
            System.out.print("Press Enter to continue...");
            reader.nextLine();
        } else {
            System.out.print("No city matched. Press Enter to continue...");
            reader.nextLine();
        }
    }
    
    /* Quit the program and save routes back to a file. */
    private static void saveData(String filename){
        System.out.println("---------- Quit and Save ------------");
        System.out.print("Do you want to overwrite "+filename+"? [y/n]");
        
        // ask user to input a new file name
        String outfile = filename;
        if (!reader.nextLine().toUpperCase().equals("Y")){
            System.out.println("Please enter output file name (e.g. j-air.txt):");
            outfile=reader.nextLine();
        }
        rg.write(outfile);
        System.out.println("Successfully save the routes data!");
    }

    
    /* Main */
    public static void main(String[] args){
        boolean exit=false;                   // flag for exit the interface
        reader = new Scanner(System.in);      // initialize input reader
        rg = new RouteGraph();                // initialize the route graph
        
        System.out.println("===================== Welcome to J Airline ===================\n");
        System.out.println("Please enter a filename containing all routes (e.g. data.txt):");
        String filename = reader.nextLine();  // read filename
        rg.load(filename);                    // load the information in the input file

        while(!exit){
            int option = 0; // user option
            
            // print main menu
            System.out.println("========================= Menu ===========================");
            System.out.println("1. Display all direct routes");
            System.out.println("2. Show a minimum spanning tree based on distances");
            System.out.println("3. Find shortest path");
            System.out.println("4. Show trips cost X or less");
            System.out.println("5. Add a new route");
            System.out.println("6. Remove a route");
            System.out.println("7. Quit the program");
            System.out.println("Please enter your option number [1-7]:");

            // read user input
            try{
                option = Integer.parseInt(reader.nextLine());
            } catch(Exception e){
                System.out.print("Please input a number! Press Enter to continue...");
                reader.nextLine();
            }
            
            switch (option){
                case 1: showRoutes(); break;
                case 2: showMST(); break;
                case 3: showSP(); break;
                case 4: showAffordable(); break;
                case 5: addRoute(); break;
                case 6: removeRoute(); break;
                case 7: saveData(filename); exit = true; System.out.println("\nHave a nice day!\n"); break;
                default: System.out.println("\nInvalid input!\n"); break;
            }
        }
    }
}
