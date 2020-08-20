package de.nenick.gradle.plugins.jacoco.android

import de.nenick.gradle.test.tools.PluginTest
import de.nenick.gradle.test.tools.taskDependenciesAsStrings
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.kotlin.dsl.getByType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.*

class JacocoAndroidConfigPluginTest : PluginTest() {

    private val pluginId = "de.nenick.jacoco-android-config"

    @BeforeEach
    fun setup() {
        project = AndroidProject().withPlugin(JacocoAndroidConfigPlugin::class)
    }

    @Test
    override fun `applies by plugin id`() {
        project = AndroidProject().withPlugin(pluginId)
        expectThat(project.plugins).one { isA<JacocoAndroidConfigPlugin>() }
    }

    @Test
    fun `applies jacoco plugin`() {
        expectThat(project.plugins).one { isA<JacocoPlugin>() }
        expectThat(project.extensions.getByType<JacocoPluginExtension>().toolVersion).isEqualTo("0.8.5")
    }

    @Nested
    inner class JacocoTestReportTask {

        @Test
        fun `add the default jacoco report task`() {
            expectThat(project.tasks.matching { true }.map { it.name }) {
                contains("jacocoTestReport")
            }
        }

        @Test
        fun `jacoco report task depends on connected android tests`() {
            expectThat(jacocoReportTask().taskDependenciesAsStrings) {
                contains("task ':connectedDebugAndroidTest'")
            }
        }

        @Test
        fun `apply plugin to pure kotlin project`() {
            // Within pure kotlin projects jacoco create his own jacocoTestReport task automatically.
            expectThrows<PluginApplicationException> { givenKotlinProject { plugins.apply(pluginId) } }
                .message.isEqualTo("Failed to apply plugin [id '$pluginId']")
        }
    }

    override fun givenEmptyProjectWithPluginApplied(setup: Project.() -> Unit) {
        givenEmptyProject { plugins.apply(JacocoAndroidConfigPlugin::class.java) }
    }

    private fun jacocoReportTask() = project.tasks.getByName("jacocoTestReport")
}