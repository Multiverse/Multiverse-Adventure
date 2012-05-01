package com.onarandombox.MultiverseAdventure.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class FileUtils extends com.onarandombox.MultiverseCore.utils.FileUtils {
    /**
     * Helper method to copy the world-folder
     * 
     * @returns if it had success
     */
    public static boolean copyFolder(File source, File target, Logger log) {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (source.isDirectory()) {

                if (!target.exists())
                    target.mkdir();

                String[] children = source.list();
                // for (int i=0; i<children.length; i++) {
                for (String child : children) {
                    copyFolder(new File(source, child), new File(target, child), log);
                }
            }
            else {
                in = new FileInputStream(source);
                out = new FileOutputStream(target);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return true;
        }
        catch (FileNotFoundException e) {
            log.warning("Exception while copying file: " + e.getMessage());
        }
        catch (IOException e) {
            log.warning("Exception while copying file: " + e.getMessage());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) { }
            }
            if (out != null) {
                try {
                    in.close();
                } catch (IOException ignore) { }
            }
        }
        return false;
    }
}
