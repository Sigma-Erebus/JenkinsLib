def pack(source, archiveName)
{
   bat(script: "CALL \"C:\Program Files\7-Zip\7z.exe\" a ${archiveName}.zip ${source}")
}