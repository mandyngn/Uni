/**
 * The test class DayTest.
 *
 * @author  David J. Barnes and Michael Kolling
 * @version 2008.03.30
 */
public class DayTest extends junit.framework.TestCase
{
    /**
     * Default constructor for test class DayTest
     */
    public DayTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    protected void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    protected void tearDown()
    {
    }

    /**
     * Test basic functionality by booking at either end
     * of a day, and in the middle.
     */
    public void testMakeThreeAppointments()
    {
        Day day1 = new Day(1);
        Appointment appointm1 = new Appointment("Java lecture", 1);
        Appointment appointm2 = new Appointment("Java class", 1);
        Appointment appointm3 = new Appointment("Meet John", 1);
        assertEquals(true, day1.makeAppointment(9, appointm1));
        assertEquals(true, day1.makeAppointment(13, appointm2));
        assertEquals(true, day1.makeAppointment(17, appointm3));
    }

    /**
     * Check that double-booking is not permitted.
     */
    public void testDoubleBooking()
    {
        Day day1 = new Day(1);
        Appointment appointm1 = new Appointment("Java lecture", 1);
        Appointment appointm2 = new Appointment("Error", 1);
        assertEquals(true, day1.makeAppointment(9, appointm1));
        assertEquals(false, day1.makeAppointment(9, appointm2));
    }

	public void testFindSpaceEmpty()
	{
		Day day2 = new Day(13);
		day2.showAppointments();
		Appointment a = new Appointment("First", 1);
		assertEquals(9, day2.findSpace(a));
	}

	public void testFindSpace9()
	{
		Day day1 = new Day(13);
		Appointment a = new Appointment("first", 1);
		assertEquals(9, day1.findSpace(a));
		day1.showAppointments();
	}

	public void testNotFull()
	{
		Day day1 = new Day(13);
		Appointment appointm1 = new Appointment("first!", 1);
		assertEquals(true, day1.makeAppointment(11, appointm1));
		Appointment appointm2 = new Appointment("second", 1);
		assertEquals(9, day1.findSpace(appointm2));
		day1.showAppointments();
		Appointment b3 = new Appointment("BIg", 3);
		assertEquals(12, day1.findSpace(b3));
	}
}





