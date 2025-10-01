package com.tamago.tamagoservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tamagoservice.storage")
public class StorageProperties {
    private String imgDir = "IMG";

    public String getImgDir() { return imgDir; }
    public void setImgDir(String imgDir) { this.imgDir = imgDir; }
}
