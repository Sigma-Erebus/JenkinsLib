def sync(credential, workspace)
{
   p4sync charset: 'none', credential: credential, format: 'jenkins-${JOB_NAME}', populate: autoClean(delete: false, modtime: false, parallel: [enable: false, minbytes: '1024', minfiles: '1', threads: '4'], pin: '', quiet: true, replace: true, tidy: false), source: templateSource(workspace)
}

def createTicket(credential, p4host)
{
   def ticket = ""
   withCredentials([usernamePassword(credentialsId: credential, passwordVariable: 'P4PASS', usernameVariable: 'P4USER')]) 
   {
      bat (script: "echo %P4PASS%| p4 -p ${p4host} -u %P4USER% trust -y")
      def result = bat(script: "echo %P4PASS%| p4 -p ${p4host} -u %P4USER% login -ap", returnStdout: true)
      ticket = result.tokenize().last()
   }
   
   return ticket
}

def getCurrChangelistDescr(credential, name, viewMapping)
{
    def p4 = p4(credential: credential, workspace: manualSpec(charset: 'none', cleanup: false, name: name, pinHost: false, spec: clientSpec(allwrite: true, backup: true, changeView: '', clobber: false, compress: false, line: 'LOCAL', locked: false, modtime: false, rmdir: false, serverID: '', streamName: '', type: 'WRITABLE', view: viewMapping)))
    def list = p4.run('describe', '-s', '-S', "${env.P4_CHANGELIST}")
    def desc = ""
    for (def item : list) 
    {
        for (String key : item.keySet()) 
        {
            if(key == "desc")
            {
		        desc = item.get(key)
		        break
            }
        }
	}
	return desc
}