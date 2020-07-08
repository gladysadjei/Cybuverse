package com.gladys.cybuverse.Utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.range;

public class ReadWritableFile {
    private File FILE;
    private Scanner FILESCANNER;
    private FileWriter WRITER;
    //private FileReader READER;

    public ReadWritableFile(File file) throws IOException {
        initializeFile(file);
    }

    ReadWritableFile(String filepath) throws IOException {
        initializeFile(new File(filepath));
    }

    private void initializeFile(File file) throws IOException {
        if (file.canWrite() || file.canRead()) {
            FILE = file;
            FILESCANNER = new Scanner(FILE);
        } else {
            try {
                throw new FileNotReadWritableException();
            } catch (FileNotReadWritableException e) {
                e.printStackTrace();
            }
        }
    }

    public ReadWritableFile write(Object... lines) {
        if (lines.length > 0) {
            try {
                StringBuilder writableLines = new StringBuilder();
                for (Object line : lines) {
                    writableLines.append(line.toString()).append("\n");
                }
                WRITER = new FileWriter(getFile());
                WRITER.write(writableLines + "\n");
                WRITER.flush();
                WRITER.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    private void write(Collection<Object> lines) {
        write(lines.toArray());
    }

    public ReadWritableFile writeAppend(Object... lines) {
        StringBuilder writableLines = new StringBuilder();
        if (lines.length > 0) {
            for (int i : range(lines.length)) {
                writableLines.append(lines[i].toString()).append((i == lines.length - 1) ? "" : "\n");
            }
            write(read() + writableLines);
        }
        return this;
    }

    private ReadWritableFile writeAppend(Collection<Object> lines) {
        writeAppend(lines.toArray());
        return this;
    }

    public ReadWritableFile writeReplaceLine(int lineIndex, Object line) {
        if (lineIndex < numberOfLines()) {
            Map<Integer, Object> DATA = readEnumerate();
            DATA.put(lineIndex, line.toString());
            write(DATA.values());
        }
        return this;
    }


    public String read() {
        StringBuilder content = new StringBuilder();

        try {
            FILESCANNER = new Scanner(getFile());
            while (FILESCANNER.hasNextLine()) {
                content.append(FILESCANNER.nextLine()).append((FILESCANNER.hasNextLine()) ? "\n" : "");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public Map<Integer, Object> readEnumerate() {
        Map<Integer, Object> LINES_LIST = new HashMap<>();
        int i = 0;
        try {
            FILESCANNER = new Scanner(getFile());
            while (FILESCANNER.hasNextLine()) {
                String line = FILESCANNER.nextLine();
                LINES_LIST.put(i, line);
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (LINES_LIST.size() > 0 && LINES_LIST.get(LINES_LIST.size() - 1).toString().trim().isEmpty()) {
            LINES_LIST.remove(LINES_LIST.size() - 1);
        }
        return LINES_LIST;
    }

    public String readLineAt(int index) {
        if (index > 0 && index < numberOfLines()) {
            return readEnumerate().get(index).toString();
        } else throw new IndexOutOfBoundsException();
    }

    public boolean isReadable() {
        return FILE.canRead();
    }

    public boolean isWritable() {
        return FILE.canWrite();
    }

    public boolean isReadWritable() {
        return FILE.canWrite() && FILE.canWrite();
    }

    public IterableFile getIterableFile() {
        return new IterableFile(getFile());
    }

    public File getFile() {
        return FILE;
    }

    public String getFileName() {
        return FILE.getName();
    }

    public String getAbsoluteFilePath() {
        return getFile().toPath().toString();
    }

    public int numberOfLines() {
        return readEnumerate().size();
    }

    public ReadWritableFile clearFile() {
        try {
            WRITER = new FileWriter(getFile());
            WRITER.write("");
            WRITER.flush();
            WRITER.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String toString() {
        return format("ReadWritableFile('<>')", getAbsoluteFilePath());
    }

    public class IterableFile {

        private Scanner SCANNER;

        IterableFile(File file) {
            try {
                SCANNER = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public String readNextLine() {
            return SCANNER.nextLine();
        }

        public String readNextWord() {
            return SCANNER.next();
        }

        public boolean hasNextLine() {
            return FILESCANNER.hasNextLine();
        }

        public boolean hasNextWord() {
            return FILESCANNER.hasNext();
        }

        public boolean isReadable() {
            return FILE.canRead();
        }

        public Scanner getScannerbleFile() {
            try {
                return new Scanner(getFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private class FileNotReadWritableException extends Exception {
    }

//        ReadWritableFile(){
//            try {
//                WRITER.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

}
