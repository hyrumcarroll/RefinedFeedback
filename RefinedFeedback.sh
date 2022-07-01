#!/bin/bash

base="RefinedFeedback"
if [ "$base.java" -nt "$base.class" ]; then 
    cmd="javac RefinedFeedback.java"
    echo $cmd
    eval $cmd

    # echo "$?"
    if [ $? != 0 ]; then
	# echo "'$cmd' returned $?" 2>&1
	exit 1
    fi
    echo ""; echo "";
fi

echo "*******************************************************"
echo "Test case: Well-behaved output"
echo "*******************************************************"
answerKeyFilename="answer_key-$$.txt"
((echo "The answer to life,"; echo "the universe"; echo "and everything is 42."; echo "Next question please.") > $answerKeyFilename
cmd="(echo \"The answer to life,\"; echo \"the universe\"; echo \"and everything is 42.\"; echo \"Next question please.\") | java $base $answerKeyFilename \"life\" \"universe\" \"42\""
echo $cmd
eval $cmd
rm $answerKeyFilename
exit

echo ""; echo "";
echo "*******************************************************"
echo "Test case: Output missing a literal"
echo "*******************************************************"
cmd="(echo \"The answer to life,\"; echo \"the solar sytem\"; echo \"and everything is 42.\"; echo \"Next question please.\") | java $base \"life.*universe.*42\""
echo $cmd
eval $cmd

echo ""; echo "";
echo "*******************************************************"
echo "Test case: Output missing the last literal"
echo "*******************************************************"
cmd="(echo \"The answer to life,\"; echo \"the universe\"; echo \"and everything is forty-two.\"; echo \"Next question please.\") | java $base \"life.*universe.*42\""
echo $cmd
eval $cmd


echo ""; echo "";
echo "*******************************************************"
echo "Test case: No matches"
echo "*******************************************************"
cmd="(echo \"The answer to LIFE,\"; echo \"the UNIVERSE\"; echo \"and everything is forty-two.\"; echo \"Next question please.\") | java $base \"life.*universe.*42\""
echo $cmd
eval $cmd



echo ""; echo "";
echo "*******************************************************"
echo "Test case: Project 4"
echo "*******************************************************"
cmd="cat exampleOutput-cpsc1302-project4.txt | java $base \"Welcome.*options.*name.*max.*remaining.*coins.*options.*name.*Human.*Mario.*98.8.*100.0.*98.8.*15.*options.*name.*Human.*Mario.*98.8.*100.0.*98.8.*15.*options.*name.*could not find.*options.*bye\""
echo $cmd
eval $cmd
