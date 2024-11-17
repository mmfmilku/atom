package org.mmfmilku.atom.agent.compiler.parser.aa;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public final class TestFile4 implements Serializable {

    public static void tryCase1() {
        try {
            String a = "1";
            int i = Integer.parseInt(a);
            System.out.println(i);
        } catch (NullPointerException | UnsupportedClassVersionError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception");
        } finally {
            System.out.println("finally");
        }
    }

    public static void tryCase2() {
        try {
            String a = "1";
            int i = Integer.parseInt(a);
            System.out.println(i);
        } finally {
            System.out.println("finally");
        }

        try {
            String a = "1";
            int i = Integer.parseInt(a);
            System.out.println(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tryCase3() {
        try (InputStream inputStream1 = new FileInputStream(new File(""));
             InputStream inputStream2 = new FileInputStream(new File(""))) {
            int read1 = inputStream1.read();
            int read2 = inputStream2.read();
            System.out.println(read1);
            System.out.println(read2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream inputStream1 = new FileInputStream(new File(""));
             InputStream inputStream2 = new FileInputStream(new File(""))) {
            int read1 = inputStream1.read();
            int read2 = inputStream2.read();
            System.out.println(read1);
            System.out.println(read2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally with resource");
        }
    }

}