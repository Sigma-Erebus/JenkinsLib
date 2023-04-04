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
        
        // Unreal Engine 5
        ENGINEROOT = "C:\\Program Files\\Epic Games\\UE_5.0\\"
        PROJECT = "${env.WORKSPACE}\\<PROJECT>"
        PROJECTNAME = "<PROJECT_NAME>"
        OUTPUTDIR = "${env.WORKSPACE}\\Output"
        
        // Configuration
        CONFIG = "Development"
        PLATFORM = "Win64"
        
        // Discord
        WEBHOOK_REVIEW = "<DISCORD_WEBHOOK>"
        
        // Swarm
        SWARMURL = "https://<SWARMHOST>:<SWARMPORT>"
        SWARMUSER = "<SWARMID>"
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
        stage("review-setup") {
            steps {
                script {
                    log.currStage()
                    
                    // Swarm operations
                    def ticket = p4v.createTicket()
                    swarm.init(env.SWARMUSER, ticket, env.SWARMURL)
                    
                    withCredentials([file(credentialsId: 'groups', variable: 'GROUPS')]) 
                    {
                        def groups = readFile(file: env.GROUPS)
                        def participants = env.PARTICIPANTS.split("-")
                        
                        def reviewers = swarm.getParticipantsOfGroups(participants, groups)
                        def reviewInfo = swarm.createReview(env.CHANGELIST_ID, reviewers)
                        env.REVIEW_ID = swarm.getReviewID(reviewInfo)
                        env.REVIEW_AUTHOR = swarm.getReviewAuthor(reviewInfo)
                    }
                }
            }
        }
        stage("ue5-build") {
            steps {
                script {
                    log.currStage()
                    
                    // Unshelve files before building
                    p4v.unshelve(env.CHANGELIST_ID)
                    
                    // Build unreal project
                    ue5.buildBueprintProject(env.ENGINEROOT, env.PROJECTNAME, env.PROJECT, env.CONFIG, env.PLATFORM, env.OUTPUTDIR)
                }
            }
        }
    }
    post {
        success {
            script {
                log("Build succeeded")
                
                withCredentials([file(credentialsId: 'groups', variable: 'GROUPS')]) 
                {
                    def groups = readFile(file: env.GROUPS)
                    def participants = env.PARTICIPANTS.split("-")
                    def author = discord.swarmIDtoDiscordID(env.REVIEW_AUTHOR, groups)
                    
                    discord.newReview(
                    env.REVIEW_ID,
                    "<@${author}>",
                    env.SWARMURL,
                    env.WEBHOOK_REVIEW,
                    "passed :white_check_mark:",
                    """
                    ${discord.mentionGroups(participants, groups)}
                    """
                    )
                }
            }
        }
        failure {
            script {
                log("Build failed")
                swarm.reject(env.REVIEW_ID)
    
                withCredentials([file(credentialsId: 'groups', variable: 'GROUPS')]) 
                {
                    def groups = readFile(file: env.GROUPS)
                    def participants = env.PARTICIPANTS.split("-")
                    def author = discord.swarmIDtoDiscordID(env.REVIEW_AUTHOR, groups)
                    
                    discord.newReview(
                    env.REVIEW_ID,
                    "<@${author}>",
                    env.SWARMURL,
                    env.WEBHOOK_REVIEW,
                    "failed :x:"
                    )
                }
            }
        }
        aborted {
            p4v.clean()
            cleanWs()
        }
        cleanup {
            script {
                p4v.clean()
                cleanWs()
            }
        }
    }
}
