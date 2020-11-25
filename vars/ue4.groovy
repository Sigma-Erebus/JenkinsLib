def ue4Info = null

// Must be called first before calling other functions
def init(ue4EngineRoot, ue4ProjectName, ue4Project)
{
   ue4Info = [engineRoot: ue4EngineRoot, projectName: ue4ProjectName, project: ue4Project]
}

def clear() 
{
   ue4Info.clear()
}

// Can be called without calling init()
def build(engineRoot, projectName, project, config, platform, outputDir, blueprintOnly = false, logFile = "${env.WORKSPACE}\\Logs\\UE4Build-${env.BUILD_NUMBER}.txt")
{
   init(engineRoot, projectName, project)
   if (!blueprintOnly)
   {
      // Build
      bat(label: "Run UnrealBuildTool", script: "\"${ue4Info.engineRoot}Engine\\Binaries\\DotNET\\UnrealBuildTool.exe\" -projectfiles -project=\"${ue4Info.project}\" -Game -Rocket -Progress -NoIntellisense -WaitMutex -Platforms=\"${platform}\" PrecompileForTargets = PrecompileTargetsType.Any;")
      
      if (config.toLowerCase() == "development" && platform.toLowerCase() != "ps4")
      {
         bat(label: "Build UE4 project", script: "\"${ue4Info.engineRoot}Engine\\Build\\BatchFiles\\Build.bat\" ${ue4Info.projectName}Editor ${platform} ${config} \"${ue4Info.project}\" -Log=\"${logFile}\"")
      }
      
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