/**
 * Marks up supplied output according to how it matched supplied regular expressions (regexes).
 * @author Hyrum D. Carroll
 * @version 0.5 (Jun 22, 2022)
 */

import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.*;

public class RefinedFeedback{

    private final static String USAGE = "Usage:  <answer key output>  <regex>[ <regex> ...] (with stdin containing the output to match up with the regex)";
    public final static String FLANKING_STR = "***";  // string appearing before and after matches

    public final static String PARAGRAPH_SYMBOL = "\u00B6"; // ¶, pilcrow (paragraph) symbol

    public final static String ALL_MATCHES_FOUND_MSG = "All matches found!  Great job!";  
    public final static String NO_MATCHES_FOUND_MSG = "No matches found :(";

    private final static int DEFAULT_REGEX_FLAGS = Pattern.CASE_INSENSITIVE;  // if flags are not specified, then use this/these flags

    private final static boolean DEBUG = false;

    public static void DEBUG( String msg ){
        if( DEBUG ){
            System.err.println("DEBUGGING: " + msg);
        }
    }

    public static void DEBUG( String[] a ){
        if( DEBUG ){
            System.err.println("DEBUGGING: array (count: " + a.length + ")");
            for( String msg : a ){
                System.err.println("DEBUGGING:\t" + msg);
            }
        }
    }

