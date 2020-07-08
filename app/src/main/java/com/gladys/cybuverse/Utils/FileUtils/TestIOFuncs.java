package com.gladys.cybuverse.Utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class TestIOFuncs {


    public static class IOStreams {


        public IOStreams() {
        }

        public static void make() {
            FileReader in = null;
            FileWriter out = null;
            try {
                in = new FileReader("input.txt");
                out = new FileWriter("output.txt");
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        public static void input() {
            InputStreamReader cin = null;
            try {
                cin = new InputStreamReader(System.in);
                System.out.println("Enter characters, 'q' to quit.");
                char c;
                do {
                    c = (char) cin.read();
                    System.out.print(c);
                } while (c != 'q');
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (cin != null) {
                    try {
                        cin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        public static void byteScanner() {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
            while (bOutput.size() != 1000) {
                try {
                    byte[] arr = {100, 123};
                    bOutput.write(System.in.read(arr));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            byte b[] = bOutput.toByteArray();
            System.out.println("Print the content");
            for (byte aB : b) {
                System.out.print((char) aB + " ");
            }
            System.out.println(" ");
            int c;
            ByteArrayInputStream bInput = new ByteArrayInputStream(b);
            System.out.println("Converting characters to Upper case ");
            for (int y = 0; y < 1; y++) {
                while ((c = bInput.read()) != -1) {
                    System.out.println(Character.toUpperCase((char) c));
                }
                bInput.reset();
            }
        }

        public static void writeUTF(String lines) throws IOException {
            DataOutputStream dataOut = new DataOutputStream(new FileOutputStream("output.txt"));
            dataOut.writeUTF(lines);
            DataInputStream dataIn = new DataInputStream(new FileInputStream("output.txt"));
            while (dataIn.available() > 0) {
                String k = dataIn.readUTF();
                System.out.print(k + " ");
            }
        }

        public static void file() throws IOException {
            File f;
            String[] strs = {"output.txt", "test2.txt"};
            try {
                for (String s : strs) {
                    f = new File(s);
                    boolean bool = f.canExecute();
                    String a = f.getAbsolutePath();
                    System.out.print(a);
                    System.out.println(" is executable: " + bool);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void fileread() throws IOException {
            File file = new File("input.txt");
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("This\n is\n an\n example\n");
            writer.flush();
            writer.close();
            FileReader fr = new FileReader(file);
            char[] a = new char[50];
            fr.read(a); // reads the content to the array
            for (char c : a)
                System.out.print(c); //prints the characters one by one
            fr.close();
        }

        public static void mkdir(String path) {
            String dirname = "/tmp/user/java/bin";
            File d = new File(path);
            d.mkdirs();
        }


        public static void ls(String at_path) {
            File file = null;
            String[] paths;
            try {
                file = new File(at_path);
                paths = file.list();
                for (String path : paths) {
                    System.out.println(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
