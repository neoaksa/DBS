package client;


import compute.Task;

import java.io.Serializable;


public class Prime implements Task<String>, Serializable{

    private static final long serialVersionUID = 227L;

    private int max;
    private int min;
    // generate prime number between min and max
    public Prime(int max, int min) {
        this.max = max;
        this.min = min;
    }

    // get the prime number
    public String execute() {
        String primeNumbers = "";
        for (int i = this.min; i <= this.max; i++)
        {
            int counter=0;
            for(int num =i; num>=1; num--)
            {
                if(i%num==0)
                {
                    counter = counter + 1;
                }
            }
            if (counter ==2)
            {
                //Appended the Prime number to the String
                primeNumbers = primeNumbers + String.valueOf(i) + " ";
            }
        }
        return primeNumbers;
    }
}
