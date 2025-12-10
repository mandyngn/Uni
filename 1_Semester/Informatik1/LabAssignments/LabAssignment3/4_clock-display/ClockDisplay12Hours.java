
/**
 * The ClockDisplay12Hours class implements a digital clock display for a
 * European-style 24 hour clock. The clock shows hours and minutes. The 
 * range of the clock is 00:00 (midnight) to 23:59 (one minute before 
 * midnight).
 * 
 * The clock display receives "ticks" (via the timeTick method) every minute
 * and reacts by incrementing the display. This is done in the usual clock
 * fashion: the hour increments when the minutes roll over to zero.
 * 
 * @author Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */
public class ClockDisplay12Hours
{
    private NumberDisplay hours;
    private NumberDisplay minutes;
    private String displayString;    // simulates the actual display
    private boolean isAM;
    
    /**
     * Constructor for ClockDisplay12Hours objects. This constructor 
     * creates a new clock set at 00:00.
     */
    public ClockDisplay12Hours()
    {
        hours = new NumberDisplay(13);
        minutes = new NumberDisplay(60);
        hours.setValue(12);
        isAM = true;
        updateDisplay();
    }

    /**
     * Constructor for ClockDisplay12Hours objects. This constructor
     * creates a new clock set at the time specified by the 
     * parameters.
     */
    public ClockDisplay12Hours(int hour, int minute, boolean isAM)
    {
        hours = new NumberDisplay(13);
        minutes = new NumberDisplay(60);
        setTime(hour, minute, isAM);
    }

    /**
     * This method should get called once every minute - it makes
     * the clock display go one minute forward.
     */
    public void timeTick()
    {
        minutes.increment();
        if(minutes.getValue() == 0) {  // it just rolled over!
            hours.increment();
        } 
        if(hours.getValue() == 0)
        {
            hours.setValue(1); //there is no 0 pm or am
        }
        if(hours.getValue() == 12)
        {
            isAM = !isAM; 
        }
        updateDisplay();
    }

    /**
     * Set the time of the display to the specified hour and
     * minute.
     */
    public void setTime(int hour, int minute, boolean isAM)
    {
        if(hour < 1 || hour > 12)
        {
            System.exit(0);
        }
        hours.setValue(hour);
        minutes.setValue(minute);
        this.isAM = isAM;
        updateDisplay();
    }

    /**
     * Return the current time of this display in the format HH:MM.
     */
    public String getTime()
    {
        return displayString;
    }
    
    /**
     * Update the internal string that represents the display.
     */
    private void updateDisplay()
    {
          String hoursString;
          String minutesString;
          if(hours.getValue() < 10) 
          { 
              hoursString = "0" + hours.getValue(); 
            } else {
                hoursString = "" + hours.getValue();
            }
          if(minutes.getValue() < 10) 
          {
            minutesString = "0" + minutes.getValue();
            } else {
            minutesString = "" + minutes.getValue();
            }
            
          if(isAM == true)
          {
              displayString = hoursString + ":" + minutesString + " AM";
            } else {
                displayString = hoursString + ":" + minutesString + " PM";
            }
    }
}
