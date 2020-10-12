def ue4Info = null

// Must be called first before calling other functions
def build(ue4EngineRoot, eu4ProjectName, eu4Project, config, platform, outputDir, blueprintOnly = false)
{
   ue4Info = [engineRoot: ue4EngineRoot, projectName: eu4ProjectName, project: eu4Project]
   if (!blueprintOnly)
   {
      // Build
      bat(label: "Build UE4 project", script: "\"${ue4Info.engineRoot}Engine\\Binaries\\DotNET\\UnrealBuildTool.exe\" -projectfiles -project=\"${ue4Info.project}\" -game -rocket -progress")

      // Compile
      bat(label: "Compile UE4 project", script: "\"${ue4Info.engineRoot}Engine\\Binaries\\DotNET\\UnrealBuildTool.exe\" ${ue4Info.projectName} ${config} ${platform} -project=\"${ue4Info.project}\" -rocket -editorrecompile -progress -noubtmakefiles -NoHotReloadFromIDE -2019")

      // Package
      bat(label: "Package UE4 project", script: "\"${ue4Info.engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${ue4Info.project}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Cook -Allmaps -Build -Stage -Pak -Archive -Archivedirectory=\"${outputDir}\" -Rocket -Prereqs -Package")
   }
   else
   {
      // Only package since we have a blueprintOnly project
      bat(label: "Package UE4 project", script: "\"${ue4Info.engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${ue4Info.project}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Cook -Allmaps -Build -Stage -Pak -Archive -Archivedirectory=\"${outputDir}\" -Rocket -Prereqs -Package")
   }
}

def runAllTests(config = "Development", platform = "Win64")
{
   runAutomationCommand("RunAll Now", config, platform)
}

def runNamedTests(testNames, config = "Development", platform = "Win64")
{
   def testNamesJoined = testNames.join("+");
   runAutomationCommand("RunTests Now ${testNamesJoined}", config, platform)
}

def runFilteredTests(testFilter, config = "Development", platform = "Win64")
{
   switch (filter.toLowerCase().capitalize())
   {
      case "Engine":
      case "Smoke":
      case "Stress":
      case "Perf":
      case "Product":
         runAutomationCommand("RunFilter Now ${testFilter}", config, platform)
         break
      default:
         log.error("Invalid Filter! Valid Filters: Engine, Smoke, Stress, Perf, Product.")
         break
   }
}

def runAutomationCommand(testCommand, config = "Development", platform = "Win64")
{
   log("Running tests in ${config} configuration on ${platform}")
   def result = bat (label: "Run UE4 Automation Tests", script: "\"${ue4Info.engineRoot}Engine\\Binaries\\${platform}\\UE4Editor-cmd.exe\" \"${ue4Info.project}\" -stdout -fullstdlogoutput -buildmachine -nullrhi -unattended -NoPause -NoSplash -NoSound -ExecCmds=\"Automation ${testCommand};Quit\"", returnStatus: true)
   
   if (result != 0)
   {
      unstable "Some tests did not pass!"
   }
}

def fixupRedirects(platform = "Win64")
{
   bat(label: "Fix up redirectors in UE4 project", script: "\"${ue4Info.engineRoot}Engine\\Binaries\\${platform}\\UE4Editor.exe\" \"${ue4Info.project}\" -run=ResavePackages -fixupredirects -autocheckout -projectonly -unattended -stdout")
}