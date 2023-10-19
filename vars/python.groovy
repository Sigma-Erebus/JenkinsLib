def runScript(scriptPath, args)
{
   bat(label: "running ${scriptPath}", script: "python \"${scriptPath}\" \"${args}\"", returnStdout: true)
}
