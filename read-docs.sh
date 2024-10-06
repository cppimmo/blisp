#!/usr/bin/env bash
#
# Open the javadocs in the default web browser.
#
PATH_TO_INDEX=./target/reports/apidocs/index.html
mvn javadoc:javadoc
python -m webbrowser $PATH_TO_INDEX

