package com.automataevox.craftapi.listeners;

import com.automataevox.craftapi.utils.Logger;
import org.json.JSONObject;

import java.lang.management.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;

public class ServerHealthListener {
    public JSONObject getServerHealth() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        JSONObject healthJson = new JSONObject();

        // System Memory Usage
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;

        healthJson.put("totalMemory", formatSize(totalMemory));
        healthJson.put("usedMemory", formatSize(usedMemory));
        healthJson.put("freeMemory", formatSize(freeMemory));

        // JVM Memory Usage
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        healthJson.put("jvmHeapMemoryUsed", formatSize(heapMemoryUsage.getUsed()));
        healthJson.put("jvmHeapMemoryMax", formatSize(heapMemoryUsage.getMax()));
        healthJson.put("jvmNonHeapMemoryUsed", formatSize(nonHeapMemoryUsage.getUsed()));
        healthJson.put("jvmNonHeapMemoryMax", formatSize(nonHeapMemoryUsage.getMax()));

        // CPU Information
        healthJson.put("availableProcessors", osBean.getAvailableProcessors());
        healthJson.put("systemLoadAverage", osBean.getSystemLoadAverage());

        // Disk Usage
        JSONObject diskUsageJson = new JSONObject();
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {
                long totalSpace = store.getTotalSpace();
                long usableSpace = store.getUsableSpace();
                long usedSpace = totalSpace - usableSpace;

                JSONObject diskJson = new JSONObject();
                diskJson.put("total", formatSize(totalSpace));
                diskJson.put("used", formatSize(usedSpace));
                diskJson.put("available", formatSize(usableSpace));

                diskUsageJson.put(store.name(), diskJson);
            } catch (Exception e) {
                Logger.error("Error getting disk usage for " + store.name());
            }
        }
        healthJson.put("diskUsage", diskUsageJson);

        // Thread Information
        healthJson.put("threadCount", threadBean.getThreadCount());
        healthJson.put("peakThreadCount", threadBean.getPeakThreadCount());
        healthJson.put("totalStartedThreadCount", threadBean.getTotalStartedThreadCount());
        healthJson.put("deadlockedThreads", threadBean.findDeadlockedThreads() != null);

        // Uptime
        healthJson.put("uptime", formatUpTime(runtimeBean.getUptime()));

        // Return the JSON object with the health information
        return healthJson;
    }

    // Helper method to format memory size
    private long formatSize(long size) {
        // Implement memory size formatting logic
        return size / (1024 * 1024);
    }

    // Helper method to format uptime
    private long formatUpTime(long uptime) {
        // Implement uptime formatting logic
        return uptime / 1000;
    }
}
