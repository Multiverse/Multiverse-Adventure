package com.onarandombox.MultiverseAdventure.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils extends com.onarandombox.MultiverseCore.utils.FileUtils {
    /**
     * Helper method to copy the world-folder
     * 
     * @returns if it had success
     */
    public static boolean copyFolder(File source, File target) {
        try {
            if (source.isDirectory()) {

                if (!target.exists())
                    target.mkdir();

                String[] children = source.list();
                // for (int i=0; i<children.length; i++) {
                for (String child : children) {
                    copyFolder(new File(source, child), new File(target, child));
                }
            }
            else {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }
            return true;
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
