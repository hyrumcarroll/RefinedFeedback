"""Produces more user-friendly feedback for matchings text (e.g., a student's submission) with a set of required regular expressions"""

import re
import sys

USAGE = """Usage:  <answer key output>  <regex>[ <regex> ...]
           (with stdin containing the output to match up with the regex)"""
FLANKING_STR = "***" # string appearing before and after matches

PARAGRAPH_SYMBOL = "\u00B6"  # ¶, pilcrow (paragraph) symbol

ALL_MATCHES_FOUND_MSG = "All matches found!  Great job!"
NO_MATCHES_FOUND_MSG = "No matches found :("

DEFAULT_REGEX_FLAGS = re.IGNORECASE  # if flags are not specified, then use this/these flags

DEBUG_FLAG = False


def DEBUG( msg ):
    if DEBUG_FLAG:
        print("DEBUGGING: " + msg, file=sys.stderr)


def readFileContents( filename: str ) -> str:
    """Read all of the contents of a file"""

    try:
        fileObj = open( filename )
        fileContents = fileObj.read()
        fileObj.close()
    except BaseException as err:
        print(f"REFINED FEEDBACK ERROR: {err=}, {type(err)=}")
        return None
    
    return fileContents


def getAllInput( ) -> str:
    """Get all of the input from stdin and return it as a string"""

    try:
        return sys.stdin.read()
    except:
        print("REFINED FEEDBACK ERROR")
        sys.exit(1)


def getAnswerKeyMatches( regexes, answerKey=None, flags=DEFAULT_REGEX_FLAGS):
    """
    Finds the part of the answer key that matches each of the regexes.

    If a match is not found, an message is displayed to stderr and the regex is used instead.
    regexes is an ordered list of regular expressions.
    answerKey is the text to look for matches in.
    flags are to be used for each match.
    Returns a list of the matches in the answer key (or the regex if not found).
    """
    
    # If there's no answer key, just use the regexes
    if answerKey is None:
        return regexes

    matches = [ None ] * len(regexes)
    
    # Get indices of matches for each regular expression element (against the answer key)
    indices = getMatchingIndices( regexes, answerKey, flags )

    textStartIndex = -1  # index of the first character in answerKey that matches regex
    textEndIndex   = -1  # index of the last  character in answerKey that matched the last matching regex
    for regexI in range(len(regexes)):
        match = ""
        
        if indices[regexI] is None:
            # no match found for this regex in the answer key,
            # so use the regex for the match instead
            match = regexes[ regexI ]
            print(f"\n\nREFINED FEEDBACK ERROR: Unable to find regex index {regexI} ({match}) in answer key!!!\n\n", file=sys.stderr)
        else:
            textStartIndex = indices[regexI][0]
            textEndIndex = indices[regexI][1]
            match = answerKey[ textStartIndex: textEndIndex + 1]

        matches[ regexI ] = match

    return matches


