package com.curtisnewbie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration in {@code application.properties} for image scanning
 */
@ConfigurationProperties(prefix = "scan")
public class ScanConfig {

    private String dir;
    private String defDir;

    public String dir() {
        return dir;
    }

    public String defaultDir() {
        return defDir;
    }
}