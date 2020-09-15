def build(MSBuildPath, projectPath, config, platform)
{
   bat(script: "CALL \"${MSBuildPath}\" \"${projectPath}\" /t:build /p:Configuration=${config};Platform=${platform};verbosity=diagnostic")
}