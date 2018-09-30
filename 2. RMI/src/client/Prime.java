package client;


import compute.Task;
import java.io.Serializable;


public class Prime implements Task<String>, Serializable{

//    private static final long serialVersionUID = 228L;

    private int max;
    private int min;

    // generate prime number between min and max
    public Prime(int min, int max) {
        this.max = max;
        this.min = min;
    }

    // get the prime number
    public String execute() {
        StringBuffer primeNumbers = new StringBuffer(" ");
        int count=0,i,j;
        for(i = this.min; i <= this.max; i++)
        {
            for( j = 2; j < i; j++)
            {
                if(i % j == 0)
                {
                    count = 0;
                    break;
                }
                else
                {
                    count = 1;
                }
            }
            if(count == 1)
            {
                primeNumbers.append(Integer.toString(i));
                primeNumbers.append(" ");
            }
        }
        return primeNumbers.toString();

    }
}
