def sync(credential, repoURL, includeChangeLog = true, shouldPoll = true)
{
   git changelog: ${includeChangeLog}, credentialsId: '${credential}', poll: ${shouldPoll}, url: '${repoURL}'
}