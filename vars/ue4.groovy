def build(engineRoot, projectName, project, config, platform, outputDir, blueprintOnly = false)
{
   if (!blueprintOnly)
   {
      // Build
      bat(script: "\"${engineRoot}Engine\\Binaries\\DotNET\\UnrealBuildTool.exe\" -projectfiles -project=\"${project}\" -game -rocket -progress")

      // Compile
      bat(script: "\"${engineRoot}Engine\\Binaries\\DotNET\\UnrealBuildTool.exe\" ${projectName} ${config} ${platform} -project=\"${project}\" -rocket -editorrecompile -progress -noubtmakefiles -NoHotReloadFromIDE -2019")

      // Package
      bat(script: "\"${engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${project}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Cook -Allmaps -Build -Stage -Pak -Archive -Archivedirectory=\"${outputDir}\" -Rocket -Prereqs -Package")
   }
   else
   {
      // Only package since we have a blueprintOnly project
      bat(script: "\"${engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${project}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Cook -Allmaps -Build -Stage -Pak -Archive -Archivedirectory=\"${outputDir}\" -Rocket -Prereqs -Package")
   }
}

def fixupredirects(engineRoot, project)
{
   bat(script: "\"${engineRoot}Engine\\Binaries\\Win64\\UE4Editor.exe\" \"${project}\" -run=ResavePackages -fixupredirects -autocheckout -projectonly -unattended")
}