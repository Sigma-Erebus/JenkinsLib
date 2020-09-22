# JenkinsSharedLib
A Shared Library to reuse functions between Jenkins Pipelines

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
* ```sync(credential, workspace)``` - Syncs Perforce workspace
* ```createTicket(credential, p4host)``` - Creates a valid ticket for Perforce/Swarm operations

### swarm.groovy
Allows operations on the swarm server

**Functions:**
* ```init(user, ticket, url)``` - Initializes swarm data
* ```clear()``` - Clears swarm data
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
* ```build(engineRoot, projectPath, config, platform, outputDir)``` - Build a (blueprintOnly) Unreal Engine 4 project

### vs.groovy
Uses MSBuild to compile Visual Studio projects

**Functions:**
* ```build(MSBuildPath, projectPath, config, platform)``` - Builds Visual Studio project

### discord.groovy
Handles communication between Jenkins and Discord

**Functions:**
* ```createMessage(title, buildPassed, fields, footer)``` - Used internally by discord.groovy to set up a message
* ```sendMessage(message, webhook)``` - Uses cURL to send a message to discord
* ```succeeded(config, platform, webhook)``` - Sends build information to discord if the build succeeds
* ```failed(config, platform, webhook)``` - Sends build information to discord if the build fails

### zip.groovy
Used to archive files into a zip folder using 7z

**Functions:**
* ```pack(source, archiveName)``` - Packs the content of the source folder to <archiveName>.zip
  
### gdrive.groovy
Sends files to Google Drive using cURL

**Functions:**
* ```upload(source, fileName, clientID, clientSecret, refreshToken, parents)``` - Uploads files to a folder in Google Drive
