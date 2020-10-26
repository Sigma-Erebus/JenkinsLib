def pack(source, archiveName, use7z = true)
{
   if (use7z)
   {
      bat(label: "Pack into zip file using 7z", script: "7z a \"${archiveName}.zip\" \"${source}\\*\"")
   }
   else
   {
      powershell(label: "Pack into zip file using powershell", script: "Compress-Archive -Path \"${source}\" -DestinationPath \"${archiveName}.zip\"");
   }
}

def unpack(archiveName, destination, use7z = true)
{
   if (use7z)
   {
      bat(label: "Unpack zip file using 7z", script: "7z e \"${archiveName}.zip\" \"${destination}\"")
   }
   else
   {
      powershell(label: "Unpack zip file using powershell", script: "Expand-Archive -Path \"${archiveName}.zip\" -DestinationPath \"${destination}\"");
   }
}