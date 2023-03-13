import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def createGroup(groupName, groupDiscordID, groupSwarmID, groupType, groupsList)
{
   def group = [
      name: groupName,
      discordID: groupDiscordID,
      swarmID: groupSwarmID,
      type: groupType
   ]

   def groupJSON = JsonOutput.toJson(group)
   groupsList.add(groupJSON)
}

def mentionGroup(groupName, groups)
{
   def groupType = ""
   def discordID = ""
   def groupsParsed = new JsonSlurper().parseText(groups)

   groupsParsed.groups.each { group ->
      
      if (group.name == groupName)
      {
         groupType = group.type
         discordID = group.discordID
      }
   }

   def message = discordID
   switch (groupType)
   {            
      case "user":
         message = message.replace(discordID, "<@${discordID}>")
         break
      case "role":
         message = message.replace(discordID, "<@&${discordID}>")
         break
      case "channel":
         message = message.replace(discordID, "<#${discordID}>")
         break
      default: 
         message = message.replace(discordID, "<@${discordID}>")
         break
   }

   // Format message
   message = message.replace(",", " ")
   message = "${groupName}: " + message

   return message
}

def mentionGroups(groupNames, groups)
{
   def message = ""

   groupNames.each {
      message = message + mentionGroup(it, groups) + "\n"
   }

   return message
}

def swarmIDtoDiscordID(swarmID, groups)
{
   def swarmName = swarmID.replaceAll("\\d", "")
   def discordID = ""
   def groupsParsed = new JsonSlurper().parseText(groups)

   groupsParsed.groups.each { group ->
      
      if (group.name == swarmName)
      {
         discordID = group.discordID
      }
   }

   return discordID
}

def createMessage(title, messageColor, fields, footer = null, content = null)
{
   // Color must be decimal value
   def color = 0 // Transparant

   switch (messageColor) 
   {            
      case "green":
         color = 65280 // Green
         break
      case "yellow":
         color = 16776960 // Yellow
         break
      case "red":
         color = 16711680 // Red
         break
      default: 
         color = 0 // Transparant
         break
   }

   def body = [embeds: 
      [[
      title: title,
      color: color,
      fields: fields
      ]]
   ]
	
   if (footer)
   {
      body.embeds[0].footer = footer
   }

   if (content)
   {
      body.content = content
   }

   return JsonOutput.toJson(body).replace('"','""')
}

def sendMessage(message, webhook)
{
   bat(label: "Send Discord Message", script: "curl -X POST -H \"Content-Type: application/json\" -d \"${message}\" ${webhook}")
}

def succeeded(config, platform, webhook)
{
   sendMessage(createMessage(":white_check_mark: BUILD SUCCEEDED :white_check_mark:",
                                     "green",
                                     [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} has succeeded", 
                                     value:"Last Changelist: ${env.P4_CHANGELIST}"],
                                     [name:"Job url", 
                                     value:"${env.BUILD_URL}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"])
                                 , webhook)
}

def failed(config, platform, webhook)
{
   sendMessage(createMessage(":x: BUILD FAILED :x:",
                                     "red",
                                     [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} has failed", 
                                     value:"Last Changelist: ${env.P4_CHANGELIST}"],
                                     [name:"Job url", 
                                     value:"${env.BUILD_URL}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"])
                                 , webhook)
}

def unstable(config, platform, webhook)
{
   sendMessage(createMessage(":warning: UNSTABLE BUILD :warning:",
                    "yellow",
                    [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} is unstable", 
                    value:"Last Changelist: ${env.P4_CHANGELIST}"],
                    [name:"Job url", 
                    value:"${env.BUILD_URL}"]],
                    [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"])
                , webhook)
}
def newReview(id, author, swarmUrl, webhook, buildStatus = "not built", description = null)
{
   sendMessage(createMessage(":warning: NEW REVIEW :warning:",
                                     "yellow",
                                     [[name:"A new review is ready", 
                                     value:"${swarmUrl}/reviews/${id}"],
                                     [name:"Author",
                                     value:"${author}"],
                                     [name:"Participants", 
                                     value:"${description}"],
                                     [name:"Build status",
                                     value:"Build ${buildStatus}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"],
                                     "${description}")
                                 , webhook)
}

def reportTestResults(json, webhook)
{
   def testResults = new JsonSlurper().parseText(json)

   def success = testResults.succeeded
   def warning = testResults.succeededWithWarnings
   def failed = testResults.failed
   def total = success + warning + failed

   sendMessage(createMessage(":clipboard: NEW TEST REPORT :clipboard: ",
                                     "yellow",
                                     [[name:"A new test report is ready", 
                                     value:"${env.BUILD_URL}testReport/"],
                                     [name:":white_check_mark: Succeeded", 
                                     value:"${success}/${total}"],
                                     [name:":warning: Succeeded with warnings", 
                                     value:"${warning}/${total}"],
                                     [name:":x: Failed", 
                                     value:"${failed}/${total}"]],
                                     [text:"${env.JOB_BASE_NAME} (${env.BUILD_NUMBER})"])
                                 , webhook)
}
