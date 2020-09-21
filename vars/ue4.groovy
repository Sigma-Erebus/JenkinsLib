def build(engineRoot, projectPath, config, platform, outputDir)
{
   bat(script: "CALL \"${engineRoot}Engine\\Build\\BatchFiles\\RunUAT.bat\" BuildCookRun -Project=\"${projectPath}\" -NoP4 -Distribution -TargetPlatform=${platform} -Platform=${platform} -ClientConfig=${config} -ServerConfig=${config} -Cook -Allmaps -Build -Stage -Pak -Archive -Archivedirectory=\"${outputDir}\" -Rocket -Prereqs -Package")
}