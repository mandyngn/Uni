/**
 * Write a description of class Lab here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Lab
{
    // instance variables - replace the example below with your own
    /**
     * Constructor for objects of class Lab
     */
    public Lab()
    {
    }

    /**
     * Print an array of numbers to the console
     */
    public static void printArray()
    {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        
        for(int i = 0; i < numbers.length; i++)
        {
            System.out.println(numbers[i]);
        }
    }
    
    /**
     * Takes an array of grades, calculates the average and prints it out to the console
     * 
     * @param array of numbers type double
     */
    public static void calcAverage(double[] grades)
    {
        double average = 0;
        
        for(int i = 0; i < grades.length; i++)
        {
            average += grades[i];
        }
        
        average /= grades.length;
        
        System.out.println(average);
    }
    
    /**
     * Finds the maximum value in an array
     * 
     * @param array with integer numbers
     */
    public static void maxValue(int[] numbers)
    {
        int max = 0;
        
        for(int i = 0; i < numbers.length; i++)
        {
            if(max < numbers[i])
            {
                max = numbers[i];
            }
        }
        
        System.out.println(max);
    }
    
    /**
     * Prints elements of a predefined array in reverse order
     */
    public static void reverseArray()
    {
        int[] numbers = {1, 2, 3, 4, 5, 8, 20};
        
        for(int i = numbers.length - 1; i >= 0; i--)
        {
            System.out.println(numbers[i]);
        }
    }
    
    /**
     * main to call methods
     */
    public static void main(String[] args)
    {
        int[] numbers = {23, 45, 66, 12, 6};
        double[] grades = {4.5, 6.0, 1.6, 2.4, 3.7};
        
        printArray();
        calcAverage(grades);
        maxValue(numbers);
        reverseArray();
    }
}

