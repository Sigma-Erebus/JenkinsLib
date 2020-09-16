def pack(source, archiveName)
{
   bat(script: "7z a \"${archiveName}.zip\" \"${source}\\*\"")
}