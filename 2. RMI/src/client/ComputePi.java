package client;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import compute.Compute;
import java.util.Scanner;

public class ComputePi {
    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            Registry registry = LocateRegistry.getRegistry(args[0]);
            Compute comp = (Compute) registry.lookup(name);
            //loop for input
            Scanner input = new Scanner(System.in);
            int inputNum;
            while(true) {
                System.out.println("please input your action: 1-PI; 2-Prime; 3-Exit.");
                inputNum = input.nextInt();

                if(inputNum==3){
                    break;
                }
                else if(inputNum==1){
                    Pi task = new Pi(45);
                    BigDecimal pi = comp.executeTask(task);
                    System.out.println("result is:" + pi.toString());
                }
                else if(inputNum==2){
                    System.out.println("please input min:");
                    int min = input.nextInt();
                    System.out.println("please input max:");
                    int max = input.nextInt();
                    Prime task = new Prime(min, max);
                    String strPrimeNum = comp.executeTask(task);
                    System.out.println("Prime Numbers are:" + strPrimeNum);
                }
            }

        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
}
