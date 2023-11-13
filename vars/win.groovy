def makeWritable(folderPath)
{
   bat(label: "Removing Read-Only flags", script: "attrib -r ${folderPath}*.* /s")
}

def copyPathFiles(sourcePath, targetPath, fileFilter)
{
    try {
        bat(label: "Copying files", script: "robocopy ${sourcePath} ${targetPath} ${fileFilter}")
    } catch (err) {
        echo "Caught: ${err}"
        echo "Exit Code 1 means success for robocopy"
    }
}

def movePathFiles(sourcePath, targetPath, fileFilter)
{
    try {
        bat(label: "Copying files", script: "robocopy ${sourcePath} ${targetPath} ${fileFilter} /mov")
    } catch (err) {
        echo "Caught: ${err}"
        echo "Exit Code 1 means success for robocopy"
    }
}
