def sendMessage(message, webhook)
{
   bat(script: "curl -H \"Content-Type: application/json\" -X POST -d \"${message}\" ${webhook}")
}

def succeeded(webhook)
{
   sendMessage("Build Success", ${webhook})
}

def failed(webhook)
{
   sendMessage("Build Failed", ${webhook})
}