    /**
     * Read all of the contents of a file
     * @param filename Filename to read all of the contents
     * @return A String with all of the contents of filename
     */
    public static String readFileContents( String filename ){
        StringBuilder fileContents = new StringBuilder();
        Scanner fileScanner = null;
        try{
            // Try to open the file
            fileScanner = new Scanner( new FileReader( filename ) );

            // Read all lines
            while( fileScanner.hasNextLine() ){
                fileContents.append( fileScanner.nextLine() );
            }
            /* }catch( FileNotFoundException e ){
               System.err.println("REFINED FEEDBACK ERROR: " + e.getMessage() );
               return null; */
        }catch( Exception e){
            System.err.println("REFINED FEEDBACK ERROR: " + e.getMessage() );
            return null;
        }finally{
            // Close file
            if( fileScanner != null ){
                fileScanner.close();
            }
        }
        return fileContents.toString();
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
        return getMatchingIndices( regexes, text, DEFAULT_REGEX_FLAGS);
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
     * Finds the part of the answer key that matches each of the regexes.  If a match is not found, an message is displayed to stderr and the regex is used instead.
     * @param regexes Ordered list of regular expressions
     * @param answerKey Text to look for matches in
     * @return Array of the matches in the answer key (or the regex if not found)
     */
    public static String[] getAnswerKeyMatches( String[] regexes, String answerKey){
        return getAnswerKeyMatches( regexes, answerKey, DEFAULT_REGEX_FLAGS);
    }
        
    /**
     * Finds the part of the answer key that matches each of the regexes.  If a match is not found, an message is displayed to stderr and the regex is used instead.
     * @param regexes Ordered list of regular expressions
     * @param answerKey Text to look for matches in
     * @param flags Flags to be used for each match
     * @return Array of the matches in the answer key (or the regex if not found)
     */
    public static String[] getAnswerKeyMatches( String[] regexes, String answerKey, int flags){

        // If there's no answer key, just use the regexes
        if( answerKey == null ){
            return regexes;
        }
        
        String[] matches = new String[ regexes.length ];

        // Get indices of matches for each regular expression element (against the answer key)
        int[][] indices = getMatchingIndices( regexes, answerKey, flags );

        int textStartIndex = -1; // index of the first character in answerKey that matches regex
        int textEndIndex   = -1; // index of the last  character in answerKey that matched the last matching regex
        for(int regexI = 0; regexI < regexes.length; ++regexI){
            String match = "";
            textStartIndex = indices[regexI][0];
            
            if( textStartIndex < 0 ){
                // no match found for this regex in the answer key, so use the regex for the match instead
                match = regexes[ regexI ];
                System.err.println("\n\nREFINED FEEDBACK ERROR: Unable to find regex index " + regexI + " (" + match + ") in answer key!!!\n\n");
            }else{
                textEndIndex = indices[regexI][1]; // only update if there was a match so that it is the last matched index
                match = answerKey.substring( textStartIndex, textEndIndex + 1);
            }
            matches[ regexI ] = match;
        }
        return matches;
    }
    
    
    /**
     * Display the annotated view (matches indicated with UPPERCASE letters and flanked by ***) 
     * @param regexes Ordered list of regular expressions
     * @param text Student's submission
     * @param indices First and last indices of matches of the regular expressions in regexes in text
     * @param answerKeyMatches Matches for the regexes from the answer key (for displaying if the regex is not found in the text)
     */
    public static void displayAnnotatedViewed( String[] regexes, String text, int[][] indices, String[] answerKeyMatches ){
        assert regexes.length == indices.length : "Number of regexes + ("+regexes.length+ ") differs from the number of indices of those matches ("+indices.length+")";
        
        StringBuilder output = new StringBuilder(); // For matches captialized and flanked with ***

        // Figure out if there are no matches, at least one or everything matches
        int numMatches = 0;
        for( int i = 0; i < indices.length; ++i){
            if( indices[i][0] >= 0 ){
                ++numMatches;
            }
        }
        
        if( numMatches == indices.length ){
            // found all matches :)
            output.append( "\n" + ALL_MATCHES_FOUND_MSG + "\n\n");
        }
        String numMatchesStr = " ("+numMatches+" of " + indices.length + " matches found)";
        output.append("Annotated Matches View" + numMatchesStr + "\n");
        output.append("Matches are uppercased and indicated with *** before and after the match\n");
        output.append("========================================================================\n");

        int textStartIndex = -1; // index of the first character in text that matches regex
        int textEndIndex   = -1; // index of the last  character in text that matched the last matching regex
        for(int regexI = 0; regexI < regexes.length; ++regexI){
            String regexStr = "";
            if( DEBUG ){ regexStr = " (" + regexes[regexI] + ")";  }
            textStartIndex = indices[regexI][0]; // index of the first character in text that matches regex
            
            if( textStartIndex < 0 ){
                // no match found for this regex
                String answerKeyMatch = answerKeyMatches[regexI];
                if( regexI != 0 ){
                    output.append("\n");
                }
                output.append( "\nMissing: " + answerKeyMatch + regexStr + "\n");
                // Continue to display other missing regexes without the extra newline 
                while( regexI + 1 < regexes.length && indices[regexI + 1][0] < 0){
                    ++regexI;
                    if( DEBUG ){ regexStr = " (" + regexes[regexI] + ")";  }
                    answerKeyMatch = answerKeyMatches[regexI];
                    output.append( "Missing: " + answerKeyMatch + regexStr + "\n");
                }
                output.append( "\n" );
            }else{
                output.append( text.substring( textEndIndex + 1, textStartIndex ) ); // copy of text before this match (if any)
                textEndIndex = indices[regexI][1]; // only update if there was a match so that it is the last matched index
                output.append( FLANKING_STR + text.substring( textStartIndex, textEndIndex + 1).toUpperCase() + FLANKING_STR ); // capitalized matches with flanking strings
            }
            DEBUG("output: " + output);
        }
        output.append( text.substring( textEndIndex + 1, text.length() ) ); // copy of output until the end
        
        System.out.println( output.toString().replace( PARAGRAPH_SYMBOL, PARAGRAPH_SYMBOL + "\n") + "\n" );
    }

    
    public static void main( String[] args ){
        /* Get the regular expression from the command-line */

        if( args.length < 2 ){
            System.err.println("ERROR: Only found " + args.length + " command-line arguments!\n");
            System.err.println("\n" + USAGE + "\n");
            System.exit(1);
        }

        // Read in answer key file 
        String answerKey = readFileContents( args[0] );
        
        // Copy the rest of the command-line arguments (the regexes) 
        String[] regexes = Arrays.copyOfRange( args, 1, args.length );
        DEBUG("regexes:");
        DEBUG(regexes);

        String[] answerKeyMatches = getAnswerKeyMatches( regexes, answerKey );
        
        String outputStr = getAllInput();
        DEBUG( "outputStr (" + outputStr.length() + " characters): " + outputStr);
        outputStr = outputStr.replace("\n", PARAGRAPH_SYMBOL ); // replace all newlines with the pilcrow (paragraph symbol)
        
        // Get indices of matches for each regular expression element (against the submission)
        int[][] indices = getMatchingIndices( regexes, outputStr );

        /*
         * Display the annotated output (with flanking "***"s and capitalized matches)
         */
        displayAnnotatedViewed( regexes, outputStr, indices, answerKeyMatches );
    }
}
