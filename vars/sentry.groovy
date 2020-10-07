def upload(sentryCLIPath, authToken, organisation, project, outputFolder)
{
   bat(label: "Upload debug symbols to Sentry", script: "\"${sentryCLIPath}\" --auth-token ${authToken} upload-dif -o ${organisation} -p ${project} ${outputFolder}")
}