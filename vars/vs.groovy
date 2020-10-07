def build(MSBuildPath, projectPath, config, platform)
{
   bat(label: "Compile VS project", script: "CALL \"${MSBuildPath}\" \"${projectPath}\" /t:build /p:Configuration=${config};Platform=${platform};verbosity=diagnostic")
}