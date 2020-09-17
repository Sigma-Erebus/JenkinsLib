def sync(credential, workspace)
{
   p4sync charset: 'none', credential: credential, format: 'jenkins-${JOB_NAME}', populate: autoClean(delete: false, modtime: false, parallel: [enable: false, minbytes: '1024', minfiles: '1', threads: '4'], pin: '', quiet: true, replace: true, tidy: false), source: templateSource(workspace)
}

def createTicket(credential, p4host)
{
   def ticket = ""
   withCredentials([usernamePassword(credentialsId: 'credential', passwordVariable: 'P4PASS', usernameVariable: 'P4USER')]) {
      bat(script: 'FOR /F "tokens=*" %%F IN ('"echo %P4PASS%| p4 -p ssl:swarm2.buas.nl:1667 -u %P4USER% login -ap"') do SET ${ticket}=%%F')
   }
   
   return ticket
}