package com.romaniuk;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.tasks.TaskState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TaskTimerPlugin implements Plugin<Project> {

    static Map<String, Long> taskTimes = new HashMap<>();

    private static final Map<String, Long> startTimes = new HashMap<>();

    @Override
    public void apply(Project project) {

        project.getGradle().addListener(new TaskExecutionListener() {

            @Override
            public void beforeExecute(Task task) {
                startTimes.put(task.getName(), System.currentTimeMillis());
            }

            @Override
            public void afterExecute(Task task, TaskState state) {
                Long start = startTimes.get(task.getName());
                if (start != null) {
                    long duration = System.currentTimeMillis() - start;
                    taskTimes.put(task.getName(), duration);
                }
            }
        });

        project.getTasks().register("generatePerformanceReport", Task.class, task -> {
            task.setGroup("reporting");
            task.setDescription("Generates a performance report from task execution times.");

            task.doLast(t -> {
                StringBuilder sb = new StringBuilder();
                sb.append("*** Performance Report ***\n");

                for (Map.Entry<String, Long> entry : taskTimes.entrySet()) {
                    sb.append("Task: ")
                            .append(entry.getKey())
                            .append(" | Duration: ")
                            .append(entry.getValue())
                            .append(" ms\n");
                }

                File reportDir = new File(project.getBuildDir(), "reports");
                reportDir.mkdirs();
                File reportFile = new File(reportDir, "performance-report.txt");

                try (FileWriter writer = new FileWriter(reportFile)) {
                    writer.write(sb.toString());
                    System.out.println("[INFO] Report written to: " + reportFile.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to write performance report", e);
                }
            });
        });

        project.getTasks().register("verifyTimeLimits", Task.class, task -> {
            task.setGroup("reporting");
            task.setDescription("Verifies that no task exceeded the 5-second time limit.");

            task.mustRunAfter("generatePerformanceReport");

            task.doLast(t -> {
                System.out.println("*** Verifying Time Limits ***");

                for (Map.Entry<String, Long> entry : taskTimes.entrySet()) {
                    if (entry.getValue() > 5000) {
                        System.out.println("[WARNING] Task '" + entry.getKey()
                                + "' exceeded limit! Duration: " + entry.getValue() + " ms");
                    }
                }

                System.out.println("[INFO] Verification complete.");
            });
        });
    }
}