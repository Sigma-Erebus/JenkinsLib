def pack(source, archiveName)
{
   powershell(label: "Pack into zip file", script: "Compress-Archive -Path \"${source}\" -DestinationPath \"${archiveName}.zip\"");
}