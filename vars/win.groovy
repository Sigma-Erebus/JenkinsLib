def makeWritable(folderPath)
{
   bat(label: "Removing Read-Only flags", script: "attrib -r ${folderPath}*.* /s")
}

def copyPathFiles(sourcePath, targetPath)
{
    try {
        bat(label: "Copying files", script: "robocopy ${sourcePath} ${targetPath}")
    } catch (err) {
        echo "Caught: ${err}"
        echo "Exit Code 1 means success for robocopy"
    }
}

def movePathFiles(sourcePath, targetPath)
{
    try {
        bat(label: "Copying files", script: "robocopy ${sourcePath} ${targetPath} /mov")
    } catch (err) {
        echo "Caught: ${err}"
        echo "Exit Code 1 means success for robocopy"
    }
}
