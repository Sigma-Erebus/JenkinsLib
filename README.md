# JenkinsSharedLib
A Shared Library to reuse functions between Jenkins Pipelines

## Groups system:
The Swarm and Discord scripts read from a JSON file containing groups.

### Format
The format for these groups look like this:
```
{
	"groups": [
		{
			"name": "<GROUPNAME>",
			"discordID": "<DISCORD_ID>",
			"swarmID": ["<SWARM_ID>", "<SWARM_ID>"],
			"type": "<GROUPTYPE>"
		}
	]
}
```

```<GROUPNAME>``` is the name of the group. This could be "PR", "David" or "GENERAL" for example.

```<DISCORD_ID>``` is the Discord ID of a "user", "role" or "channel" from the ```<GROUPTYPE>```.

```<SWARM_ID>``` is the Swarm ID of a user from the ```<GROUPTYPE>```.

```<GROUPTYPE>``` determines the type of the group. This can be "user", "role" or "channel".

### Example
Here is an example of a groups.json file:
```
{
	"groups": [
		{
			"name": "<ROLE_NAME>",
			"discordID": "<DISCORD_ROLE_ID>",
			"swarmID": ["<SWARM_ID_ONE>", "<SWARM_ID_TWO>"],
			"type": "role"
		},
		{
			"name": "<CHANNEL_NAME>",
			"discordID": "<DISCORD_CHANNEL_ID>",
			"swarmID": ["<SWARM_ID_ONE>", "<SWARM_ID_TWO>", "<SWARM_ID_THREE>"],
			"type": "channel"
		},
		{
			"name": "<USER_NAME>",
			"discordID": "<DISCORD_USER_ID>",
			"swarmID": ["<SWARM_ID>"],
			"type": "user"
		}
	]
}
```

## Scripts:

### log.groovy
Used to log messages to the console

**Functions:**
* ```log(message)``` - Log a custom message
* ```log.warning(message)``` - Log a warning
* ```log.error(message)``` - Log an error
* ```log.currStage()``` - Log the current stage

### p4v.groovy
Handles all Perforce related functions

