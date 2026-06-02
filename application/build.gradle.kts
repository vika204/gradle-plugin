import java.text.SimpleDateFormat
import java.util.Date

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.romaniuk:task-timer-plugin:1.0.0")
    }
}

apply(plugin = "com.romaniuk.task-timer-plugin")

plugins {
    application
}

tasks.register("archivePerformanceReports") {
    group = "reporting"
    description = "Archives the performance report with a timestamp."

    mustRunAfter("generatePerformanceReport")

    doLast {
        val sourceFile = file("${layout.buildDirectory.get()}/reports/performance-report.txt")

        if (!sourceFile.exists()) {
            println("[WARNING] performance-report.txt not found, skipping archive.")
            return@doLast
        }

        val historyDir = file("${layout.buildDirectory.get()}/performance-history")
        historyDir.mkdirs()

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val timestamp = sdf.format(Date())
        val destFile = file("${historyDir}/report_${timestamp}.txt")

        sourceFile.copyTo(destFile)
        println("[INFO] Report archived to: ${destFile.absolutePath}")
    }
}