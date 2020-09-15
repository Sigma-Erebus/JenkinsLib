def build(MSBuildPath, project, config, platform)
{
   bat(script: "CALL "${MSBuildPath}" "${project}" /t:build /p:Configuration=${config};Platform=${platform};verbosity=diagnostic")
}