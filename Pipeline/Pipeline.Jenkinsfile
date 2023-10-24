// Fetch Shared Library from Github
library identifier: 'JenkinsLib@master',
    retriever: modernSCM([
      $class: 'GitSCMSource',
      credentialsId: '', // Public repo, no credentials needed
      remote: 'https://github.com/Sigma-Erebus/JenkinsLib'
    ])

def boolean dds_auth_timeout = false

pipeline {
    agent {
        // Set custom Workspace
        node {
            label "Win64"
            customWorkspace "C:\\Jenkins\\${env.JOB_NAME}"
        }
    }
    environment {                                                               // Note for Credentials: Credentials are stored per folder, and are available to pipelines in that folder and it's subfolders. Please keep Credentials as local to the pipeline as possible. (e.g. only your team needs it, place it in your team folder, entirety of your year needs it, place it in your year folder.)
        // Perforce
        P4USER = "ce8c344f-646e-45da-ab58-32b0cb723d0f"                         // Credentials ID to a perforce user credential (that has access to the depot of the project)
        P4HOST = "ssl:perforce.buas.nl:1666"                                    // Perforce Server - No need to change
        P4WORKSPACE = "Hidde200170_JenkinsTest"                                 // Name of the Workspace it should use (best to make and assign a workspace for use just by Jenkins)
        P4MAPPING = "//Hidde200170/... //Hidde200170_Jenkins/Hidde200170/..."   // Mapping view of the workspace (Please exclusively map the folder containing the .uproject file, since anything beyond that is unnecessary download time)
        
        // Unreal Engine 5
        ENGINEROOT = "${env.UE53DIR}"                                           // Root of the engine - Check Teams Channel for available versions and their respective paths
        PROJECT = "${env.WORKSPACE}\\Hidde200170\\JnknsTst\\JnknsTst.uproject"  // Path to .uproject file relative to the workspace
        PROJECTDIR = "${env.WORKSPACE}/Hidde200170/JnknsTst/"                   // Path to the folder containing the .uproject file relative to the workspace
        PROJECTNAME = "JnknsTst"                                                // Name of the project
        OUTPUTDIR = "${env.WORKSPACE}\\Output"                                  // Output directory - Don't change
        
        // Discord
        WEBHOOK_BUILD = "https://discord.com/api/webhooks/1061783301481832448/JEWoUs7z_W0aJrvXzCD8gtw66yjW8QQDKP7X7D_PaMsuBe-1Xs03KjfXZjnATR1P-NSd"     // Discord Webhook URL so that update messages can be sent to your discord server
        
        // Steam
        STEAMUSR = "4dcb6cd0-69ea-4f18-8f00-c8333db90524"                       // Credentials ID to a steam user/password credential (that has permission to upload the game to steam)
        
        // Google Drive
        GDAUTH = "a877727d-88c3-4ae3-b7fb-c374c64cf3a6"                         // Credentials ID for Jenkins' GDrive Daemon - Don't Change
        GOOGLEDRIVEID = "1uiXReW78bGFpXryfyqmuDfoXoUDDKuX_"                     // ID of your team's shared google drive folder where you want the Daemon to upload to (ID is the last part of the URL, and make sure that daemon@jenkins-buas.iam.gserviceaccount.com has permission to add files to said folder)
        UPLOADSIZEMULTIPLIER = "16"                                             // Multiplier to packet size when uploading - No need to change - though might increase/decrease the speed of GDrive uploads
        
        // Itch.io
        BUTLER_API_KEY = "ziib04jySa6Vb5n9BgzMbIZ8oAaRylKYoK1lGqDn"             // API key for butler to identify if the application has authority to upload to a certain project (uses Environment Variable directly, so cannot use credentials...)
        BUTLERTARGET = "sigma-erebus/testproject:win"                           // user and project to target when uploading
        
        // General
        CLEANWORKSPACE = true                                                   // Clean workspace - Don't change
    }
    stages {
        stage('P4-Setup') {
            steps {
                script {
                    def newestChangeList = p4v.initGetLatestCL(env.P4USER, env.P4HOST)
                    log("NEWEST CHANGELIST: ${newestChangeList}")
                    log.currStage()
                    p4v.init(env.P4USER, env.P4HOST, env.P4WORKSPACE, env.P4MAPPING, '138398', !env.CLEANWORKSPACE)
                    log("P4 synced to: ${env.P4_CHANGELIST}")
                }
            }
        }
        stage("UE5-Build") {
            steps {
                script {
                    log.currStage()
                    win.makeWritable(env.PROJECTDIR)
                    python.runScript("C:\\Users\\Administrator\\Desktop\\Scripts\\MatchBuildID.py", "${env.PROJECTDIR}\" \"${env.ENGINEROOT}\\\" \"false")
                    if(env.PLATFORM == "PS5") {
					    win.movePathFiles("\"${PROJECTDIR}Content\\Images\"", "\"${ENGINEROOT}Engine\\Platforms\\PS5\\Build\\sce_sys\"")
					}
                    ue5.buildPrecompiledProject(env.ENGINEROOT, env.PROJECTNAME, env.PROJECT, env.CONFIG, env.PLATFORM, env.OUTPUTDIR)
                }
            }
        }
        stage("Automation-Tests") {
            when { 
                expression { env.RUNTESTS.toBoolean() }
            }
            steps {
                script {
                    log.currStage()
                    ue5.runNamedTests(["Project"], env.CONFIG)                  // Tests to run
                }
            }
        }
        stage("Primary-DDS-Deployment") {
            when {
                expression { env.DEPLOYTODDS.toBoolean() }
            }
            steps {
                script {
                    log.currStage()
                    def platformDir = env.PLATFORM
                    if(env.PLATFORM == "Win64") {
                        platformDir = "Windows"
                    }
                    if(env.DDS == "Steam") {
                        if(currentBuild.result == "UNSTABLE") {
                            log.warning("====!!==== UNSTABLE BUILD - PUSH TO STEAM ABORTED, PUSHING TO GDRIVE REGARDLESS OF CONFIG TO ALLOW DEBUG ====!!====")
                        }
                        else if(env.PLATFORM == "PS5") {
                            log.warning("====!!==== PS5 BUILD - NOT ALLOWED TO UPLOAD TO STEAM ====!!====")
                        }
                        else
                        {
                            discord.sendMessage(discord.createMessage(
                            "Started push to steam",
                            "white",
                            [[name:"Push to Steam has been initiated, SteamGuard Authorization might be required", 
                            value:"${env.BUILD_URL}"],
                            [name:"Authorizer:", 
                            value:"<@178828134408388608>"]],
                            [text:"${env.JOB_BASE_NAME}"])
                            , env.WebHook_BUILD)
                            
                            steam.init(env.STEAMUSR, env.STEAMCMD)
                            
                            def appManifest = steam.createAppManifest("2163100", "2163101", "", "${env.JOB_BASE_NAME}", false, "", "${env.STEAMBRANCH}", env.OUTPUTDIR)
                            
                            steam.createDepotManifest("2163101", "${env.OUTPUTDIR}\\${platformDir}")
                            
                            try {
                                steam.tryDeploy("${env.WORKSPACE}\\${appManifest}")
                            }
                            catch (Exception e) {
                                dds_auth_timeout = true
                                
                                discord.sendMessage(discord.createMessage(
                                "Steam Authorization Timed Out",
                                "white",
                                [[name:"Steam Authorization was not provided in time, Build has not been uploaded to steam, will be uploaded to GDrive instead", 
                                value:"${env.BUILD_URL}"]],
                                [text:"${env.JOB_BASE_NAME}"])
                                , env.WebHook_BUILD)
                                
                                catchError(stageResult: 'UNSTABLE', buildResult: currentBuild.result) {
                                error("Mark Stage as Skipped")
                                }
                            }
                        }
                    }
                    if(env.DDS == "Itch.io") {
                        if(currentBuild.result == "UNSTABLE") {
                        log.warning("====!!==== UNSTABLE BUILD - PUSH TO ITCH ABORTED, PUSHING TO GDRIVE REGARDLESS OF CONFIG TO ALLOW DEBUG ====!!====")
                        }
                        else if(env.PLATFORM == "PS5") {
                            log.warning("====!!==== PS5 BUILD - NOT ALLOWED TO UPLOAD TO ITCH ====!!====")
                        }
                        else {
                            itch.upload("${env.BUTLER}", env.BUTLER_API_KEY, "\"${env.OUTPUTDIR}\\${platformDir}\"", "${env.BUTLERTARGET}")
                        }
                    }
					
                }
            }
        }
        stage("GDrive-Deployment") {
            when {
                expression { env.GOOGLEDRIVEUPLOAD.toBoolean() || (currentBuild.result == "UNSTABLE" && env.DEPLOYTODDS.toBoolean()) || dds_auth_timeout }
            }
            steps {
                script {
                    log.currStage()
				    if(env.PLATFORM == "Win64") {
				        zip.pack("${env.OUTPUTDIR}\\Windows", "${env.JOB_BASE_NAME}_${env.BUILD_NUMBER}", use7z = false)
				    }
				    else {
				        zip.pack("${env.OUTPUTDIR}\\${env.PLATFORM}", "${env.JOB_BASE_NAME}_${env.BUILD_NUMBER}", use7z = false)
				    }
                    withCredentials([file(credentialsId: 'a877727d-88c3-4ae3-b7fb-c374c64cf3a6', variable: 'SECRETFILE')]) {
                    python.runScript("${env.PYTHONGDRIVEUPLOAD}", "${SECRETFILE}\" \"${env.WORKSPACE}\\${env.JOB_BASE_NAME}_${env.BUILD_NUMBER}.zip\" \"${env.JOB_BASE_NAME}_${env.BUILD_NUMBER}.zip\" \"${env.GOOGLEDRIVEID}\" ${env.UPLOADSIZEMULTIPLIER}")
                    }
                }
            }
        }
    }
    post {
        success {
            script {
                log("Build Succeeded!")
                discord.succeeded(env.CONFIG, "(${env.PLATFORM})", env.WEBHOOK_BUILD)
                
            }
        }
        unstable {
            script {
                log("Build Unstable!")
                discord.unstable(env.CONFIG, "(${env.PLATFORM})", env.WEBHOOK_BUILD)
            }
        }
        failure {
            script {
                log("Build Failed!")
                discord.failed(env.CONFIG, "(${env.PLATFORM})", env.WEBHOOK_BUILD)
            }
        }
        aborted {
            script {
                log("After Abort actions")
                if(env.CLEANWORKSPACE.toBoolean()) {
                    cleanWs()
                }
            }
        }
        cleanup {
            script {
                log("Cleanup actions")
                if(env.CLEANWORKSPACE.toBoolean()) {
                    cleanWs()
                }
            }
        }
    }
}
