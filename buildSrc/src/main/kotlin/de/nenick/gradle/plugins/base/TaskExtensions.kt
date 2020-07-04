package de.nenick.gradle.plugins.base

import org.gradle.api.Task
import java.io.File

fun Task.whenBuildSrcExist(found: (directory: File) -> Unit) {
    val buildSrcDir = File("${project.projectDir}/buildSrc")
    if (buildSrcDir.exists()) {
        found(buildSrcDir)
    }
}