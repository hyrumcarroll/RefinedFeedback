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

javaExecStr="java $base"
pythonExecStr="python $base.py"

answerKeyFilename="answer_key-$$.txt"
(echo "The answer to life,"; echo "the universe"; echo "and everything is 42."; echo "Next question please.") > $answerKeyFilename

echo "*******************************************************"
echo "Test case: Well-behaved output"
echo "*******************************************************"
for execStr in "$javaExecStr" "$pythonExecStr"; do 
    cmd="(echo \"The answer to life,\"; echo \"the universe\"; echo \"and everything is 42.\"; echo \"Next question please.\") | $execStr  --answer $answerKeyFilename \"life\" \"universe\" \"42\""
    echo $cmd
    eval $cmd
done

echo ""; echo "";
echo "*******************************************************"
echo "Test case: Output missing a literal"
echo "*******************************************************"
for execStr in "$javaExecStr" "$pythonExecStr"; do 
    cmd="(echo \"The answer to life,\"; echo \"the solar sytem\"; echo \"and everything is 42.\"; echo \"Next question please.\") | $execStr  --answer $answerKeyFilename \"life\" \"universe\" \"42\""
    echo $cmd
    eval $cmd
done


echo ""; echo "";
echo "*******************************************************"
echo "Test case: Output missing the last literal"
echo "*******************************************************"
for execStr in "$javaExecStr" "$pythonExecStr"; do 
    cmd="(echo \"The answer to life,\"; echo \"the universe\"; echo \"and everything is forty-two.\"; echo \"Next question please.\") | $execStr  --answer $answerKeyFilename \"life\" \"universe\" \"42\""
    echo $cmd
    eval $cmd
done


echo ""; echo "";
echo "*******************************************************"
echo "Test case: Only case-insensitive matches"
echo "*******************************************************"
for execStr in "$javaExecStr" "$pythonExecStr"; do 
    cmd="(echo \"The answer to LIFE,\"; echo \"the UNIVERSE\"; echo \"and everything is forty-two.\"; echo \"Next question please.\") | $execStr  --answer $answerKeyFilename \"life\" \"universe\" \"42\""
    echo $cmd
    eval $cmd
done


echo ""; echo "";
echo "*******************************************************"
echo "Test case: No matches"
echo "*******************************************************"
for execStr in "$javaExecStr" "$pythonExecStr"; do 
    cmd="(echo \"The answer to mortality,\"; echo \"the solor system\"; echo \"and everything is forty-two.\"; echo \"Next question please.\") | $execStr  --answer $answerKeyFilename \"life\" \"universe\" \"42\""
    echo $cmd
    eval $cmd
done

rm $answerKeyFilename

echo ""; echo "";
echo "*******************************************************"
echo "Test case: Project 4"
echo "*******************************************************"
for execStr in "$javaExecStr" "$pythonExecStr"; do 
    cmd="cat exampleOutput-cpsc1302-project4.txt | $execStr  --answer answerKey-cpsc1302-project4.txt  \"Welcome\" \"options\" \"name\" \"max\" \"remaining\" \"coins\" \"options\" \"name\" \"Human\" \"Mario\" \"98\\.8\" \"100\\.0\" \"98\\.8\" \"15\" \"options\" \"name\" \"Human\" \"Mario\" \"98\\.8\" \"100\\.0\" \"98\\.8\" \"15\" \"options\" \"name\" \"could not find\" \"options\" \"bye\""
    echo $cmd
    eval $cmd
done

