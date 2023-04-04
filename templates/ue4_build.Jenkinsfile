// Fetch Shared Library from Github
library identifier: 'JenkinsLib@master',
    retriever: modernSCM([
      $class: 'GitSCMSource',
      credentialsId: '', // Public repo, no credentials needed
      remote: 'https://github.com/Sigma-Erebus/JenkinsLib'
    ])

pipeline {
    agent {
        // Set custom Workspace
        node {
            label ""
            customWorkspace "C:\\Jenkins\\${env.JOB_NAME}"
        }
    }
    environment {
        // Perforce
        P4USER = "<CREDENTIAL_ID>"
        P4HOST = "<HOST>"
        P4WORKSPACE = "<WORKSPACE_NAME>"
        P4MAPPING = "<WORKSPACE_MAPPING>"
        
        // Unreal Engine 4
        ENGINEROOT = "C:\\Program Files\\Epic Games\\UE_4.25\\"
        PROJECT = "${env.WORKSPACE}\\<PROJECT>"
        PROJECTNAME = "<PROJECT_NAME>"
        OUTPUTDIR = "${env.WORKSPACE}\\Output"
        
        // Configuration
        CONFIG = "Development" // Target list: Unknown, Debug, DebugGame, Development, Shipping, Test
        PLATFORM = "Win64" // Platform list: Win32, Win64, Linux, Switch, PS4
        
        // Discord
        WEBHOOK_BUILD = "<DISCORD_WEBHOOK>"
    }
    stages {
        stage('p4-setup') {
            steps {
                script {
                    log.currStage()
                    p4v.init(env.P4USER, env.P4HOST, env.P4WORKSPACE, env.P4MAPPING)
                }
            }
        }
        stage("ue4-build") {
            steps {
                script {
                    log.currStage()
                    ue4.build(env.ENGINEROOT, env.PROJECTNAME, env.PROJECT, env.CONFIG, env.PLATFORM, env.OUTPUTDIR)
                }
            }
        }
    }
    post {
        success {
            script {
                log("Build succeeded")
                discord.succeeded(env.CONFIG, env.PLATFORM, env.WEBHOOK_BUILD)
                
            }
        }
        failure {
            script {
                log("Build failed")
                discord.failed(env.CONFIG, env.PLATFORM, env.WEBHOOK_BUILD)
            }
        }
        aborted {
            cleanWs()
        }
        cleanup {
            cleanWs()
        }
    }
}
