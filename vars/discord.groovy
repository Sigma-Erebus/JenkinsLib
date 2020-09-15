def sendMessage(webhook)
{
   bat(script: """
      curl -X POST ^
      -H "Content-Type: application/json" ^
      -d "{\"username\": \"test\", \"content\": \"hello\"}" ^
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