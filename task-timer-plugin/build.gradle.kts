plugins {
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.romaniuk"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("taskTimerPlugin") {
            id = "com.romaniuk.task-timer-plugin"
            implementationClass = "com.romaniuk.TaskTimerPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            groupId = "com.romaniuk"
            artifactId = "task-timer-plugin"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}