package com.gladys.cybuverse.Utils.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.println;

public class FileManager {

    private FileManager() {
    }

    public static void createDirectory(File parent, String childName) {
        File temp = new File(parent, childName);
        if (!temp.exists()) {
            if (!temp.mkdirs()){
                temp.mkdir();
            }
        }
    }

    public static File getDirectory(File parent, String childName) {
        return new File(parent, childName);
    }

    public static void createFile(File parent, String fileName) {
        File temp = new File(parent, fileName);
        if (!temp.exists()) {
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getFile(File parent, String fileName) {
        File temp = new File(parent, fileName);
        if (temp.exists()) {
            return temp;
        }
        return null;
    }

    public static void listDir(File file) {
        String[] paths;
        try {
            paths = file.list();
            if (paths != null)
                for (String path : paths)
                    System.out.println(path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeDir(String path) {
        new File(path).mkdirs();
    }

    public static void write(File file, String line) {

        FileWriter WRITER = null;

        if (file.canWrite()) {
            try {
                WRITER = new FileWriter(file);
                WRITER.write(line);
                WRITER.flush();
                WRITER.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeAppend(File file, String lines) {

        FileWriter WRITER = null;

        if (file.canWrite()) {
            try {
                WRITER = new FileWriter(file);
                WRITER.write(read(file) + "\n" + lines);
                WRITER.flush();
                WRITER.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String read(File file) {
        String retStr = "";

        FileReader READER = null;

        if (file.canRead()) {
            try {
                READER = new FileReader(file);
                while (true) {
                    int c = READER.read();
                    if (c != -1) {
                        retStr += (char) c;
                    } else {
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            assert READER != null;
            READER.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retStr;
    }

    public static Map<Integer, String> readEnumerate(File file) {

        Map<Integer, String> lines = new HashMap<>();

        String retStr = "";

        FileReader READER = null;

        if (file.canRead()) {
            try {
                READER = new FileReader(file);
                int i = 1;
                while (true) {
                    int c = READER.read();
                    if (c != -1) {
                        if (c == 10) {
                            if (!retStr.trim().isEmpty()) {
                                lines.put(i, retStr);
                                retStr = "";
                                i++;
                            }
                        } else {
                            retStr += (char) c;
                        }
                    } else {
                        if (!retStr.trim().isEmpty()) {
                            lines.put(i, retStr);
                        }
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                assert READER != null;
                READER.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static void writeUTF(File file, String lines) {
        if (file.canWrite()) {
            DataOutputStream dataOut = null;
            try {
                dataOut = new DataOutputStream(new FileOutputStream(file.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                assert dataOut != null;
                dataOut.writeUTF(lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readUTF(File file) {
        StringBuilder retStr = new StringBuilder();

        if (file.canRead()) {
            DataInputStream dataIn = null;
            try {
                dataIn = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                while (dataIn.available() > 0) {
                    String c = dataIn.readUTF();
                    retStr.append(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retStr.toString();
    }

    public static void copy(File file, File newLocation) {
        copy(file, newLocation, "");
    }

    public static void copy(File file, File newLocation, String newName) {
        if (file.exists()) {
            if (file.isFile()) {
                copyFile(file, newLocation, newName);
            } else {
                copyDirectory(file, newLocation, newName);
            }
        }
    }

    private static void copyFile(File file, File newLocation, String newName) {
        try {
            if (file.isFile()) {
                if (newLocation.exists() && newLocation.isDirectory()) {
                    File copiedFile = getCopiedFile(file, newLocation, newName);
                    copiedFile.createNewFile();

                    try {
                        FileInputStream inputStream = new FileInputStream(file);
                        FileOutputStream fileOutputStream = new FileOutputStream(copiedFile);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    while (inputStream.available() != 0) {
//                        fileOutputStream.write(inputStream.read());
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyDirectory(File file, File newLocation, String newName) {
        try {
            if (file.isDirectory()) {

                if (newLocation.exists() && newLocation.isDirectory()) {

                    File copiedDirectory = getCopiedFile(file, newLocation, newName);

                    if (!copiedDirectory.exists()) {
                        copiedDirectory.mkdirs();
                    }
                    File[] fileList = file.listFiles();
                    if (fileList != null) {
                        for (File subfile : fileList) {
                            if (subfile.isFile()) {
                                copyFile(subfile, copiedDirectory, "");
                            } else {
                                copyDirectory(subfile, copiedDirectory, "");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String renameToCopiedFile(String name) {
        if (!name.contains(".")) {
            return name + "_copy";
        } else if (name.lastIndexOf(".") == 0) {
            return name + "_copy";
        }
        String nameOnly = name.substring(0, name.lastIndexOf("."));
        String newName = nameOnly + "_copy" + name.substring(name.lastIndexOf("."));
        return newName;
    }

    public static boolean renameFile(File file, String name) {
        if (file.exists()) {
            String pathName = file.getAbsolutePath()
                    .substring(0, file.getAbsolutePath().lastIndexOf(file.getName())) + name;
            return file.renameTo(new File(pathName));
        }
        return false;
    }

    public static String getExtension(String name) {
        if (!name.contains(".")) {
            return "";
        } else if (name.lastIndexOf(".") == 0) {
            return "";
        }
        return name.substring(name.lastIndexOf("."));
    }

    private static File getCopiedFile(File file, File newLocation, String newName) {

        File copiedFile;

        if (newName.trim().isEmpty()) {
            if (newLocation.isFile()) {
                String name = newLocation.getName();
                newLocation = newLocation.getParentFile();
                return getCopiedFile(file, newLocation, name);
            }
            if (getDirectory(newLocation, file.getName()).exists()) {
                copiedFile = getDirectory(newLocation, renameToCopiedFile(file.getName()));
                while (copiedFile.exists()) {
                    copiedFile = getDirectory(newLocation, renameToCopiedFile(copiedFile.getName()));
                }
            } else {
                copiedFile = getDirectory(newLocation, file.getName());
            }
        } else {

            if (!getExtension(newName).equals(getExtension(file.getName()))) {
                newName += getExtension(file.getName());
            }

            if (getDirectory(newLocation, newName).exists()) {
                copiedFile = getDirectory(newLocation, renameToCopiedFile(file.getName()));
            } else {
                copiedFile = getDirectory(newLocation, newName);
            }
        }
        return copiedFile;
    }

    public static void cut(File file, File newLocation) {
        copy(file, newLocation);
        file.delete();
    }

    public static void cut(File file, File newLocation, String newName) {
        copy(file, newLocation, newName);
        file.delete();
    }

    public static String getEnviron(String name) {
        return System.getenv(name);
    }

    public static void listEnviron() {
        for (String k : System.getenv().keySet()) {
            println("KEY : " + k, "\tVALUE : " + System.getenv(k));
        }
    }

    private static long sizeOfFile(File file, int pow) {
        return (long) (file.length() / (Math.pow(1024, pow)));
    }

    public static long sizeInBytes(File file) {
        return sizeOfFile(file, 0);
    }

    public static long sizeInKiloBytes(File file) {
        return sizeOfFile(file, 1);
    }

    public static long sizeInMegaBytes(File file) {
        return sizeOfFile(file, 2);
    }

    public static long sizeInGigaBytes(File file) {
        return sizeOfFile(file, 3);
    }

    public static long sizeInTeraBytes(File file) {
        return sizeOfFile(file, 4);
    }
}