def displayAnnotatedViewed( regexes, text, indices, answerKeyMatches ):
    """
    Display the annotated view (matches indicated with UPPERCASE letters and flanked by ***)

    regexes is an ordered list of regular expressions.
    text is a student's submissionl
    indices are a list of tuples with the first and last indices of matches of the regular expressions in regexes in text.
    answerKeyMatches are the matches for the regexes from the answer key (for displaying if the regex is not found in the text).
    """

    if len(regexes) != len(indices):
        print(f"Number of regexes + ({len(regexes)}) differs from the number of indices of those matches ({len(indices)})", file=sys.stderr)
        sys.exit(1)

    output = '' # For matches captialized and flanked with ***

    # Figure out if there are no matches, at least one or everything matches
    numMatches = len(indices) - indices.count(None)

    if numMatches == len(indices):
        # found all matches :)
        output += f"\n{ALL_MATCHES_FOUND_MSG}\n"

    numMatchesStr = f" ({numMatches} of {len(indices)} matches found)"
    output += f"\nAnnotated Matches View{numMatchesStr}\n"
    output += "(Matches are uppercased and indicated with *** before and after the match)\n"
    output += "==========================================================================\n"

    textStartIndex = -1  # index of the first character in text that matches regex
    textEndIndex   = -1  # index of the last  character in text that matched the last matching regex
    regexI = 0
    while regexI < len(regexes):
        regexStr = ""
        if DEBUG_FLAG: regexStr = f" ({regexes[regexI]})"

        if indices[regexI] is None:
            # no match found for this regex
            answerKeyMatch = answerKeyMatches[regexI]
            if regexI != 0:
                output += "\n"

            output += f"\nMissing: {answerKeyMatch}{regexStr}\n"

            # Continue to display other missing regexes without the extra newline
            while regexI + 1 < len(regexes) and indices[regexI + 1] is None:
                regexI += 1
                if DEBUG_FLAG: regexStr = f" ({regexes[regexI]})"
                answerKeyMatch = answerKeyMatches[regexI]
                output += f"Missing: {answerKeyMatch}{regexStr}\n"

            output += "\n"
        else:
            textStartIndex = indices[regexI][0]  # index of the first character in text that matches regex
            # copy of text before this match (if any) (and add in pilcrow to visualize the newline)
            output += text[textEndIndex + 1 : textStartIndex ].replace("\n", PARAGRAPH_SYMBOL + "\n" )
            textEndIndex = indices[regexI][1]  # only update if there was a match so that it is the last matched index
            output += FLANKING_STR + text[textStartIndex : textEndIndex + 1].upper().replace("\n", PARAGRAPH_SYMBOL + "\n" ) + FLANKING_STR  # capitalized matches with flanking strings

        DEBUG("output: " + output)
        regexI += 1

    output += text[ textEndIndex + 1 : ]  # copy of output until the end

    print( output )


def getMatchingIndices( regexes, text, reFlags=DEFAULT_REGEX_FLAGS ):
    """
    Finds the indices of the first non-overlapping match in text for
    each regex in regexes.

    reFlags are instances of RegexFlag.
    Returns a list of tuples with each tuple containing the index of
    first and last matching characters in the corresponding regex from
    the parameter list.  If a regex is not found, the element is None
    instead.
    """

    matches = []

    # initalize index for starting search position
    startingSearchIndex = 0

    # for each regex
    for regex in regexes:

        # search for first match (using reFlags)
        mo = re.search( regex, text[startingSearchIndex:], reFlags)
        # print(mo)

	# record match (if found)
        if mo:
            matchStartIndex = mo.start() + startingSearchIndex
            matchEndIndex = mo.end() - 1 + startingSearchIndex
            # print( f'Searched for {regex} and found {text[matchStartIndex:matchEndIndex+1]}')

            # update starting search position
            startingSearchIndex = matchEndIndex + 1
            matches.append( (matchStartIndex, matchEndIndex) )
        else:
            matches.append( None )

    return matches


def main():
    # Get the regular expression from the command-line

    if len(sys.argv) < 2:
        print(f"ERROR: Only found {len(sys.argv)} command-line arguments!\n", file=sys.stderr)
        print(f"\n{USAGE}\n", file=sys.stderr)
        sys.exit(1)


    # Read in answer key file
    answerKey = readFileContents( sys.argv[1] )

    # Copy the rest of the command-line arguments (the regexes)
    regexes = sys.argv[ 2 : ]
    DEBUG(f"regexes: {regexes}")

    answerKeyMatches = getAnswerKeyMatches( regexes, answerKey )

    outputStr = getAllInput()
    DEBUG( f"outputStr ({len(outputStr)} characters): {outputStr}" )

    # Get indices of matches for each regular expression element (against the submission)
    indices = getMatchingIndices( regexes, outputStr )


    # Display the annotated output (with flanking "***"s and capitalized matches)
    displayAnnotatedViewed( regexes, outputStr, indices, answerKeyMatches )


if __name__ == '__main__':
    main()
