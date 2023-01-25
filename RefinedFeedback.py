"""Produces more user-friendly feedback for matchings text (e.g., a student's submission) with a set of required regular expressions"""

import re
import sys
import argparse

#USAGE = """[-answer <answer key output filename>]  [-explanations <explanations filename>]  <regex>[ <regex> ...]
#           (with stdin containing the output to match up with the regex)"""
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
        #print(f"REFINED FEEDBACK ERROR: {err=}, {type(err)=}")
        print(f"REFINED FEEDBACK ERROR: {err}, {type(err)}")
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
        return list(regexes)

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


def getAnnotatedView( regexes, text, indices, answerKeyMatches ):
    """
    Generate the annotated view (matches indicated with UPPERCASE letters and flanked by ***)

    regexes is an ordered list of regular expressions.
    text is a student's submissionl
    indices are a list of tuples with the first and last indices of matches of the regular expressions in regexes in text.
    answerKeyMatches are the matches for the regexes from the answer key (for displaying if the regex is not found in the text).
    Returns a string with the full annotated view.
    """

    if len(regexes) != len(indices):
        print(f"Number of regexes + ({len(regexes)}) differs from the number of indices of those matches ({len(indices)})", file=sys.stderr)
        sys.exit(1)

    output = '' # For matches captialized and flanked with ***

    # Calculate the number of matches
    numMatches = len(indices) - indices.count(None)

    if numMatches == len(indices):
        # found all matches :)
        output += f"\n{ALL_MATCHES_FOUND_MSG}\n"

    numMatchesStr = f" ({numMatches} of {len(indices)} matches found)"
    #output += f"\nAnnotated Matches View{numMatchesStr}\n"
    output += f"\nOutput with Matches and Missing Items{numMatchesStr}\n"
    output += "(Matches are uppercased and indicated with *** before and after the match)\n"
    output += "(Terms that are missing are identified with '<<< Missing: [the missing item] >>>')\n";
    output += "==========================================================================\n"

    textStartIndex = -1  # index of the first character in text that matches regex
    textEndIndex   = -1  # index of the last  character in text that matched the last matching regex
    regexI = 0
    missingStr = '' # lines to be added to the output with missed matches
    while regexI < len(regexes):
        regexStr = ""
        if DEBUG_FLAG: regexStr = f" ({regexes[regexI]})"

        if indices[regexI] is None:
            # no match found for this regex
            answerKeyMatch = answerKeyMatches[regexI]
            if regexI != 0:
                missingStr += "\n"

            missingStr += f"\n<<< Missing: {answerKeyMatch}{regexStr} >>>\n"

            # Continue to display other missing regexes without the extra newline
            while regexI + 1 < len(regexes) and indices[regexI + 1] is None:
                regexI += 1
                if DEBUG_FLAG: regexStr = f" ({regexes[regexI]})"
                answerKeyMatch = answerKeyMatches[regexI]
                missingStr += f"<<< Missing: {answerKeyMatch}{regexStr} >>>\n"

            missingStr += "\n"
        else:
            textStartIndex = indices[regexI][0]  # index of the first character in text that matches regex
            # copy of text before this match (if any) (and add in pilcrow to visualize the newline)
            output += text[textEndIndex + 1 : textStartIndex ].replace("\n", PARAGRAPH_SYMBOL + "\n" )
            # add in lines about missed matches, if any
            output += missingStr
            missingStr = ''
            textEndIndex = indices[regexI][1]  # only update if there was a match so that it is the last matched index
            output += FLANKING_STR + text[textStartIndex : textEndIndex + 1].upper().replace("\n", PARAGRAPH_SYMBOL + "\n" ) + FLANKING_STR  # capitalized matches with flanking strings

        DEBUG("output: " + output)
        regexI += 1

    output += text[ textEndIndex + 1 : ]  # copy of output until the end
    output += missingStr

    return output


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


def updateAnswerKeyMatches( answerKeyMatches, explanationsFilename ) -> None:

    #print(f'updateAnswerKeyMatches( {answerKeyMatches}, {explanationsFilename} )')
    
    # make sure the filename has at least 1 character
    if len(explanationsFilename) == 0:
        return
    
    explanationsFileContents = readFileContents( explanationsFilename )
    # print(f'explanationsFileContents: {explanationsFileContents}.')
    if explanationsFileContents.endswith('\n'):
        # if the last character is a newline, then remove it
        explanationsFileContents = explanationsFileContents[:-1]
        
    explanationsStrs = explanationsFileContents.split( '\n' ) # assumes that each explanation string is on its own line
    if len(explanationsStrs) > len(answerKeyMatches):
        print(f'ERROR: Found more explanation strings ({len(explanationsStrs)}) than answer key items / regular expressions ({len(answerKeyMatches)})!', file=sys.stderr)
        sys.exit()
        
    for i in range(len(explanationsStrs)):
        eStr = explanationsStrs[i]
        if len( eStr ):
            # update only non-empty explanation strings
            DEBUG( f'Updating answer key at position {i+1} ("{answerKeyMatches[i]}") with "{eStr}"')
            answerKeyMatches[i] = explanationsStrs[i]


def init_argparse() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        #usage=f'{USAGE}',
        description='Compare the output of stdin with the regular expressions (using the answer key and/or explanations file to describe what was expected)',
    )
    parser.add_argument(
        '-v', '--version', action='version',
        version=f'{parser.prog} version 2.1.0'
    )
    parser.add_argument('--answer', dest='answerKeyFilename', type=str, required=False)
    parser.add_argument('--explanations', dest='explanationsFilename', type=str)
    parser.add_argument('regexes', nargs="+", type=str)
    return parser

def main() -> None:
    parser = init_argparse()
    args = parser.parse_args()

    regexes = list(args.regexes)
    print(args)
    
    # if the answer key was passed in, then read in the answer key file
    answerKey=None
    if args.answerKeyFilename:
        # Read in answer key file
        answerKey = readFileContents( args.answerKeyFilename )

    answerKeyMatches = getAnswerKeyMatches( regexes, answerKey )

    # if the file with the explanations for each of the terms is passed in, then override the answerKeyMatches
    if args.explanationsFilename:
        updateAnswerKeyMatches( answerKeyMatches, args.explanationsFilename )
    
    outputStr = getAllInput()
    DEBUG( f"outputStr ({len(outputStr)} characters): {outputStr}" )

    # Get indices of matches for each regular expression element (against the submission)
    indices = getMatchingIndices( regexes, outputStr )

    # Display the annotated output (with flanking "***"s and capitalized matches)
    annotatedView = getAnnotatedView( regexes, outputStr, indices, answerKeyMatches )
    print( annotatedView )


if __name__ == '__main__':
    main()
