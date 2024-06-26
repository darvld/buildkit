plugins {
  id("java-gradle-plugin")
  `kotlin-dsl`

  alias(libs.plugins.buildconfig)
  alias(libs.plugins.buildTimeTracker)
  alias(libs.plugins.kotlinx.abiValidator)
}

// use overridable options for publishing information (see gradle.properties)
// in CI for example, the "SNAPSHOT" variant may be added to the version
group = property("buildkit.group").toString()
version = property("buildkit.version").toString()
description = "Buildkit plugin for Gradle projects"

// resolve the name and full ID for the project plugin
val projectPluginId = "$group.${property("buildkit.project-plugin-id")}"
val projectPluginName = property("buildkit.project-plugin-name").toString()

gradlePlugin {
  plugins.create(projectPluginName) {
    id = projectPluginId
    implementationClass = "dev.elide.buildkit.BuildkitPlugin"

    displayName = "BuildKit"
    description = "A collection of utilities for authoring Gradle builds"
  }
}

kotlin {
  explicitApi()
}

dependencies {
  testImplementation(libs.junit.params)
  testImplementation(gradleTestKit())
  testImplementation(kotlin("test"))
}

// values used during env-based option tests
val testEnvKey = "TEST_ENV"
val testEnvValue = "TEST_ENV_VALUE"

buildConfig {
  packageName = "dev.elide.buildkit"

  sourceSets.named("test") {
    className = "TestConstants"

    buildConfigField("String", "TEST_ENV_KEY", "\"$testEnvKey\"")
    buildConfigField("String", "TEST_ENV_VALUE", "\"$testEnvValue\"")

    buildConfigField("String", "PLUGIN_ID", "\"$projectPluginId\"")
    buildConfigField("String", "PLUGIN_VERSION", "\"$version\"")
  }
}

tasks.test {
  useJUnitPlatform()

  // set the env expected by some test cases
  environment(testEnvKey, testEnvValue)
}
