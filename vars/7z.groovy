def pack(source, archiveName)
{
   bat(script: "7z a \"${source}.zip\" \"${source}\\*\"")
}