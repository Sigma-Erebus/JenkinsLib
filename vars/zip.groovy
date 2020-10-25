def pack(source, archiveName)
{
   powershell(label: "Pack into zip file", script: "Compress-Archive -Path \"${source}\" -DestinationPath \"${archiveName}.zip\"");
}

def unpack(archiveName, destination)
{
   powershell(label: "Unpack zip file", script: "Expand-Archive -Path \"${archiveName}\" -DestinationPath \"${destination}\"");
}