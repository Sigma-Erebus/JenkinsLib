// Fetch Shared Library from Github
library identifier: 'JenkinsSharedLib@master',
    retriever: modernSCM([
      $class: 'GitSCMSource',
      credentialsId: '', // Public repo, no credentials needed
      remote: 'https://github.com/DavidtKate/JenkinsSharedLib'
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
        
        // Visual Studio
        MSBUILD = "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\MSBuild\Current\Bin\amd64\msbuild.exe"
        PROJECT = "${env.WORKSPACE}\\<PROJECT>"
        
        // Configuration
        CONFIG = "Debug"
        PLATFORM = "x64"
        
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
        stage("vs-build") {
            steps {
                script {
                    log.currStage()
                    vs.build(env.MSBUILD, env.PROJECT, env.CONFIG, env.PLATFORM)
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