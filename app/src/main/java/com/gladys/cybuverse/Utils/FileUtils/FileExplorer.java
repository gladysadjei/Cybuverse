package com.gladys.cybuverse.Utils.FileUtils;


import com.gladys.cybuverse.Utils.GeneralUtils.collections.Stack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//TODO : make files to copy or cut a list

public class FileExplorer {

    private File HOME;
    private File CURRENTFILE;
    private String TRANSFER_STATE;
    private Stack<File> FORWARDSTACK;
    private Stack<File> BACKWARDSTACK;
    private List<File> TEMP_FILES_LIST;

    public FileExplorer() {
        CURRENTFILE = new File("");
        initializeFields();
    }

    public FileExplorer(File directory) {
        CURRENTFILE = directory;
        initializeFields();
    }

    public FileExplorer(String directoryPath) {
        if (directoryPath.equals("")) {
            CURRENTFILE = new File("");
        } else {
            CURRENTFILE = new File(directoryPath);
        }
        initializeFields();
    }

    public FileExplorer(FileExplorer fileExplorer) {
        CURRENTFILE = fileExplorer.getCurrentFile();
        TEMP_FILES_LIST = getFilesToTransfer();
        initializeFields();
    }

    private void initializeFields() {
        FORWARDSTACK = new Stack<>();
        BACKWARDSTACK = new Stack<>();
        HOME = CURRENTFILE;
        TEMP_FILES_LIST = new ArrayList<>();
    }

    public void setHome(File file) {
        HOME = file;
    }

    public void setHome(String filePath) {
        HOME = new File(filePath);
    }

    public void setHome(FileExplorer file) {
        HOME = file.getCurrentFile();
    }

    private void setTransferStateCopy() {
        this.TRANSFER_STATE = "COPY";
    }

    private void setTransferStateCut() {
        this.TRANSFER_STATE = "CUT";
    }

    private String getTransferState() {
        return TRANSFER_STATE;
    }

    public List<File> getFilesToTransfer() {
        return TEMP_FILES_LIST;
    }

    public FileExplorer createNewFolder(String childName) {
        FileManager.createDirectory(CURRENTFILE, childName);
        return this;
    }

    public FileExplorer createNewFile(String fileName) {
        FileManager.createFile(CURRENTFILE, fileName);
        return this;
    }

    public FileExplorer renameFile(String oldName, String newName) {
        if (getFile(oldName).exists()) {
            getFile(oldName).renameTo(new File(CURRENTFILE, newName));
        }
        return this;
    }

    public FileExplorer deleteFile(String childName) {
        if (getFile(childName).exists()) {
            getFile(childName).delete();
        }
        return this;
    }

    public FileExplorer copy(String... fileNames) {
        TEMP_FILES_LIST.clear();
        for (String name : fileNames) {
            TEMP_FILES_LIST.add(getFile(name));
        }
        setTransferStateCopy();
        return this;
    }

    public FileExplorer copyCurrentFile() {
        TEMP_FILES_LIST.clear();
        TEMP_FILES_LIST.add(getCurrentFile());
        setTransferStateCopy();
        return this;
    }

    public FileExplorer cut(String... fileNames) {
        TEMP_FILES_LIST.clear();
        for (String name : fileNames) {
            TEMP_FILES_LIST.add(getFile(name));
        }
        setTransferStateCut();
        return this;
    }

    public FileExplorer cutCurrentFile() {
        TEMP_FILES_LIST.clear();
        TEMP_FILES_LIST.add(getCurrentFile());
        setTransferStateCut();
        return this;
    }

