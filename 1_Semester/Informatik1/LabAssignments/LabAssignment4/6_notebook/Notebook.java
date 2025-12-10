import java.util.ArrayList;

/**
 * A class to maintain an arbitrarily long list of notes.
 * Notes are numbered for external reference by a human user.
 * In this version, note numbers start at 0.
 * 
 * @author David J. Barnes and Michael Kolling.
 * @version 2008.03.30
 */
public class Notebook
{
    // Storage for an arbitrary number of notes.
    private ArrayList<String> notes;

    /**
     * Perform any initialization that is required for the
     * notebook.
     */
    public Notebook()
    {
        notes = new ArrayList<>();
    }
    
    public void exampleNotes()
    {
        notes.add("This is an example.");
        notes.add("1 + 1 = 2");
        notes.add("Hello there!");
    }

    /**
     * Store a new note into the notebook.
     * @param note The note to be stored.
     */
    public void storeNote(String note)
    {
        notes.add(note);
    }
    
    public void removeNote(int noteNumber)
    {
        if(noteNumber >= 0 && noteNumber < notes.size())
        {
            notes.remove(noteNumber); 
        } else {
            System.out.println("Enter a valid number!");
        }
    }

    /**
     * @return The number of notes currently in the notebook.
     */
    public int numberOfNotes()
    {
        return notes.size();
    }
    
    public void searchNote(String note)
    {
            if(notes.contains(note))
            {
                System.out.println(note);
            } else {
                System.out.println("Search term not found");
            }
    }
    
    public void searchNoteFor(String note)
    {
        boolean found = false;
        
        for(int i = 0; i < notes.size(); i++)
        {
            //check if the current index is the same as the search term
            if(notes.get(i) == note)
            {
                System.out.println("Found: " + note);
                found = true;
            }
        }
        if(!found)
            {
                System.out.println("Search term not found");
            }
    }
    
    public void searchNoteForEach(String note)
    {
        boolean found = false;
        
        for(String notes : notes)
        {
            //check if the current index is the same as the search term
            if(notes == note)
            {
                System.out.println("Found: " + note);
                found = true;
            }
        }
        if(!found)
            {
                System.out.println("Search term not found");
            }
    }
    
    public void searchNoteWhile(String note)
    {
        int i = 0;
        boolean found = false;
        int size = notes.size();
        
        while(i < size)
        {
            if(notes.get(i) == note)
            {
                System.out.println("Found: " + note);
                found = true;
            }
            i++;
        }
        /*
        while(i < notes.size())
        {
            if(notes.get(i) == note)
            {
                System.out.println("Found: " + note);
                found = true;
            }
            i++;
        }
        */
        if(!found)
            {
                System.out.println("Search term not found");
            }
    }

    /**
     * Show a note.
     * @param noteNumber The number of the note to be shown.
     */
    public void showNote(int noteNumber)
    {
        //cannot be bigger than size because index starts at 0 not 1
        if(noteNumber >= 0 && noteNumber < notes.size())
        {
            //index number given in the parameter + get method to get the string in the ArrayList
            System.out.println( noteNumber + " " + notes.get(noteNumber));
        } else {
            System.out.println("Enter a valid number!");
        }
    }
}
