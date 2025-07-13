/**
 * TicketMachine models a naive ticket machine that issues
 * flat-fare tickets.
 * The price of a ticket is specified via the constructor.
 * It is a naive machine in the sense that it trusts its users
 * to insert enough money before trying to print a ticket.
 * It also assumes that users enter sensible amounts.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class TicketMachine
{
    // The price of a ticket from this machine.
    private int price;
    // The amount of money entered by a customer so far.
    private int balance;
    // The total amount of money collected by this machine.
    private int total;
    private int ticketAmount;

    /**
     * Create a machine that issues tickets of the given price.
     * Note that the price must be greater than zero, and there
     * are no checks to ensure this.
     */
    public TicketMachine(int ticketCost)
    {
        if (ticketCost <= 0)
        {
            System.out.println("not valid");
            //terminate the current program, status code = 0 indicates termination completed, non-zero = abnormal termination
            System.exit(0);
        }
        else
        {
        price = ticketCost;
        balance = 0;
        total = 0;
        }
    }

    /**
     * Return the price of a ticket.
     */
    public int getPrice()
    {
         return price;
    }

    /**
     * Return the amount of money already inserted for the
     * next ticket.
     */
    public int getAmount()
    {
        return balance;
    }
    
    public int getTotal()
    {
        return total;
    }
    
    /** * reduce price by the given amount. 
    */ 
    public void discount (int amount) 
    { 
         price = price - amount;
    }

    /**
     * Receive an amount of money from a customer.
     */
    public void insertMoney(int amount)
    {
        if ( amount > 0)
        {
            balance = balance + amount;
        }
        else 
        {
            System.out.println("Not enough money!");
        }
    }
    
    public void prompt()
    {
        System.out.println("Please insert the correct amount of money.");
    }
    
    public void showPrice()
    {
         System.out.println("The price of the ticket is" + price + " cents.");
    }
    
    public int showTicketAmount()
    {
        return ticketAmount;
    }
    
    public void empty()
    // to set total to zero
    {
        total = 0;
    }

    /**
     * Print a ticket.
     * Update the total collected and
     * reduce the balance to zero.
     */
    public void printTicket()
    {
        if(balance >= price)
        {
            // Simulate the printing of a ticket.
            System.out.println("##################");
            System.out.println("# The BlueJ Line");
            System.out.println("# Ticket");
            System.out.println("# " + price + " cents.");
            System.out.println("##################");
            System.out.println();

            // Update the total collected with the balance.
            total = total + price;
            balance = balance - price;
            ticketAmount += 1;
        }
        else 
        {
            System.out.println("Not enough money");
        }
    }
}