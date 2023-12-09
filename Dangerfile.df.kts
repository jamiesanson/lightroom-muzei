// Copyright (C) 2023, Jamie Sanson
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("ktlint")
import systems.danger.kotlin.*

danger(args) {
    onGitHub {
        // Big PR Check
        if ((pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > 300) {
            warn("Big PR, try to keep changes smaller if you can")
        }

        // Work in progress check
        if (pullRequest.title.contains("WIP", false)) {
            warn("PR is classed as Work in Progress")
        }
    }
}
