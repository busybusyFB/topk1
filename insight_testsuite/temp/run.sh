#!/bin/bash

#remove the files created previously
rm -r ./output ./src/*.class
#create output dir
mkdir ./output
echo "Previous files removed."
#compile and run java
echo "compile and run java:"
javac ./src/TopKSelector.java
cd ./src
java TopKSelector ../input/H1B_FY_2014.csv ../output/top_10_occupations.txt ../output/top_10_states.txt
