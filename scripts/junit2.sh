#!/bin/bash
#
# NAME
#     junit - run JUnit tests with ease
#
# USAGE
#     junit [FILE... | CLASS... | DIR]
#
# EXAMPLES
#     junit
#     junit inf101/v16/tests
#     junit inf101/v16/lab1/StackTest.java
#     junit inf101.v16.lab1.StackTest
#
# AUTHOR
#     Adrian Dvergsdal (atmoz.net)

JUNIT_HOME=/usr/share/java # Change this to where junit jar-files are installed
CLASSPATH=.:$JUNIT_HOME/junit.jar:$JUNIT_HOME/hamcrest-core.jar
CLASSPATH="lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar"


tmpArgs=($@)

# Use current directory by default
if [ ${#tmpArgs[@]} -eq 0 ]; then
    tmpArgs=(".")
fi

# Expand directory arguments with files
for arg in ${tmpArgs[@]}; do
    if [ -d "$arg" ]; then
        # If dir: add *.java files having @Test
        files=($(grep "@Test" --include "*.java" -rsl "$arg"))
        args=(${args[@]} ${files[@]})
    fi
done

# Build path and class arrays
for arg in ${args[@]}; do
    if [[ "$arg" == *".java" ]]; then
        path="$arg"
        path="${path#./}" # Remove ./ if present
        class="${path//\//\.}" # Replaces / with .
        class="${class%.java}" # Removes .java
    else
        class="$arg"
        path="${class//\./\/}" # Replaces . with /
        path+=".java"
    fi

    if [ ! -f "$path" ]; then
        echo "The file \"$path\" does not exist!"
        exit 1
    fi

    pathArray=(${pathArray[@]} "$path")
    classArray=(${classArray[@]} "$class")
done

# Check results
if [ ${#pathArray[@]} -eq 0 ]; then
    echo "Found no tests!"
    exit 1
else
    echo "Found ${#pathArray[@]} files with tests:"
    for path in ${pathArray[@]}; do
        echo " - $path"
    done
    echo # new line
fi

# Compile and run tests
echo "classpath: $CLASSPATH"
echo "path array: ${pathArray[@]}"
echo "class array: ${classArray[@]}"
set -x
javac --enable-preview --source 17 -d bin -cp "bin;$CLASSPATH" ${pathArray[@]}
echo compiled.
# works fine. but the dirname is tst/junit
# looks like it needs just junt
# how to strip off the tst 
javac --enable-preview --source 17 -d bin -cp "bin;$CLASSPATH" ${pathArray[@]} && exec java -cp "bin;$CLASSPATH" org.junit.runner.JUnitCore ${classArray[@]}
#                                                        javac ${pathArray[@]} && exec java -cp $CLASSPATH org.junit.runner.JUnitCore ${classArray[@]}