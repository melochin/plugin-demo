plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.0.0"
}

group = "com.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    buildSearchableOptions.set(false)

    pluginConfiguration {
        id.set("com.demo.my.plugin")
        name.set("My Plugin")
        version.set("1.0.0")
        description.set("This is test plugin")
        changeNotes.set("No change")
        vendor.name.set("test")
        vendor.email.set("test@com")
        vendor.url.set("test.com")

        ideaVersion {
            sinceBuild.set("241")
            untilBuild.set("241.*")
        }
    }

}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.6")
        bundledPlugins("com.intellij.java")
        instrumentationTools()
    }
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    prepareSandbox {
        doFirst{
            println(destinationDir)
            delete(fileTree(destinationDir))
        }
    }

    runIde {
        jvmArgs = listOf(
            "-XX:+UnlockDiagnosticVMOptions"
        )
    }
}