import RefinedFeedback

def test(regexes, submission, matches):
        indices = RefinedFeedback.getMatchingIndices( regexes, submission )
        print( "DEBUGGING: Indices:", indices )
        print( RefinedFeedback.getAnnotatedView( regexes, submission, indices, matches ) )


def testCheckingAccount():
    regexes = ["Checking", "balance", "checking123", "690\\.68", "(check|number)", "\\b2124\\b", "Savings", "balance", "savings124", "\\b1,?122.00\\b", "APR", "\\b1\\.0\\b"]

    submission = '''Checking Account:
Balance for account checking123: $0.0
Last processed check number:2124
Savings Account: 
Balance for account savings124: $0.0
APR: 0.01%
Balance for account checking123: $801.02
'''

    matches = ["Checking", "Balance", "checking123", "690.68", "check", "2124", "Savings", "Balance", "savings124", "1,122.00", "APR", "1.0"];
    
    test( regexes, submission, matches )
    
    
def testCheckingAccount2():
    regexes = ["account manager", "Checking", "balance", "checking123", "690\\.68", "(check|number)", "\\b2124\\b", "Savings", "balance", "savings124", "\\b1,?122.00\\b", "APR", "\\b1\\.0\\b", "Thank"]

    submission = '''Welcome to my manager of accounts!
Checking Account:
Balance for account checking123: $0.0
Last processed check number:2124
Savings Account: 
Balance for account savings124: $0.0
APR: 0.01%
Balance for account checking123: $801.02
Exiting the Account Manager 
Thank-you'''
    
    matches = ["Account Manager", "Checking", "Balance", "checking123", "690.68", "check", "2124", "Savings", "Balance", "savings124", "1,122.00", "APR", "1.0", "Thank"]
    
    test( regexes, submission, matches )

    
    
def testVideoGameChar():    
    regexes = ["Welcome", "options", "name", "max", "remaining", "coins", "options", "name", "Human", "Mario", "98\\.8", "100\\.0", "98\\.8", "15", "options", "name", "Human", "Mario", "98\\.8", "100\\.0", "98\\.8", "15", "options", "name", "could not find", "options", "bye"]
    
    submission = '''Welcome to you VideoGameChar Manager to store all of the video game characters
options: 
1) Add a human character 
2) Add an enemy character 
3) Add an enemy boss character 
4) Display a character 
5) Display all characters 
6) Remove a character 
7) Increase a character's health 
8) Decrease a character's health 
9) Quit
Please choose from the above options: 
You entered: 1
Please enter the name of the Human character: 
You entered: Mario
Please enter Mario's max health: 
You entered: 100.0
Please enter Mario's remaining health: 
You entered: 98.76
Please enter the number of coins for Mario:
You entered: 15
options: 
1) Add a human character 
2) Add an enemy character 
3) Add an enemy boss character 
4) Display a character 
5) Display all characters 
6) Remove a character 
7) Increase a character's health 
8) Decrease a character's health 
9) Quit
Please choose from the above options: 
You entered: 4
Please enter the name of the character to display: 
You entered: Mario
Human Mario: 98.8 out of 100.0 health or 98.8% and 15 coins
options: 
1) Add a human character 
2) Add an enemy character 
3) Add an enemy boss character 
4) Display a character 
5) Display all characters 
6) Remove a character 
7) Increase a character's health 
8) Decrease a character's health 
9) Quit
Please choose from the above options: 
You entered: 6
Please enter the name of the character to remove: 
You entered: Mario
Removing: 
Human Mario: 98.8 out of 100.0 health or 98.8% and 15 coins
options: 
1) Add a human character 
2) Add an enemy character 
3) Add an enemy boss character 
4) Display a character 
5) Display all characters 
6) Remove a character 
7) Increase a character's health 
8) Decrease a character's health 
9) Quit
Please choose from the above options: 
You entered: 4
Please enter the name of the character to display: 
You entered: Mario
options: 
1) Add a human character 
2) Add an enemy character 
3) Add an enemy boss character 
4) Display a character 
5) Display all characters 
6) Remove a character 
7) Increase a character's health 
8) Decrease a character's health 
9) Quit
Please choose from the above options: 
You entered: 9
Good-bye!
'''

    test( regexes, submission, regexes )


def main():
    testCheckingAccount()
    testCheckingAccount2()
    testVideoGameChar()

    
if __name__ == '__main__':
    main()
