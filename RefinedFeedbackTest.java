/**
 * Test cases for RefinedFeedback
 * @author Hyrum D. Carroll
 * @version 0.1 (Jun 22, 2022)
 */
import java.util.Arrays;

public class RefinedFeedbackTest{

    public static void test( String[] regexes, String submission, String[] matches ){
        
        int[][] indices = RefinedFeedback.getMatchingIndices( regexes, submission );
        System.out.println( "DEBUGGING: Indices: " + Arrays.deepToString( indices ) );
        System.out.println( RefinedFeedback.getAnnotatedView( regexes, submission, indices, matches ) );
    }
    
    public static void testCheckingAccount(){
        String[] regexes = {"Checking", "balance", "checking123", "690\\.68", "(check|number)", "\\b2124\\b", "Savings", "balance", "savings124", "\\b1,?122.00\\b", "APR", "\\b1\\.0\\b"};

        String submission = "Checking Account:\n" +
            "Balance for account checking123: $0.0\n" +
            "Last processed check number:2124\n" +
            "Savings Account: \n" +
            "Balance for account savings124: $0.0\n" +
            "APR: 0.01%\n" +
            "Balance for account checking123: $801.02\n";


        String[] matches = {"Checking", "Balance", "checking123", "690.68", "check", "2124", "Savings", "Balance", "savings124", "1,122.00", "APR", "1.0"};
        
        test( regexes, submission, matches );
    }
        

    public static void testVideoGameChar(){


        String[] regexes = {"Welcome", "options", "name", "max", "remaining", "coins", "options", "name", "Human", "Mario", "98\\.8", "100\\.0", "98\\.8", "15", "options", "name", "Human", "Mario", "98\\.8", "100\\.0", "98\\.8", "15", "options", "name", "could not find", "options", "bye"};

    
        String submission = "Welcome to you VideoGameChar Manager to store all of the video game characters\n" +
            "options: \n" +
            "1) Add a human character \n" +
            "2) Add an enemy character \n" +
            "3) Add an enemy boss character \n" +
            "4) Display a character \n" +
            "5) Display all characters \n" +
            "6) Remove a character \n" +
            "7) Increase a character's health \n" +
            "8) Decrease a character's health \n" +
            "9) Quit\n" +
            "Please choose from the above options: \n" +
            "You entered: 1\n" +
            "Please enter the name of the Human character: \n" +
            "You entered: Mario\n" +
            "Please enter Mario's max health: \n" +
            "You entered: 100.0\n" +
            "Please enter Mario's remaining health: \n" +
            "You entered: 98.76\n" +
            "Please enter the number of coins for Mario:\n" +
            "You entered: 15\n" +
            "options: \n" +
            "1) Add a human character \n" +
            "2) Add an enemy character \n" +
            "3) Add an enemy boss character \n" +
            "4) Display a character \n" +
            "5) Display all characters \n" +
            "6) Remove a character \n" +
            "7) Increase a character's health \n" +
            "8) Decrease a character's health \n" +
            "9) Quit\n" +
            "Please choose from the above options: \n" +
            "You entered: 4\n" +
            "Please enter the name of the character to display: \n" +
            "You entered: Mario\n" +
            "Human Mario: 98.8 out of 100.0 health or 98.8% and 15 coins\n" +
            "options: \n" +
            "1) Add a human character \n" +
            "2) Add an enemy character \n" +
            "3) Add an enemy boss character \n" +
            "4) Display a character \n" +
            "5) Display all characters \n" +
            "6) Remove a character \n" +
            "7) Increase a character's health \n" +
            "8) Decrease a character's health \n" +
            "9) Quit\n" +
            "Please choose from the above options: \n" +
            "You entered: 6\n" +
            "Please enter the name of the character to remove: \n" +
            "You entered: Mario\n" +
            "Removing: \n" +
            "Human Mario: 98.8 out of 100.0 health or 98.8% and 15 coins\n" +
            "options: \n" +
            "1) Add a human character \n" +
            "2) Add an enemy character \n" +
            "3) Add an enemy boss character \n" +
            "4) Display a character \n" +
            "5) Display all characters \n" +
            "6) Remove a character \n" +
            "7) Increase a character's health \n" +
            "8) Decrease a character's health \n" +
            "9) Quit\n" +
            "Please choose from the above options: \n" +
            "You entered: 4\n" +
            "Please enter the name of the character to display: \n" +
            "You entered: Mario\n" +
            "options: \n" +
            "1) Add a human character \n" +
            "2) Add an enemy character \n" +
            "3) Add an enemy boss character \n" +
            "4) Display a character \n" +
            "5) Display all characters \n" +
            "6) Remove a character \n" +
            "7) Increase a character's health \n" +
            "8) Decrease a character's health \n" +
            "9) Quit\n" +
            "Please choose from the above options: \n" +
            "You entered: 9\n" +
            "Good-bye!\n";

        test( regexes, submission, regexes );
    }
    
    public static void main( String[] args ){
        testCheckingAccount();
        testVideoGameChar();
    }
}
