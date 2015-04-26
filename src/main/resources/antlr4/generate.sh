#!/bin/bash
OUT=~/workspaces/personal/regexp/target/generated-sources/antlr4
rm -rf "$OUT"
antlr4 Regexp.g4 -o "$OUT/cz/kvafy/regexp" -package "cz.kvafy.regexp" -no-listener -visitor
