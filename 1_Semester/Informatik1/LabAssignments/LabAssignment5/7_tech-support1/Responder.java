import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response to an input string.
 * 
 * @author     Michael Kölling and David J. Barnes
 * @version    0.1 (2016.02.29)
 */
public class Responder
{
    private ArrayList<String> responses;
    private Random randomGenerator;
    private HashMap<String, String> moreResponses;
    
    /**
     * Construct a Responder - nothing to do
     */
    public Responder()
    {
        randomGenerator = new Random();
        responses = new ArrayList<>();
        moreResponses = new HashMap<>();
        fillResponses();
        fillHashMap();
    }

    /**
     * Fill the responses ArrayList with Strings
     */
    public void fillResponses()
    {
        responses.add("Hello!");
        responses.add("How can I help you?");
        responses.add("That's interesting.");
        responses.add("What are you even saying?");
        responses.add("No.");
        responses.add("Yes.");
        responses.add("Perhaps..");
        responses.add("No, thank you");
        responses.add("Erm...okay?");
    }
    
    /**
     * Fill the moreResponses HashMap with key and value Strings.
     */
    public void fillHashMap()
    {
        moreResponses.put("Hello", "Hello, how are you?");
        moreResponses.put("Good", "I'm glad to hear that");
        moreResponses.put("Bad", "What's going on?");
        moreResponses.put("No", "Why not?");
        moreResponses.put("Yes", "Nice");
        moreResponses.put("Help", "That's my job. What do you need?");
    }
    
    /**
     * Generate a Response with input. 
     * Checks if the given HashSet type String contains a key of the HashMap. Else random response.
     * 
     * @param HashSet type String from the user or program
     * @return String, value of the HashMap or random Response
     */
    public String generateResponseHashSet(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        String value = "";
        
        while(it.hasNext())
        {
            String text = it.next();
            if(moreResponses.containsKey(text))
            {
                 value = moreResponses.get(text);
                 break;
            } else {
                value = generateResponse();
            }
        }
        
        //testing print to console 
        //System.out.println(value);
        return value;
    }
    
    /**
     * Generate a response with input.
     * Checks if the given String contains a key of the HashMap.
     * 
     * @param String for the key
     * @return String of the key value
     */
    public String generateResponseHashMap(String term)
    {
        return moreResponses.get(term);
    }
    
    /**
     * Generate a response.
     * @return   A string that should be displayed as the response
     */
    public String generateResponse()
    {
        int index = randomGenerator.nextInt(responses.size() - 1);
        return responses.get(index);
    }
}
