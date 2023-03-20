def makeWritable(folderPath)
{
   bat(label: "Removing Read-Only flags", script: "attrib -r ${folderPath}*.* /s")
}

def copyPathFiles(sourcePath, targetPath)
{
    bat(label: "Copying files", script: "robocopy ${sourcePath} ${targetPath}")
}

def movePathFiles(sourcePath, targetPath)
{
    bat(label: "Moving files", script: "robocopy ${sourcePath} ${targetPath} /mov")
}