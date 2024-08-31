package me.jellysquid.mods.sodium.common.walden.util;

import org.apache.commons.codec.digest.DigestUtils;

public class HWIDUtils {

    public static String getHWID() {
        return DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("COMPUTERNAME")
                + System.getenv("os")
                + System.getProperty("os.name")
                + System.getProperty("os.arch")
                + System.getenv("SystemRoot")
                + System.getenv("PROCESSOR_LEVEL")
                + System.getenv("PROCESSOR_REVISION")
                + System.getenv("PROCESSOR_IDENTIFIER")
                + System.getenv("PROCESSOR_ARCHITECTURE")
                + System.getenv("PROCESSOR_ARCHITEW6432")
                + System.getenv("NUMBER_OF_PROCESSORS")
        ));
    }

}