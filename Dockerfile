FROM adoptopenjdk:11-jre-openj9

WORKDIR /musicbot

#Copy Current Jar to musicbot dir
ADD Jars/MusicBotNetwork_1.4.2-Beta.jar /jar/Musicbot.jar

#Start Bot
CMD java -jar /jar/Musicbot.jar