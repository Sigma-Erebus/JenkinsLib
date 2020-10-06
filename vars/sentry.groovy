def upload(sentryCLIPath, authToken, organisation, project, outputFolder)
{
   bat(script: "\"${sentryCLIPath}\" --auth-token ${authToken} upload-dif -o ${organisation} -p ${project} ${outputFolder}")
}