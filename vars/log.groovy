def print(message)
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