package com.curtisnewbie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "manage")
public class ManageConfig {

    /** whether the list of images fetched from backend should be shuffled */
    private boolean listShuffled;

    public boolean isListShuffled() {
        return listShuffled;
    }

    public void setListShuffled(boolean randomList) {
        this.listShuffled = randomList;
    }
}
