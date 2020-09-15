def pack(source, archiveName)
{
   bat(script: "CALL 7z a \"${archiveName}.zip\" \"${source}\*\"")
}