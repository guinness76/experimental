package org.experimental.protobuf;

import java.io.Serializable;
import java.util.List;

public class AgentInfo implements Serializable {
    private Version version;
    private String edition;
    private List<String> projects;
    private boolean isDemoExpired;


    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public boolean isDemoExpired() {
        return isDemoExpired;
    }

    public void setDemoExpired(boolean demoExpired) {
        isDemoExpired = demoExpired;
    }

    @Override
    public String toString() {
        return "AgentInfo{" +
            "version='" + version + '\'' +
            ", edition='" + edition + '\'' +
            ", projects=" + projects +
            ", isDemoExpired=" + isDemoExpired +
            '}';
    }
}
