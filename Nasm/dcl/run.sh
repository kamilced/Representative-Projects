#!/usr/bin/env bash

echo $(nasm -f elf64 -w+all -w+error -o dcl.o dcl.asm)
echo $(ld --fatal-warnings -o dcl dcl.o)
echo $(./dcl $(cat przyklady/_test_0_2.key))