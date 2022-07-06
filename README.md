# RefinedFeedback
More user-friendly feedback for matchings text (e.g., a student's submission) with a set of required regular expressions

Note: Best practice is to not use greedy modifers in the regular expressions (or to make them non-greedy).
This is because each of the regular expressions are searched against the student's submission independently.
An example that illustrates this is:

Regular expressions:
- length.+as a number
- width.+as a number
- area is 6

Code:
```python
length = float( input( 'Please enter the length as a number: ') )
width = float( input( 'Please enter the width as a number: ') )
area = length * width
print( 'The area is', area )
```

Student's Submission (with an input of 2 and 3):
```
Please enter the length as a number: Please enter the width as a number: The area is 6.0
```
Notice that because the submission is just stdout (and doesn't not contain the newlines from stdin) and therefore is just one line.
This means that the feedback will be:
```
Annotated Matches View (2 of 3 matches found)
(Matches are uppercased and indicated with *** before and after the match)
==========================================================================
Please enter the ***LENGTH AS A NUMBER: PLEASE ENTER THE WIDTH AS A NUMBER***

Missing: width.+as a number

: The ***AREA IS 6***.0
```
Notice how the first regular expression greedily included what was intended to match for the second regular expression.
