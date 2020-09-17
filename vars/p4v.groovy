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

def upVote(review,user,ticket,swarm_url){
    swarm.init(user,ticket,swarm_url)
    swarm.upVote(review)
    swarm.clear()
}

def withSwarm(credentials, p4Port, client, mapping, Closure body){
        withSwarmUrl(env.P4USER,env.P4WORKSPACE,env.P4MAPPING)
        { 
            url,user->
            def p4Ticket = createTicket(credentials,p4Port)
            body(user,p4Ticket,url)
        }
}

def withSwarmUrl(credentials,client,mapping,Closure body){
        withCredentials([usernamePassword(credentialsId: credentials, passwordVariable: 'p4USERPASS', usernameVariable: 'p4USER' )]) {
            def url = swarmUrl(credentials,client,mapping)
            body(url,env.p4USER,env.p4USERPASS)
        }
}

def swarmUrl(credential,client,mapping){
    def p4s = p4(credential: credential, workspace: manualSpec(charset: 'none', cleanup: false, name: client, pinHost: false, spec: clientSpec(allwrite: true, backup: true, changeView: '', clobber: false, compress: false, line: 'LOCAL', locked: false, modtime: false, rmdir: false, serverID: '', streamName: '', type: 'WRITABLE', view: mapping)))
    def prop = p4s.run("property","-l","-n","P4.Swarm.URL")
    for(def item : prop) {
        for (String key : item.keySet()) {
            if(key == "value")
            {
                return item.get(key)
            }
        }
    }
    return ""
}