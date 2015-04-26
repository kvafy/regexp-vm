#!/bin/bash

# Settings

GRAMMAR=Regexp
GRAMMAR_INIT_NONTERMINAL=init
ANTLR4_COMPLETE=~/Downloads/antlr-4.5-complete.jar


# Prepare environment

export CLASSPATH=".:${ANTLR4_COMPLETE:$CLASSPATH}"
antlr4='java -Xmx500M org.antlr.v4.Tool'
grun='java org.antlr.v4.runtime.misc.TestRig'


# Compile the grammar and execute TestRig to show AST for given input

${antlr4} ${GRAMMAR}.g4
javac *.java
echo "Enter the input text into the console and then insert Ctrl-D"
${grun} ${GRAMMAR} ${GRAMMAR_INIT_NONTERMINAL} -gui


# Clean

rm -f *.java *.class *.tokens
