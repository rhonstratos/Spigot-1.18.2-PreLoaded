# Spigot-1.18.2-PreLoaded
## REQUIREMENTS
- Download [Java SE Dev Kit 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- Install Java 17
- Add `JAVA_DIRECTORY\bin` folder directory to system PATH. [Check Here (Step 2: How to set Permanent Path of JDK in Windows)](https://www.javatpoint.com/how-to-set-path-in-java)
- Open CMD, enter: `java --version`
- Everything is ok if java version is set to 17
## Run Server (one click)
- run `runServer.bat`
### Edit server ram config
- edit `runServer.bat` in notepad, <br>then
- change max ram `-Xmx<Ram Value>G`
## Server Settings 
- Server name
- Server pvp mode
- Creative/Survival/Adventure mode
- Online mode = True (Mojang account only) | False (any MC)<br>
<p>More info at <a href="https://minecraft.fandom.com/wiki/Server.properties">MC Wiki</a><br></p>
<p> edit <code>Server.properties</code> file to apply settings then restart server</p>

## Common Console Server Commands
- `say <your-message>` ex: `say Hello peasants`
- `stop` Stops the server. **IMPORTANT WHEN CLOSING SERVER**
- `save-all` Save server
- `op <player-name>` set player to Operator. ex: `op Vreiln`
- `deop <player-name>` unset operator. ex: `deop Vreiln`

<p>For more easy to use commands, goto <a href="https://mcstacker.net/">MCStack</a></p>