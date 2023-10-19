def runScript(scriptPath, args)
{
   bat(label: "running ${scriptPath}", script: "python -u \"${scriptPath}\" \"${args}\"")
}
