tasks.create<Exec>("deployGateway") {
    workingDir = project.file("scripts")
    commandLine = listOf("./deploy-gateway")
}
