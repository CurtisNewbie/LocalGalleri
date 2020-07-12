package com.curtisnewbie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration in {@code application.properties} for image scanning
 */
@Component
@ConfigurationProperties(prefix = "scan")
public class ScanConfig {

    private String dir;
    private String defDir;

    /**
     * @return the dir
     */
    public String getDir() {
        return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * @return the default dir
     */
    public String getDefDir() {
        return defDir;
    }

    /**
     * @param defDir the defDir to set
     */
    public void setDefDir(String defDir) {
        this.defDir = defDir;
    }

}