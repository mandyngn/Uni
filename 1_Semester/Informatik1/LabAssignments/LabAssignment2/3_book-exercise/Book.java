/**
 * A class that maintains information on a book.
 * This might form part of a larger application such
 * as a library system, for instance.
 *
 * @author (Insert your name here.)
 * @version (Insert today's date here.)
 */
class Book
{
    // The fields.
    private String author;
    private String title;
    private int pages;
    private int refNumber;

    /**
     * Set the author and title fields when this object
     * is constructed.
     */
    public Book(String bookAuthor, String bookTitle, int numberPages)
    {
        author = bookAuthor;
        title = bookTitle;
        pages = numberPages;
        refNumber = 0;
    }

    // Add the methods here ...
    public String getAuthor()
    {
        return author;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void printAuthor()
    {
        System.out.println(author);
    }
    
    public void printTitle()
    {
        System.out.println(title);
    }
    
    public int getPages()
    {
        return pages;
    }
    
    public void printDetails()
    {
        System.out.println("Here are the details of the book:");
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Pages: " + pages);
        
        if(refNumber != 0)
        {
            System.out.println("Reference Number: " + refNumber);
        }
    }
    
    public void setRefNumber(int ref)
    {
        if(ref > 10)
        {
            refNumber = ref;
        }
        else {
            System.out.println("The number should be greater than 10!");
        }
    }
    
    public int getRefNumber()
    {
        return refNumber;
    }
    
    public int getNumberTitleLetters()
    {
        return title.length();
    }
}
