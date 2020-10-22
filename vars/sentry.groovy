def upload(sentryCLIPath, authToken, organisation, project, outputFolder)
{
   try
   {
      bat(label: "Upload debug symbols to Sentry", script: "\"${sentryCLIPath}\" --auth-token ${authToken} upload-dif -o ${organisation} -p ${project} ${outputFolder}")
   }
   catch(err) 
   {
      log.error("Sentry upload failed. This could be due to Sentry being offline.")
   }
}