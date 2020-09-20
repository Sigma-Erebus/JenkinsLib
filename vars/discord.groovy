import groovy.json.JsonOutput

def createMessage(title, buildPassed, fields, url, content = null)
{
   // Color must be decimal value
   def color = 16711680 // Default = red

   if (buildPassed) 
   {
      color = 65280 // Green
   }

   def body = [embeds: 
    [[
    title: title,
    color: color,
    fields: fields
    ]]
   ]
	
   if (url) 
   {
       body.embeds[0].url = url
   }
	
   if (content) 
   {
       body.content = content
   }
	
   return JsonOutput.toJson(body).replace('"','""')
}

def sendMessage(message, webhook)
{
   bat(script: """
      curl -X POST ^
      -H "Content-Type: application/json" ^
      -d \"${message}\" ^
      ${webhook}
   """)
}

def succeeded(config, platform, webhook)
{
   sendMessage(createMessage(":white_check_mark: BUILD #${env.BUILD_NUMBER} - SUCCESS :white_check_mark:",
                                     true,
                                     [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} has succeeded", value:"Last Changelist: ${env.P4_CHANGELIST}", inline:true]]
                                     , "${env.BUILD_URL}")
                                 , webhook)
}

def failed(config, platform, webhook)
{
   sendMessage(createMessage(":x: BUILD #${env.BUILD_NUMBER} - FAILED :x:",
                                     false,
                                     [[name:"${config}(${platform}) ${env.JOB_BASE_NAME} has failed", value:"Last Changelist: ${env.P4_CHANGELIST}", inline:true]]
                                     , "${env.BUILD_URL}")
                                 , webhook)
}