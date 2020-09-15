def sendMessage(message, webhook)
{
   bat(script: """
      curl -X POST ^
      -H "Content-Type: application/json" ^
      -d "{\"username\": \"test\", \"${message}\": \"hello\"}" ^
      ${webhook}
   """)
}

def succeeded(webhook)
{
   sendMessage("Build Success", ${webhook})
}

def failed(webhook)
{
   sendMessage("Build Failed", ${webhook})
}