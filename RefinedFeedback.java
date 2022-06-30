/**
 * Marks up supplied output according to how it matched supplied regular expressions (regexes).
 * @author Hyrum D. Carroll
 * @version 0.5 (Jun 22, 2022)
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.*;

public class RefinedFeedback{

    private final static String USAGE = "Usage <regex>[ <regex> ...] (with stdin containing the output to match up with the regex)";
    public final static String FLANKING_STR = "***";  // string appearing before and after matching literal

    public final static String PARAGRAPH_SYMBOL = "\u00B6"; // ¶, pilcrow (paragraph) symbol

    public final static String ALL_MATCHES_FOUND_MSG = "All regular expressions terms found!  Good job!";  
    public final static String NO_MATCHES_FOUND_MSG = "No matches found :(";  

    public static void DEBUG( String msg ){
        if(false){
            System.err.println("DEBUGGING: " + msg);
        }
    }

    public static void DEBUG( String[] a ){
        if(false){
            System.err.println("DEBUGGING: array (count: " + a.length + ")");
            for( String msg : a ){
                System.err.println("DEBUGGING:\t" + msg);
            }
        }
    }


    /**
     * String.repeat() substitution
     * @param str the string to repeat count times
     * @param count number of times to repeat str
     * @return returns str repeated count times
     */
    public static String stringRepeat( String str, int count){
        StringBuilder repeatedStr = new StringBuilder();
        for( int i = 0; i < count; ++i){
            repeatedStr.append( str );
        }
        return repeatedStr.toString();
    }
    
    /**
     * Get all of the input from stdin
     * @return a string with all of the input from stdin
     */
    public static String getAllInput( ){
        StringBuilder input = new StringBuilder();
        try{
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            while( (line = stdin.readLine()) != null ){
                input.append( line + "\n" );
            }
            stdin.close();
        }catch( IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return input.toString();
    }


    /**
     * Finds the indices of the first non-overlapping match in the supplied text for each regex in regexes (ignoring casing).
     * @param regexes Ordered list of regular expressions
     * @param text Text to look for matches in
     * @return An array of two-element arrays with each two-element containing the index of first and last matching characters in the corresponding regex from the parameter list.  If a regex is not found, the element is set to -1.
     */
    public static int[][] getMatchingIndices( String[] regexes, String text ){
        return getMatchingIndices( regexes, text, Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Finds the indices of the first non-overlapping match in the supplied text for each regex in regexes (ignoring casing).
     * @param regexes Ordered list of regular expressions
     * @param text Text to look for matches in
     * @param flags Flags to be used for each match
     * @return An array of two-element arrays with each two-element containing the index of first and last matching characters in the corresponding regex from the parameter list.  If a regex is not found, the element is set to -1.
     */
    public static int[][] getMatchingIndices( String[] regexes, String text, int flags ){
        //System.out.println("getMatchingIndices(regexes,"+text+","+flags+")");
        int[][] matches = new int[regexes.length][2];
    
        // initalize index for starting search position
        int startingSearchIndex = 0;
        
        // for each regex
        for(int regexI = 0; regexI < regexes.length; ++regexI){
            String regex = regexes[regexI];
            
            // search for first match (using flags)
            Pattern p = Pattern.compile(regex, flags);
            Matcher m = p.matcher(text.substring(startingSearchIndex));
            // record match (if found)
            int matchStartIndex = -1;
            int matchEndIndex = -1;
            if( m.find() ){
                //System.out.println(m);
                matchStartIndex = m.start() + startingSearchIndex;
                matchEndIndex = m.end() - 1 + startingSearchIndex;
                //System.out.println( "Searched for " + regex + " and found "+text.substring(matchStartIndex,matchEndIndex+1));
                
                // update starting search position
                startingSearchIndex = matchEndIndex + 1;
            }
            matches[regexI][0] = matchStartIndex;
            matches[regexI][1] = matchEndIndex;
        }
        return matches;
    }
    
    
    /**
     * Display the annotated view (matches indicated with UPPERCASE letters and flanked by ***) 
     * @param regexes Ordered list of regular expressions
     * @param text Student's submission
     * @param indices First and last indices of matches of the regular expressions in regexes in text
     */
    public static void displayAnnotatedViewed( String[] regexes, String text, int[][] indices ){
        displayAnnotatedViewed( regexes, text, indices, true );
    }
    
    /**
     * Display the annotated view (matches indicated with UPPERCASE letters and flanked by ***) 
     * @param regexes Ordered list of regular expressions
     * @param text Student's submission
     * @param indices First and last indices of matches of the regular expressions in regexes in text
     * @param displayHeader Flag indicating if the header is displayed
     */
    public static void displayAnnotatedViewed( String[] regexes, String text, int[][] indices, boolean displayHeader ){
        assert regexes.length == indices.length : "Number of regexes + ("+(regexes.length + 1) + ") differs from the number of indices of those matches (" + (indices.length + 1) + ")";
        
        StringBuilder capitalizedOutput = new StringBuilder(); // For matched literals captialized and flanked with ***
        if( displayHeader ){
            capitalizedOutput.append("Annotated Matches View\n");
            capitalizedOutput.append("=================================\n");
        }
        int lastMatchI = -1; // index in text of the last match (last character of the last matched literal)
        int textStartIndex = -1; // index of the first character in text that matches regex
        int textEndIndex   = -1; // index of the last  character in text that matched the last matching regex
        for(int regexI = 0; regexI < regexes.length; ++regexI){
            String regex = regexes[regexI];
            textStartIndex = indices[regexI][0]; // index of the first character in text that matches regex
            
            if( textStartIndex < 0 ){
                // no match found for this regex
                capitalizedOutput.append( "\n\nMissing Regex: " + regex + "\n");
                // Continue to display other missing regexes without the extra newline 
                while( regexI + 1 < regexes.length && indices[regexI + 1][0] < 0){
                    ++regexI;
                    regex = regexes[regexI];
                    capitalizedOutput.append( "\nMissing Regex: " + regex + "\n");
                }
                capitalizedOutput.append( "\n" );
            }else{
                capitalizedOutput.append( text.substring( textEndIndex + 1, textStartIndex ) ); // copy of output for in between literals
                textEndIndex = indices[regexI][1]; // only update if there was a match so that it is the last matched index
                capitalizedOutput.append( FLANKING_STR + text.substring( textStartIndex, textEndIndex + 1).toUpperCase() + FLANKING_STR ); // capitalized literal with flanking strings
            }
            DEBUG("capitalizedOutput: " + capitalizedOutput);
        }
        capitalizedOutput.append( text.substring( textEndIndex + 1, text.length() ) ); // copy of output until the end
        
        System.out.println( capitalizedOutput.toString().replace( PARAGRAPH_SYMBOL, PARAGRAPH_SYMBOL + "\n") + "\n" );
    }

    
    public static void main( String[] args ){
        /* Get the regular expression from the command-line */

        if( args.length < 1 ){
            System.err.println("ERROR: Only found " + args.length + " command-line arguments.\n");
            System.err.println(USAGE + "\n");
            System.exit(1);
        }

        // regex
        String[] regexes = args;
        DEBUG("regexes:");
        DEBUG(regexes);

        String outputStr = getAllInput();
        DEBUG( "outputStr (" + outputStr.length() + " characters): " + outputStr);
        outputStr = outputStr.replace("\n", PARAGRAPH_SYMBOL ); // replace all newlines with the pilcrow (paragraph symbol)
        
        // Get indices of matches for each regular expression element (against the submission)
        int[][] indices = getMatchingIndices( regexes, outputStr );

        /*
         * Display the expanded regex (BLAST-like) string and the annotated output (with flanking "***"s and capitalized matches)
         */
        // Figure out if there are no matches, at least one or everything matches
        int numMatches = 0;
        for( int i = 0; i < indices.length; ++i){
            if( indices[i][0] >= 0 ){
                ++numMatches;
            }
        }
        
        if(numMatches == 0){
            // no matches found :(
            System.out.println( "\n" + NO_MATCHES_FOUND_MSG + "\n" );
            displayAnnotatedViewed( regexes, outputStr, indices, false);
        }else{
            // at least 1 match found
        
            if( numMatches == indices.length ){
                // found all matches :)
                System.out.println( ALL_MATCHES_FOUND_MSG + "\n");
            }
            
            displayAnnotatedViewed( regexes, outputStr, indices );
        }
    }
}
