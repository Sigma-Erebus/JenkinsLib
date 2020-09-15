def sync(credential, workspace, forceCleanws = true)
{
   if (forceCleanws)
   {
      p4sync charset: 'none', credential: credential, format: ${JOB_NAME}, populate: forceClean(have: false, parallel: [enable: true, minbytes: '1024', minfiles: '1', threads: '4'], pin: '', quiet: true), source: templateSource(workspace)
   }
   else
   {
      p4sync charset: 'none', credential: credential, format: ${JOB_NAME}, populate: autoClean(delete: true, modtime: false, parallel: [enable: true, minbytes: '1024', minfiles: '1', threads: '4'], pin: '', quiet: true, replace: true, tidy: true), source: templateSource(workspace)
   }
}