def sync(credential, workspace)
{
   p4sync charset: 'none', credential: credential, format: 'jenkins-${JOB_NAME}', populate: autoClean(delete: false, modtime: false, parallel: [enable: false, minbytes: '1024', minfiles: '1', threads: '4'], pin: '', quiet: true, replace: true, tidy: false), source: templateSource(workspace)
}

def createTicket(credential, p4host)
{
   def ticket = ""
   withCredentials([usernamePassword(credentialsId: credential, passwordVariable: 'P4PASS', usernameVariable: 'P4USER')]) {
      bat (script: "echo %P4PASS%| p4 -p ${p4host} -u %P4USER% trust -y")
      def result = bat(script: "echo %P4PASS%| p4 -p ${p4host} -u %P4USER% login -ap", returnStdout: true)
      echo result
	  ticket = result.tokenize().last()
   }
   
   return ticket
}

def withSwarm(credentials, p4Port, client, mapping, Closure body){
        withSwarmUrl(env.P4USER,env.P4CLIENT,env.P4MAPPING)
        { 
            url,user->
            def p4Ticket = ticket(credentials,p4Port)
            body(user,p4Ticket,url)
        }
}