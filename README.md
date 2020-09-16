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

### vs.groovy
Uses MSBuild to compile Visual Studio projects

**Functions:**
* ```build(MSBuildPath, projectPath, config, platform)``` - Builds Visual Studio project

### discord.groovy
Handles communication between Jenkins and Discord

**Functions:**
* ```createMessage(title, status, fields, url, content = null)``` - Used internally by discord.groovy to set up a message
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
