package com.ffanonline.json;

public class SpecVersion {

    public enum VersionFlag {
        V4(1 << 0),
        V6(1 << 1),
        V7(1 << 2),
        V201909(1 << 3);

        private final int versionFlagValue;

        VersionFlag(int value) {
            this.versionFlagValue = value;
        }

        public int getVersionFlagValue() {
            return this.versionFlagValue;
        }
    }
}