    public FileExplorer paste() {
        if (getCurrentFile().isDirectory()) {
            if (getTransferState().equals("COPY")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.copy(file, getCurrentFile());
                }
            } else if (getTransferState().equals("CUT")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.cut(file, getCurrentFile());
                }
            }
        }
        return this;
    }

    public FileExplorer pasteWithNewName(String newName) {
        if (getCurrentFile().isDirectory()) {
            if (getTransferState().equals("COPY")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.copy(file, getCurrentFile(), newName);
                }
            } else if (getTransferState().equals("CUT")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.cut(file, getCurrentFile(), newName);
                }
            }
        }
        return this;
    }

    public FileExplorer pasteIn(String folderName) {
        pasteIn(getFile(folderName));
        return this;
    }

    public FileExplorer pasteWithNewNameIn(String folderName, String newName) {
        pasteWithNewNameIn(getFile(folderName), newName);
        return this;
    }

    public FileExplorer pasteIn(File directory) {
        if (directory.isDirectory()) {
            if (getTransferState().equals("COPY")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.copy(file, directory);
                }
            } else if (getTransferState().equals("CUT")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.cut(file, directory);
                }
            }
        }
        return this;
    }

    public FileExplorer pasteWithNewNameIn(File directory, String newName) {
        if (directory.isDirectory()) {
            if (getTransferState().equals("COPY")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.copy(file, directory, newName);
                }
            } else if (getTransferState().equals("CUT")) {
                for (File file : TEMP_FILES_LIST) {
                    FileManager.cut(file, directory, newName);
                }
            }
        }
        return this;
    }

