package PJ3;

import java.util.*;
import java.io.*;
import java.io.File;
import java.io.IOException;

// You may add new functions or data in this class
// You may modify any functions or data members here
// You must use Customer, Teller and ServiceArea classes
// to implement Bank simulator

class BankSimulator {

    // input parameters
    private int numTellers, customerQLimit;
    private int simulationTime, dataSource;
    private int chancesOfArrival, maxTransactionTime;

    // statistical data
    private int numGoaway, numServed, totalWaitingTime;

    // internal data
    private int customerIDCounter;   // customer ID counter
    private ServiceArea servicearea; // service area object
    private Scanner dataFile;	   // get customer data from file
    private Random dataRandom;	   // get customer data using random function

    // most recent customer arrival info, see getCustomerData()
    private boolean anyNewArrival;
    private int transactionTime;

    // initialize data fields
    private BankSimulator()
    {
        numServed = 0;
        numGoaway = 0;
        customerIDCounter = 0;
        totalWaitingTime = 0;

    }

    private void setupParameters()
    {
        Scanner input = new Scanner(System.in);
        System.out.print("***Welcome to Simulation*** ");
        while (true){
        System.out.print("Enter Simulation time ");
        simulationTime = input.nextInt();
        if(simulationTime > 10000 || simulationTime < 0){
            System.out.print("not accepted");
            }
        else{
       break;
            }
          }
        while (true){
        System.out.print("Enter the Number of Tellers ");
        numTellers = input.nextInt();
        if(numTellers > 10 || numTellers < 0){
            System.out.print("not accepted");
            }
        else{
       break;
            }
          }
        while (true){
        System.out.print("Enter chances (0% < & <= 100%) of new customer ");
        chancesOfArrival = input.nextInt();
        if(chancesOfArrival > 100 || chancesOfArrival < 0){
            System.out.print("not accepted");
        }
        else{
       break;
            }
          }
        while (true){
        System.out.print("Enter maximum transaction time for customers ");
        maxTransactionTime = input.nextInt();
        if(maxTransactionTime > 500 || maxTransactionTime < 0){
            System.out.print("not accepted");
        }
        else{
       break;
            }
          }
        while (true){
        System.out.print("Enter customer queue limit ");
        customerQLimit = input.nextInt();
        if(customerQLimit > 50 || customerQLimit < 0){
            System.out.print("Not accepted");
        }
        else{
       break;
            }
          }
        while (true){
        System.out.print("Enter 0/1 to get data from Random/file ");
        dataSource = input.nextInt();
        if(dataSource > 1 || dataSource < 0){
            System.out.print("Not accepted");
        }
        else{
            input.close();
            dataRandom = new Random ();
            break;
        }
    }
}
    private void getCustomerData()
    {
        if(dataSource == 0){
        anyNewArrival = ((dataRandom.nextInt(100)+1) <= chancesOfArrival);
        transactionTime = dataRandom.nextInt(maxTransactionTime)+1;
        }
        if(dataSource == 1){
            int data1 = 0;
            int data2 = 0;
            data1 = data2;
        if(dataFile.hasNextInt()){
            data1 = dataFile.nextInt();
            data2 = dataFile.nextInt();
            }
        anyNewArrival = (((data1%100)+1)<= chancesOfArrival);
        transactionTime = (data2%maxTransactionTime)+1;
            }
    }

    private void doSimulation()
    {
        // add statements
        servicearea = new ServiceArea(numTellers, customerQLimit);
        // Initialize ServiceArea

        // Time driver simulation loop
        for (int currentTime = 0; currentTime < simulationTime; currentTime++) {
            System.out.println("---------------------------------------------");
            System.out.println("\tTime :" + (currentTime));
            // Step 1: any new customer enters the bank?
            getCustomerData();

            if (anyNewArrival) {
                customerIDCounter++;
            if(!servicearea.isCustomerQTooLong()){
                System.out.println("\t Customer number " + customerIDCounter + " arrived with transaction time " + transactionTime);
                System.out.println("\t Customer number " + customerIDCounter + " is waiting in the customer queue");
                servicearea.insertCustomerQ(new Customer(customerIDCounter, transactionTime, currentTime));
            }
            else{
                    numGoaway++;
                    }
            } else {
                System.out.println("\tNo new customer!");
            }
            while(servicearea.numFreeTellers() > 0 && servicearea.numWaitingCustomers() > 0){
                numServed++;
                Teller aTeller = servicearea.removeFreeTellerQ();
                Customer customer = servicearea.removeCustomerQ();
                System.out.println("\t Customer Number "+customer.getCustomerID()+" goes to a teller");
                int timeWaited = (currentTime - customer.getArrivalTime());
                totalWaitingTime += timeWaited;
                aTeller.freeToBusy(customer, currentTime);
                servicearea.insertBusyTellerQ(aTeller);

            }
            while(servicearea.numBusyTellers() > 0 && servicearea.getFrontBusyTellerQ().getEndBusyTime() == currentTime){
                Teller aTeller = servicearea.removeBusyTellerQ();
                System.out.println("\t Customer Number "+ aTeller.getCustomer().getCustomerID() +" is done");
                System.out.println("\t Teller Number "+ aTeller.getTellerID() +" is free");
                servicearea.insertFreeTellerQ(aTeller);
            }
            System.out.println("---------------------------------------------");
        } // end simulation loop
        // clean-up - close scanner
    }

    private void printStatistics()
    {
        System.out.println("---------------------------------------------");
        System.out.println();
        System.out.println("Number of total arrival customers :  "+ numServed);
        System.out.println("Number of customers gone-away     :  "+ numGoaway);
        System.out.println("Numbe of customers served         :  "+ numServed);
        System.out.println();
        System.out.print("\t *** Current Tellers Info ***");
        System.out.println();
        System.out.println("Number of waiting customers : "+ servicearea.numWaitingCustomers());
        System.out.println("Number of busy tellers      : "+ servicearea.numBusyTellers());
        System.out.println("Number of free tellers      : "+ servicearea.numFreeTellers());
        System.out.println();
        System.out.print("\t Busy Tellers Info :");
        System.out.println();
        if (!servicearea.emptyBusyTellerQ()){
        while(servicearea.numBusyTellers() > 0){
            Teller aTeller = servicearea.removeBusyTellerQ();
            aTeller.printStatistics();
                }
    }else {
                System.out.println("None");
                }
        System.out.print("\t Free Tellers Info :");
        System.out.println();
        if (!servicearea.emptyFreeTellerQ()){
        while(servicearea.numFreeTellers() > 0){
            Teller aTeller = servicearea.removeFreeTellerQ();
            aTeller.printStatistics();
                }
    }else {
                System.out.println("None");
                }
    }

    public static void main(String[] args) {
        //datasource error//
        BankSimulator runBankSimulator = new BankSimulator();
        runBankSimulator.setupParameters();
        runBankSimulator.doSimulation();
        runBankSimulator.printStatistics();
    }

}
