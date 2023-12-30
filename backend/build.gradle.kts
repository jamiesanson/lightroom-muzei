// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
tasks.create<Exec>("deployGateway") {
    workingDir = project.file("scripts")
    commandLine = listOf("./deploy-gateway")
}
