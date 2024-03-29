#!/bin/bash

# project 0
# counts

input=$1
output=$2
java counts.Main $input $output

#
# Fill in the empty line below with the command or commands that run your
# Counts program, so that the data is read from 'input' and the file 'output'
# is populated with the word counts. For example, if your program is written
# in Java and takes one argument, the input file name, and prints the word
# counts as standard output, type
#
#       java <myprogram> $input > $output
#
# with <myprogram> replaced with your program name.
#   If instead your program takes both the input and output file names as
# arguments, type
#
#       java <myprogram> $input $output
#
#   If you are having difficulty getting the script to run your program, talk
# to the TAs.
##############################################################################

##############################################################################
# Run this script with the command
#
#       ./counts data.txt name_of_output_file
