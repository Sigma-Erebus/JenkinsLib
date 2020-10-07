def pack(source, archiveName)
{
   bat(label: "Pack into zip file", script: "7z a \"${archiveName}.zip\" \"${source}\\*\"")
}