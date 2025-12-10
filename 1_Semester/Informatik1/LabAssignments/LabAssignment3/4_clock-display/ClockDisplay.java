/**
 * The ClockDisplay class implements a digital clock display for a
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
public class ClockDisplay
{
    private NumberDisplay hours;
    private NumberDisplay minutes;
    private String displayString;    // simulates the actual display
    private int alarmMinutes;
    private int alarmHours;
    private boolean alarmStatus;
    
    /**
     * Constructor for ClockDisplay objects. This constructor 
     * creates a new clock set at 00:00.
     */
    public ClockDisplay()
    {
        hours = new NumberDisplay(24);
        minutes = new NumberDisplay(60);
        alarmStatus = false;
        updateDisplay();
    }

    /**
     * Constructor for ClockDisplay objects. This constructor
     * creates a new clock set at the time specified by the 
     * parameters.
     */
    public ClockDisplay(int hour, int minute)
    {
        hours = new NumberDisplay(24);
        minutes = new NumberDisplay(60);
        alarmStatus = false;
        setTime(hour, minute);
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
        updateDisplay();
        checkAlarm();
    }

    /**
     * Set the time of the display to the specified hour and
     * minute.
     */
    public void setTime(int hour, int minute)
    {
        hours.setValue(hour);
        minutes.setValue(minute);
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
     * Set the alarm in the format HH:MM.
     */
    public void setAlarm(int hours, int minutes, boolean onOff)
    {
        alarmMinutes = minutes;
        alarmHours = hours;
        alarmStatus = onOff;
    }
    
    public void checkAlarm()
    {
        if(alarmMinutes == minutes.getValue() && alarmHours == hours.getValue() && alarmStatus == true)
        {
            System.out.println("Riiiiiing!");
        }
    }
    
    /**
     * Update the internal string that represents the display.
     */
    private void updateDisplay()
    {
          int hours12 = hours.getValue();
          int minute = minutes.getValue();
          
          String timeframe;
          if(hours12 < 12)
          {
            timeframe = "AM"; 
          } else {
            timeframe = "PM";
          }
          
          hours12 = hours12 % 12;
          if (hours12 == 0)
          {
              hours12 = 12;
          }
          
          String hoursString;
          String minutesString;
          if(hours12 < 10) 
          { 
              hoursString = "0" + hours12; 
            } else {
                hoursString = "" + hours12;
            }
          if(minute < 10) 
          {
            minutesString = "0" + minute;
            } else {
            minutesString = "" + minute;
            }
            
          displayString = hoursString + ":" + minutesString + " " + timeframe;  
    }
}
