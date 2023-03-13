import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
def ue5Info = null

// Must be called first before calling other functions
def init(ue5EngineRoot, ue5ProjectName, ue5Project)
{
   ue5Info = [engineRoot: ue5EngineRoot, projectName: ue5ProjectName, project: ue5Project]
}

def clear() 
{
   ue5Info.clear()
}

// Can be called without calling init()
def buildBlueprintProject(engineRoot, projectName, project, config, platform, outputDir, logFile = "${env.WORKSPACE}\\Logs\\UE5Build-${env.BUILD_NUMBER}.txt")
{
   init(engineRoot, projectName, project)
   
   // Only package since we have a blueprintOnly project
   bat(label: "Package UE5 project", script: "\"${ue5Info.engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${ue5Info.project}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Cook -Allmaps -Build -Stage -Pak -Archive -Archivedirectory=\"${outputDir}\" -Rocket -Prereqs -Package")
   
}

def buildPrecompiledProject(engineRoot, projectName, project, config, platform, outputDir, logFile = "${env.WORKSPACE}\\Logs\\UE5Build-${env.BUILD_NUMBER}.txt")
{
   init(engineRoot, projectName, project)
   
   // Package
   bat(label: "Package UE5 project", script: "\"${env.ENGINEROOT}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${env.PROJECT}\" -NoP4 -nocompileeditor -skipbuildeditor -TargetPlatform=${env.PLATFORM} -Platform=${env.PLATFORM} -ClientConfig=${env.CONFIG} -Cook -Build -Stage -Pak -Archive -Archivedirectory=\"${env.OUTPUTDIR}\" -Rocket -Prereqs -iostore -compressed -Package -nocompile -nocompileuat")
}

def buildCustomProject(engineRoot, projectName, project, config, platform, outputDir, customFlags = "-Cook -Allmaps -Build -Stage -Pak -Rocket -Prereqs -Package -crashreporter", logFile = "${env.WORKSPACE}\\Logs\\UE5Build-${env.BUILD_NUMBER}.txt")
{
   init(engineRoot, projectName, project)
   
   // Package
   bat(label: "Package UE5 project", script: "\"${ue5Info.engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${ue5Info.project}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Archive -Archivedirectory=\"${outputDir}\" ${customFlags}")
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
   def result = bat (label: "Run UE5 Automation Tests", script: "\"${ue5Info.engineRoot}Engine\\Binaries\\${platform}\\UnrealEditor-Cmd.exe\" \"${ue5Info.project}\" -stdout -fullstdlogoutput -buildmachine -nullrhi -unattended -NoPause -NoSplash -NoSound -ExecCmds=\"Automation ${testCommand};Quit\" -ReportExportPath=\"${env.WORKSPACE}\\Logs\\UnitTestsReport\"", returnStatus: true)
   
   if (result != 0)
   {
      unstable "Some tests did not pass!"
   }
}

def getTestResults()
{
    def json = readFile file: 'Logs/UnitTestsReport/index.json', encoding: "UTF-8"
    json = json.replace( "\uFEFF", "" );
    return json
}

// Author: https://www.emidee.net/ue4/2018/11/13/UE4-Unit-Tests-in-Jenkins.html
@NonCPS
def getJUnitXMLContentFromJSON( String json_content ) {
    def j = new JsonSlurper().parseText( json_content )
    
    def sw = new StringWriter()
    def builder = new MarkupBuilder( sw )

    builder.doubleQuotes = true
    builder.mkp.xmlDeclaration version: "1.0", encoding: "utf-8"

    builder.testsuite( tests: j.succeeded + j.failed, failures: j.failed, time: j.totalDuration ) {
        for ( test in j.tests ) {
            builder.testcase( name: test.testDisplayName, classname: test.fullTestPath, status: test.state ) {
                for ( entry in test.entries ) { 
                    builder.failure( message: entry.event.message, type: entry.event.type, entry.filename + " " + entry.lineNumber )
                }
            }
        }
    } 

    return sw.toString()
}

def fixupRedirects(platform = "Win64")
{
   bat(label: "Fix up redirectors in UE5 project", script: "\"${ue5Info.engineRoot}Engine\\Binaries\\${platform}\\UnrealEditor.exe\" \"${ue5Info.project}\" -run=ResavePackages -fixupredirects -autocheckout -projectonly -unattended -stdout")
}