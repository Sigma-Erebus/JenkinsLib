def call(message)
{
   echo "${message}"
}

def warning(message)
{
   echo "Warning: ${message}"
}

def error(message)
{
   echo "Error: ${message}"
}

def currStage()
{
   echo "${STAGE_NAME}"
}

def file(targetFile)
{
   def content = readFile(file: targetFile)
   echo "Content of ${targetFile}:\n\n"
}