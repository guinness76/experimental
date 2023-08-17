package org.test;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// jar is written to dist folder
public class ZipTest {
    public static void main(String[] args) throws Exception {
        String filePathArg = args[0];

        if (args.length == 0) {
            throw new Exception("File path arg was not set");
        }

        File theFile = new File(filePathArg);
        ZipFile zipFile = new ZipFile(theFile);
        System.out.printf("Successfully opened zip file '%s' with '%d' entries\n", filePathArg, zipFile.size());

        Enumeration e = zipFile.entries();
        ZipEntry entry = null;
        while (e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            String entryName = entry.getName();
            System.out.println("Zip entry name=" + entryName);
        }
    }
}