//            public void renameSelf(String newName){ CURRENTFILE.renameTo(new File(CURRENTFILE.getParentFile(), newName)); }
//
//            public void deleteSelf(){ CURRENTFILE.delete(); }

    public boolean isFile() {
        return CURRENTFILE.isFile();
    }

    public boolean isFolder() {
        return CURRENTFILE.isFile();
    }

    public URI getCurrentFileURI() {
        return CURRENTFILE.toURI();
    }

    public ReadWritableFile openCurrentFileAsReadWritableFile() {
        if (isFile()) {
            try {
                return new ReadWritableFile(getCurrentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ReadWritableFile openReadWritableFile(String fileName) {
        if (getFile(fileName).isFile()) {
            try {
                return new ReadWritableFile(getFile(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getCurrentPath() {
        return CURRENTFILE.getAbsolutePath();
    }

    public String makeFilePath(String newPathName) {
        return getCurrentPath() + "/" + newPathName;
    }

    public String[] getFileNames() {
        String[] files = CURRENTFILE.list();
        return (files == null) ? new String[]{} : files;
    }

    public File[] getFileItems() {
        File[] files = CURRENTFILE.listFiles();
        return (files == null) ? new File[]{} : files;
    }

    public FileExplorer openFolder(String folderName) {
        if (getFile(folderName).exists()) {
            if (getFile(folderName).isDirectory()) {
                return new FileExplorer(getFile(folderName));
            }
            try {
                throw new FileNotDirectoryException();
            } catch (FileNotDirectoryException e) {
                e.printStackTrace();
            }
        } else {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public FileExplorer exploreFile(String fileName) {
        if (getFile(fileName).exists()) {
            BACKWARDSTACK.push(getCurrentFile());
            CURRENTFILE = getFile(fileName);
        } else {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public FileExplorer goBack() {
        if (BACKWARDSTACK.isEmpty()) {
            return null;
        }
        if (BACKWARDSTACK.peekTop().exists()) {
            FORWARDSTACK.push(getCurrentFile());
            CURRENTFILE = BACKWARDSTACK.pop();
        } else {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public FileExplorer goForward() {
        if (FORWARDSTACK.isEmpty()) {
            return null;
        }
        if (FORWARDSTACK.peekTop().exists()) {
            BACKWARDSTACK.push(getCurrentFile());
            CURRENTFILE = FORWARDSTACK.pop();
        } else {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public FileExplorer goHome() {
        if (HOME.exists()) {
            if (CURRENTFILE != HOME) {
                BACKWARDSTACK.push(getCurrentFile());
                BACKWARDSTACK.push(getCurrentFile());
                CURRENTFILE = HOME;
            }
        } else {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public File[] searchFolderWithPattern(String pattern) {
        List<File> matchesList = new ArrayList<>();
        for (File file : getFileItems()) {
            String name = file.getName();
            if (name.toLowerCase().contains(pattern.toLowerCase())) {
                matchesList.add(file);
            }
        }
        File[] matchesArray = new File[matchesList.size()];
        matchesList.toArray(matchesArray);
        return matchesArray;
    }

    public FileExplorer getBackwardFile() {
        return new FileExplorer(BACKWARDSTACK.peekTop());
    }

    public FileExplorer getForwardFile() {
        return new FileExplorer(FORWARDSTACK.peekTop());
    }

    public File getCurrentFile() {
        return CURRENTFILE;
    }

    public File getFile(String fileName) {
        return new File(CURRENTFILE, fileName);
    }

    public FileExplorer ls() {
        if (CURRENTFILE.isDirectory()) {
            FileManager.listDir(CURRENTFILE);
        }
        return this;
    }

    public FileExplorer clone() {
        return new FileExplorer(this);
    }

    public void forEachFileOrFolder(FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        for (File file : getFileItems()) {
            fileProcess.process(file);
        }
    }

    public void forEachFileInFolderAndSubFolders(final FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        forEachSubFolder(new FileProcess() {
            @Override
            public void process(File file) {
                new FileExplorer(file).forEachFile(new FileProcess() {
                    @Override
                    public void process(File file) {
                        fileProcess.process(file);
                    }
                });
            }
        });

    }

    public void forEachSubFolder(final FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        forEachFolder(new FileProcess() {
            private void action(File file) {
                if (file != null) {
                    fileProcess.process(file);
                    new FileExplorer(file).forEachFolder(new FileProcess() {
                        @Override
                        public void process(File file) {
                            action(file);
                        }
                    });
                }
            }

            @Override
            public void process(File file) {
                action(file);
            }
        });
    }

    public void forEachFile(final FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        forEachFileOrFolder(new FileProcess() {
            @Override
            public void process(File file) {
                if (file.isFile()) {
                    fileProcess.process(file);
                }
            }
        });
    }

    public void forEachFolder(final FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        forEachFileOrFolder(new FileProcess() {
            @Override
            public void process(File file) {
                if (file.isDirectory()) {
                    fileProcess.process(file);
                }
            }
        });
    }

    public boolean contains(String fileName) {
        System.out.print("searching file : " + fileName + " -- {");
        int index = Arrays.binarySearch(getFileNames(), fileName);
        System.out.print("\n\t\tresult : " + String.valueOf(index) + " ,");
        return index >= 0;
    }

    public void put(File file) {
        FileManager.copy(file, CURRENTFILE);
    }

    public void put(File file, String newName) {
        FileManager.copy(file, CURRENTFILE, newName);
    }

    public void forEachFileOrFolderLike(final String pattern, final FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        forEachFileOrFolder(new FileProcess() {
            @Override
            public void process(File file) {
                if (file.getName().toLowerCase().contains(pattern.toLowerCase())) {
                    fileProcess.process(file);
                }
            }
        });
    }

    public void forEachSubFileOrFolderLike(final String pattern, final FileProcess fileProcess) {
        Objects.requireNonNull(fileProcess);

        forEachFileOrFolder(new FileProcess() {

            private void action(File file) {
                if (file.getName().toLowerCase().contains(pattern.toLowerCase())) {
                    fileProcess.process(file);
                }
                if (file.isDirectory()) {
                    new FileExplorer(file).forEachFileOrFolder(new FileProcess() {
                        @Override
                        public void process(File file) {
                            action(file);
                        }
                    });
                }
            }

            @Override
            public void process(File file) {
                action(file);
            }
        });
    }

    public interface FileProcess {
        void process(File file);
    }

    private class FileNotDirectoryException extends Exception {
        FileNotDirectoryException() {
        }
    }
}