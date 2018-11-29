# Discord Musicbot Network

## Installation

1. Download the [latest Release Jar](Jars/RELEASES.md)
2. Place all needed Propertie Files next to the downlaoded Jar
    - apikeys.properties
    - botchannel.properties
    - botkeys.properties
    - (Optional) language.properties
    - (Optional) ignoredMessages.properties
3. Execute Jar

### Adding Bots to the `botkeys.properties` file

1. Visit the [Discord Developer Portal](https://discordapp.com/developers/applications)
2. Create an Application 

![Create Application](Installation/CreateApplication.png?raw=true "Create Application")

3. Switch to the "Bot" tab 

![Bot Tab](Installation/Bot.png?raw=true "Bot Tab")

4. Click on "Add Bot" 

![Add Bot](Installation/AddBot.png?raw=true "Add Bot")


5. Click on "Click to Reveal Token" 

![Bot Token](Installation/BotToken.png?raw=true "Bot Token")

6. Add the revealed Bot Token to the `botkeys.properties` file in the following format: `BOT_NAME=BOT_TOKEN`
7. You may add as many Bots to the `botkeys.properties` file as you want

### Adding API Keys to the `apikeys.properties` file

1. Follow the following [Guide](https://www.slickremix.com/docs/get-api-key-for-youtube/) to obtaining your Youtube API key
2. Add your Youtube API key to the `apikeys.properties` file in the following format: `youtube-key=YOUR_API_KEY`

### Adding your Bot to a Server

Just replace BOT_ID in the following Link with the Client ID of your Bot.

https://discordapp.com/api/oauth2/authorize?client_id=BOT_ID&scope=bot&permissions=8

You can find the Client ID in the [Discord Developer Portal](https://discordapp.com/developers/applications)
![Bot ID](Installation/BotID.png?raw=true "Bot ID")

### Adding a BotChannel to the `botchannel.properties` file

The Bots you add need their own Bot Channel to work properly and you need to add this Botchannels ID to the `botchannel.properties` file

1. Create Botchannel on the Server you want to use the Bots on.
2. Rightclick on the GuildIcon the Bot are in and select `Copy ID`
3. Add the copied Guild ID to the `botchannel.properties` file in the following format: `GUILD_ID=`
4. Rightclick on the Botchannel you just created and select `Copy ID`, aswell
5. Add the copied Channel ID to the `botchannel.properties` file just behind the 'equals'-Symbol: `GUILD_ID=BOT_CHANNEL_ID`
6. Repeat for all Servers, your bots are on

### (Optional) Select Language for Guild

There are currently 2 Languages available
- Englisch
- German

if no language is selected, englisch is used per default

1. Copy Guild ID like in the `Adding a BotChannel...` Part
2. Add Guild ID to the `language.properties` file in the following format: `GUILD_ID=LANGUAGE` with LANGUAGE being either Englisch or German

### (Optional) Add Message to Ignore list

The Bots automatically delete any Message in the Botchannel that is not related to the Bots.

But if you added a Message to a Botchannel you want not be deleted just add it to the `ignoredMessages.properties`

1. Copy Guild ID like in the `Adding a BotChannel...` Part
2. Add Guild ID to the `ignoredMessages.properties` file in the following format: `GUILD_ID=`
3. Rightclick on Message, you want to keep and select `Copy ID`
4. Add this ID to the `ignoredMessages.properties` file in the following format: `GUILD_ID=MESSAGE_ID`
5. (Optional) if you want to add more Messages for a Guild, add them to the `ignoredMessages.properties` file seperated by a dot `.`: `GUILD_ID=MESSAGE_ID.MESSAGE_ID.MESSAGE_ID`