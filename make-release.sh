#!/usr/bin/env bash
#
# Create a release archive for blisp.
#

TARGET_DIR=./target
JAR=

mvn clean compile
mvn package

# Get the most recent tag from git to use for the archive name

