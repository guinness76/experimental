package org.experimental.protobuf;

import java.io.Serializable;

public class Version implements Serializable {
    private int major = 0;
    private int minor = 0;
    private int rev = 0;
    private int build = 0;
    private int beta = -1;
    private int rc = -1;
    private boolean dev = false;
    private boolean snapshot = false;

    public Version(int major, int minor, int rev, int build, int beta, int rc, boolean isSnapshot, boolean isDev) {
        this.build = build;
        this.major = major;
        this.minor = minor;
        this.rev = rev;
        this.beta = beta;
        this.rc = rc;
        this.snapshot = isSnapshot;
        this.dev = isDev;
    }

    private Version(String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid version: \"" + s + "\"");
        }
        if ("dev".equals(s)) {
            dev = true;
        } else {
            String start = s;
            if (s.indexOf("-rc") != -1) {
                int idx = s.indexOf("-rc");
                start = s.substring(0, idx);
                String rc = s.substring(idx + 3, s.length());
                this.rc = rc.isEmpty() ? 0 : Integer.parseInt(rc);
            } else if (s.indexOf("-beta") != -1) {
                int idx = s.indexOf("-beta");
                start = s.substring(0, idx);
                String beta = s.substring(idx + 5, s.length());
                this.beta = beta.isEmpty() ? 0 : Integer.parseInt(beta);
            } else if (s.endsWith("-SNAPSHOT")) {
                int idx = s.indexOf("-SNAPSHOT");
                start = s.substring(0, idx);
                this.snapshot = true;
            }

            String[] parts = start.split("\\.");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid version: \"" + s + "\"");
            }
            major = Integer.parseInt(parts[0]);
            minor = Integer.parseInt(parts[1]);
            rev = Integer.parseInt(parts[2]);

            if (parts.length > 3) {
                build = Integer.parseInt(parts[3]);
            }
        }
    }

    public String toParseableString() {
        if (dev) {
            return "dev";
        } else {
            if (snapshot) {
                return String.format("%d.%d.%d-SNAPSHOT", major, minor, rev);
            } else if (rc != -1) {
                return String.format("%d.%d.%d.%d-rc%d", major, minor, rev, build, rc);
            } else if (beta != -1) {
                return String.format("%d.%d.%d.%d-beta%d", major, minor, rev, build, beta);
            } else {
                return String.format("%d.%d.%d.%d", major, minor, rev, build);
            }
        }
    }

    public static Version parse(String s) {
        return new Version(s);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRev() {
        return rev;
    }

    public int getBuild() {
        return build;
    }

    public int getBeta() {
        return beta;
    }

    public int getRc() {
        return rc;
    }

    public boolean isDev() {
        return dev;
    }

    public boolean isSnapshot() {
        return snapshot;
    }

    @Override
    public String toString() {
        return "Version{" +
            "major=" + major +
            ", minor=" + minor +
            ", rev=" + rev +
            ", build=" + build +
            ", beta=" + beta +
            ", rc=" + rc +
            ", dev=" + dev +
            ", snapshot=" + snapshot +
            '}';
    }
}
