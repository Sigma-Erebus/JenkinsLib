import groovy.json.JsonOutput

def createMessage(title, status, fields, url, content = null){

   def color = 16711680

   if ( status == "ok"){
      color = 65280
   }else if(status == "new"){
       color = "359360"
   }

   def body = [embeds: 
   [[
    title: title,
    color: color,
    fields: fields
    ]]]
	
    if (url != null){
        body.embeds[0].url = url
    }
    if(content !=null){
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

def succeeded(webhook)
{
   sendMessage(createMessage(":white_check_mark: Build #${env.BUILD_NUMBER} - Last Perforce Revision: ${env.P4_CHANGELIST} Passed Building",
                                     "ok",
									 [[name:"Last Perforce Revision", value:"${env.P4_CHANGELIST}", inline:true]]
									 ,"https://jbs1.buas.nl:8443/job/Y2020-Y3/job/Y2020-Y3-Arid/job/Arid-TestEnv/","**Status: Success**\nBuild URL: ${env.BUILD_URL}")
                                 , webhook)
}

def failed(webhook)
{
   sendMessage("Build Failed", ${webhook})
}