**Functions:**
* ```init(p4credential, p4host, p4workspace, p4viewMapping, cleanForce = true)``` - Syncs Perforce workspace (***Should be called before all other p4v functions!***)
* ```clean()``` - Cleans workspace default changelist (***Don't use other p4v functions after calling this function!***)
* ```createTicket()``` - Creates a valid ticket for Perforce/Swarm operations
* ```unshelve(id)``` - Unshelves a shelved changelist
* ```getChangelistDescr(id)``` - Get the description from a changelist
* ```getCurrChangelistDescr()``` - Get the description from the current changelist

### swarm.groovy
Allows operations on the swarm server

**Functions:**
* ```init(swarmUser, p4ticket, swarmUrl)``` - Initializes swarm data (***Should be called before all other swarm functions!***)
* ```clear()``` - Clears swarm data (***Don't use other swarm functions after calling this function!***)
* ```getParticipantsOfGroup(groupName, group)``` - Get participants from a group in a JSON file
* ```getParticipantsOfGroups(groupNames, groups)``` - Get participants from multiple groups in a JSON file
* ```createReview(id, participants = null)``` - Create a review from a shelved changelist
* ```getReviewID(curlResponse)``` - Get the ID of a review
* ```getReviewAuthor(curlResponse)``` - Get the author of a review
* ```upVote(id)``` - Upvotes a swarm review
* ```downVote(id)``` - Downvotes a swarm review
* ```comment(id, comment)``` - Comments on a swarm review
* ```needsReview(id)``` - Sets the state of a review to "needsReview"
* ```needsRevision(id)``` - Sets the state of a review to "needsRevision"
* ```approve(id)``` - Approve a review
* ```archive(id)``` - Archive a review
* ```reject(id)``` - Reject a review
* ```setState(id, state)``` - Set a review to a custom state

### ue4.groovy
Handles all Unreal Engine 4 related operations

**Functions**
* ```build(ue4EngineRoot, ue4ProjectName, ue4Project, config, platform, outputDir, blueprintOnly = false, logFile = "${env.WORKSPACE}\\Logs\\UE4Build-${env.BUILD_NUMBER}.txt")``` - Build a (blueprintOnly) Unreal Engine 4 project
* ```runAllTests(config = "Development", platform = "Win64")``` - Runs all tests defined in an Unreal Engine 4 project
* ```runNamedTests(testNames, config = "Development", platform = "Win64")``` - Runs named tests defined in an Unreal Engine 4 project
* ```runFilteredTests(testFilter, config = "Development", platform = "Win64")``` - Runs all tests in a filter. Valid filters are: Engine, Smoke, Stress, Perf & Product
* ```runAutomationCommand(testCommand, config = "Development", platform = "Win64")``` - Runs an automation command from the Unreal Engine 4 command line
* ```fixupRedirects(platform = "Win64")``` - Fixs up all redirects in an Unreal Engien 4 project

### vs.groovy
Uses MSBuild to compile Visual Studio projects

**Functions:**
* ```build(MSBuildPath, projectPath, config, platform)``` - Builds Visual Studio project

### discord.groovy
Handles communication between Jenkins and Discord

**Functions:**
* ```createGroup(groupName, groupDiscordID, groupSwarmID, groupType, groupsList)``` - Adds a JSON group object to a list
* ```mentionGroup(groupName, groups)``` - Mention a group on discord (***use with discord.createMessage***)
* ```mentionGroups(groupNames, groups)``` - Mention multiple groups on discord (***use with discord.createMessage***)
* ```swarmIDtoDiscordID(swarmID, groups)``` - Convert a swarm ID to a discord ID
* ```createMessage(title, messageColor, fields, footer = null, content = null)``` - Send a message to discord (***use with discord.sendMessage***)
* ```sendMessage(message, webhook)``` - Uses cURL to send a message to discord
* ```succeeded(config, platform, webhook)``` - Sends build information to discord if the build succeeds
* ```failed(config, platform, webhook)``` - Sends build information to discord if the build fails
* ```newReview(id, author, swarmUrl, webhook, buildStatus = "not built", description = null)``` - Sends review information to discord when a new review is ready

### zip.groovy
Used to archive files into a zip folder using 7z

**Functions:**
* ```pack(source, archiveName, use7z = true)``` - Packs the content of the source folder to <archiveName>.zip (Uses 7z by default)
* ```unpack(archiveName, destination, use7z = true)``` - Unpacks the content of a zip file to the destination folder (Uses 7z by default)
  
### gdrive.groovy
Sends files to Google Drive using cURL

**Functions:**
* ```upload(source, fileName, clientID, clientSecret, refreshToken, parents)``` - Uploads files to a folder in Google Drive (Shared Drives are supported)

### sentry.groovy
Tool to diagnose, fix and optimize performance and debug crashes. More information: https://sentry.io/

**Functions:**
* ```upload(sentryCLIPath, authToken, organisation, project, outputFolder)``` - Uploads debug symbols to Sentry server

### steam.groovy
Uploads packaged projects to Steam

**Functions:**
* ```init(steamCredential, steamCmdPath)``` - Initializes steam data (***Should be called before all other steam functions!***)
* ```createDepotManifest(depotID, contentRoot, localPath = "*", depotPath = ".", isRecursive = true, exclude = "*.pdb")``` - Creates the depot manifest
* ```createAppManifest(appID, depotID, contentRoot, description = "", isPreview = false, localContentPath = "", branch = "", outputDir= "output")``` - Creates app manifest
* ```tryDeploy(appManifest)``` - Tries to deploy to Steam using SteamGuard
* ```deploy(appManifest, steamGuard = null)``` - Deploy to Steam (***Prefer using tryDeploy when trying to deploy to Steam!***)
