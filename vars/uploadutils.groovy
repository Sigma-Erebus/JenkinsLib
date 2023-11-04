def doesUploadTargetExist(ProjectConfig, UploadManifest) {
    def gameTargets = ProjectConfig.GameTargets.split(';')
    def clientTargets = ProjectConfig.ClientTargets.split(';')
    def serverTargets = ProjectConfig.ServerTargets.split(';')
    def gamePlatforms = ProjectConfig.GamePlatforms.split(';')
    def clientPlatforms = ProjectConfig.ClientPlatforms.split(';')
    def serverPlatforms = ProjectConfig.ServerPlatforms.split(';')
    def gameConfigs = ProjectConfig.GameConfigurations.split(';')
    def clientConfigs = ProjectConfig.ClientConfigurations.split(';')
    def serverConfigs = ProjectConfig.ServerConfigurations.split(';')
    def manifestTarget = UploadManifest.Target
    def manifestPlatform = UploadManifest.Platform
    def manifestConfig = UploadManifest.Configuration
    def manifestType = UploadManifest.Type
    def exists = false
    switch(manifestType) {
        case "Game":
            if(gameTargets.contains(manifestTarget) && gamePlatforms.contains(manifestPlatform) && gameConfigs.contains(manifestConfig)) {
                exists = true
            }
        case "Client":
            if(clientTargets.contains(manifestTarget) && clientPlatforms.contains(manifestPlatform) && clientConfigs.contains(manifestConfig)) {
                exists = true
            }
        case "Server":
            if(serverTargets.contains(manifestTarget) && serverPlatforms.contains(manifestPlatform) && serverConfigs.contains(manifestConfig)) {
                exists = true
            }
    }
    return exists
}
