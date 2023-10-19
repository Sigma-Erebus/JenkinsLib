def p4Info = null

// Must be called first before calling other functions
def init(p4credential, p4host, p4workspace, p4viewMapping, version = '', cleanForce = false)
{
   p4Info = [credential: p4credential, host: p4host, workspace: p4workspace, viewMapping: p4viewMapping]
   if (cleanForce)
   {
      p4sync charset: 'none', credential: p4Info.credential, format: 'jenkins-${JOB_NAME}', populate: forceClean(have: false, parallel: [enable: true, minbytes: '1024', minfiles: '1', threads: '4'], pin: version, quiet: true), source: templateSource(p4Info.workspace)
   }
   else
   {
      p4sync charset: 'none', credential: p4Info.credential, format: 'jenkins-${JOB_NAME}', populate: autoClean(delete: false, modtime: false, parallel: [enable: false, minbytes: '1024', minfiles: '1', threads: '4'], pin: version, quiet: true, replace: true, tidy: false), source: templateSource(p4Info.workspace)
   }
}

def pureinit(p4credential, p4host, p4workspace, p4viewMapping)
{
   p4Info = [credential: p4credential, host: p4host, workspace: p4workspace, viewMapping: p4viewMapping]
}

def puresync(version = '', cleanForce = false)
{
   if (cleanForce)
   {
      p4sync charset: 'none', credential: p4Info.credential, format: 'jenkins-${JOB_NAME}-${NODE_NAME}', populate: forceClean(have: false, parallel: [enable: true, minbytes: '1024', minfiles: '1', threads: '4'], pin: version, quiet: true), source: depotSource(p4Info.viewMapping)
   }
   else
   {
      p4sync charset: 'none', credential: p4Info.credential, format: 'jenkins-${JOB_NAME}-${NODE_NAME}', populate: autoClean(delete: false, modtime: false, parallel: [enable: false, minbytes: '1024', minfiles: '1', threads: '4'], pin: version, quiet: true, replace: true, tidy: false), source: depotSource(p4Info.viewMapping)
   }
}

def initGetLatestCL(p4credential, p4host)
{
   p4Info = [credential: p4credential, host: p4host]
   def p4s = p4(credential: p4Info.credential, workspace: manualSpec(charset: 'none', cleanup: false, name: 'Jenkins-${NODE_NAME}', pinHost: false, spec: clientSpec(allwrite: true, backup: true, changeView: '', clobber: true, compress: false, line: 'LOCAL', locked: false, modtime: false, rmdir: false, serverID: '', streamName: '', type: 'WRITABLE', view: '')))
   def changes = p4s.run('changes', '-s', 'submitted', '-m1')
   def change = ""
   for (def item : changes) {
      for (String key : item.keySet()) {
         if (key == "change") {
            change = item.get(key)
         }
         //value = item.get(key)
         //println ("Key: " + key + " Value: " + value)
      }
   }
   return change
}

def clean()
{
   def p4s = p4(credential: p4Info.credential, workspace: manualSpec(charset: 'none', cleanup: false, name: p4Info.workspace, pinHost: false, spec: clientSpec(allwrite: true, backup: true, changeView: '', clobber: false, compress: false, line: 'LOCAL', locked: false, modtime: false, rmdir: false, serverID: '', streamName: '', type: 'WRITABLE', view: p4Info.viewMapping)))
   p4s.run('revert', '-c', 'default', '//...')
   p4Info.clear()
}

def createTicket()
{
   def ticket = ""
   withCredentials([usernamePassword(credentialsId: p4Info.credential, passwordVariable: 'P4PASS', usernameVariable: 'P4USER')]) 
   {
      bat (label: "Trust connection", script: "echo %P4PASS%| p4 -p ${p4Info.host} -u %P4USER% trust -y")
      def result = bat(label: "Create P4 ticket", script: "echo %P4PASS%| p4 -p ${p4Info.host} -u %P4USER% login -ap", returnStdout: true)
      ticket = result.tokenize().last()
   }
   
   return ticket
}

def unshelve(id)
{
   p4unshelve credential: p4Info.credential, ignoreEmpty: false, resolve: 'none', shelf: id, tidy: false, workspace: manualSpec(charset: 'none', cleanup: false, name: p4Info.workspace, pinHost: false, spec: clientSpec(allwrite: false, backup: true, changeView: '', clobber: true, compress: false, line: 'LOCAL', locked: false, modtime: false, rmdir: false, serverID: '', streamName: '', type: 'WRITABLE', view: p4Info.viewMapping))
} 

def getChangelistDescr(id)
{
   def p4s = p4(credential: p4Info.credential, workspace: manualSpec(charset: 'none', cleanup: false, name: p4Info.workspace, pinHost: false, spec: clientSpec(allwrite: true, backup: true, changeView: '', clobber: false, compress: false, line: 'LOCAL', locked: false, modtime: false, rmdir: false, serverID: '', streamName: '', type: 'WRITABLE', view: p4Info.viewMapping)))
   def changeList = p4s.run('describe', '-s', '-S', "${id}")
   def desc = ""

   for (def item : changeList) 
   {
      for (String key : item.keySet()) 
      {
         if (key == "desc")
         {
            desc = item.get(key)
         }
      }
   }

   return desc
}

def getCurrChangelistDescr()
{
   return getChangelistDescr(env.P4_CHANGELIST)
}

def publish(p4credential, p4host, p4workspace, p4viewMapping)
{
   p4Info = [credential: p4credential, host: p4host, workspace: p4workspace, viewMapping: p4viewMapping]
   p4publish credential: p4Info.credential, publish: submit(delete: false, description: 'Submitted by Jenkins. Build: ${BUILD_TAG}', modtime: false, onlyOnSuccess: true, paths: '', purge: '', reopen: false), workspace: staticSpec(charset: 'none', name: p4Info.workspace, pinHost: false)
}
