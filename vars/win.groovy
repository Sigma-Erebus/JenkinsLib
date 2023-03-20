def makeWritable(folderPath)
{
   bat(label: "Removing Read-Only flags", script: "attrib -r ${folderPath}*.* /s")
}

def copyPath(sourcePath, targetPath)
{
    bat(label: "Copying files", script: "robocopy ${sourcePath} ${targetPath}")
}