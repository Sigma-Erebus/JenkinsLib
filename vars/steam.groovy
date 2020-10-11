def createDepotManifest(depotID, contentRoot, localPath = "*", depotPath = ".", isRecursive = true, exclude = "*.pdb")
{
   def depotManifest = libraryResource("depot_build_template.vdf")

   depotManifest = depotManifest.replace("<DEPOTID>", depotID)
   depotManifest = depotManifest.replace("<CONTENTROOT>", contentRoot)
   depotManifest = depotManifest.replace("<LOCALPATH>", localPath)
   depotManifest = depotManifest.replace("<DEPOTPATH>", depotPath)
   depotManifest = depotManifest.replace("<ISRECURSIVE>", "${isRecursive ? '1' : '0'}")
   depotManifest = depotManifest.replace("<EXCLUDE>", exclude)

   writeFile(file: "depot_build_${depotNumber}.vdf", text: depotManifest)
   return "depot_build_${depotNumber}.vdf"
}

def createAppManifest(appID, depotNumber, contentRoot, description = "", isPreview = false, localContentPath = "", branch = "", outputDir= "output")
{
   def appManifest = libraryResource("app_build_template.vdf")

   appManifest = appManifest.replace("<APPID>", appID)
   appManifest = appManifest.replace("<DESCRIPTION>", description)
   appManifest = appManifest.replace("<ISPREVIEW>", "${isPreview ? '1' : '0'}")
   appManifest = appManifest.replace("<LOCALCONTENT>", localContentPath)
   appManifest = appManifest.replace("<BRANCH>", branch)
   appManifest = appManifest.replace("<OUTPUTDIR>", outputDir)
   appManifest = appManifest.replace("<CONTENTROOT>", contentRoot)

   writeFile(file: "app_build_${appID}.vdf", text: appManifest)
   return "app_build_${appID}.vdf"
}