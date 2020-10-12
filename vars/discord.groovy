import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def createGroup(groupName, groupMembers, groupType, groupsList)
{
   def group = [
      name: groupName,
      members: groupMembers,
      type: groupType
   ]

   def groupJSON = JsonOutput.toJson(group)
   groupsList.add(groupJSON)
}

def getMembersOfGroup(groupName, groups)
{
   def jsonSlurper = new JsonSlurper()
   def members = null

   groups.each {
      def groupsParsed = jsonSlurper.parseText(it)
      if (groupsParsed.get("name") == groupName)
      {
         members = groupsParsed.get("members")
      }
   }

   return members
}

def getGroupType(groupName, groups)
{
   def jsonSlurper = new JsonSlurper()
   def type = null

   groups.each {
      def groupsParsed = jsonSlurper.parseText(it)
      if (groupsParsed.get("name") == groupName)
      {
         type = groupsParsed.get("type")
      }
   }

   return type
}

def mentionGroup(groupName, groups)
{
   def members = getMembersOfGroup(groupName, groups)
   def groupType = getGroupType(groupName, groups)

   def message = members.toMapString()
   members.each { key, value -> 
      if (value?.trim()) 
      {
         switch (groupType)
         {            
            case "user":
               message = message.replace("${key}:${value}", "<@${value}>")
               break
            case "role":
               message = message.replace("${key}:${value}", "<@&${value}>")
               break
            case "channel":
               message = message.replace("${key}:${value}", "<#${value}>")
               break
            default: 
               message = message.replace("${key}:${value}", "<@${value}>")
               break
         }
      }
      else
      {
         message = message.replace("${key}:", "")
      }
   }

   // Format message
   message = message.replace(",", " ")
   message = message.replace("[", "")
   message = message.replace("]", "")
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
   def jsonSlurper = new JsonSlurper()
   def swarmName = swarmID.replaceAll("\\d", "")
   def discordID = ""

   groups.each {
      def groupsParsed = jsonSlurper.parseText(it)
      if (groupsParsed.get("name") == swarmName)
      {
         def members = groupsParsed.get("members")
         members.each { key, value ->
            discordID = value
         }
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