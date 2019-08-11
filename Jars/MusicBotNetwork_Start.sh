#!/bin/bash

stdout_log="/home/MusicBot/log/$name`date +%Y-%m-%d-%T`.log"
stderr_log="/home/MusicBot/log/$name`date +%Y-%m-%d-%T`.err"

java -jar MusicBotNetwork_1.5.2-Beta.jar
