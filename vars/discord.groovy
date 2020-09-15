def sendMessage(message, webhook)
{
   bat(script: "curl -X POST --data '{\"content\": \"${message}\"' --header \"Content-Type:application/json\" \"${webhook}\"")
}

def succeeded(webhook)
{
   sendMessage("Build Success", ${webhook})
}

def failed(webhook)
{
   sendMessage("Build Failed", ${webhook})
}