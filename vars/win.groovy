def makeWritable(folderPath)
{
   bat(label: "Removing Read-Only flags", script: "attrib -r ${folderPath}*.* /s")
}