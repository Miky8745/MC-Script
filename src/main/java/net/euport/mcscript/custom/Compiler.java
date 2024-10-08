package net.euport.mcscript.custom;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Compiler {

    public static void downloadFile(String url, String name) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(name + ".txt")) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            fileOutputStream.close();
            if (!renameToDotJava(name)) {
                throw new RuntimeException("Couldn't rename the file to .java");
            }
        } catch (IOException e) {
            throw new RuntimeException("Code in URL: " + url + " is not valid or couldn't be read.");
        }
    }

    private static boolean renameToDotJava(String name) {
        File file = new File(name + ".txt");
        File newName = new File(name + ".java");

        return file.renameTo(newName);
    }

    public static void compileJava(String fileName) throws IOException {
        Process runtime = Runtime.getRuntime().exec(Utils.generateCommandLineCommand(fileName, "javac") + ".java" + " MCScriptHelperClass.java");
        Scanner sc = new Scanner(runtime.getErrorStream(), StandardCharsets.UTF_8);
        StringBuilder out = new StringBuilder();
        while (sc.hasNext()) {
            out.append(sc.nextLine());
        }
        if (!out.toString().isEmpty()) {
            throw new RuntimeException("Couldn't compile the code with error " + out);
        }
        sc.close();
    }

    public static String[] runJavaCode(String fileName, @Nullable String[] params) throws Exception {
        Process runtime = Runtime.getRuntime().exec(Utils.generateCommandLineCommand(fileName, "java") + " " + catParams(params));
        Scanner sc = new Scanner(runtime.getInputStream(), StandardCharsets.UTF_8);
        Scanner errSc = new Scanner(runtime.getErrorStream(), StandardCharsets.UTF_8);
        List<String> out = new ArrayList<>();
        while (sc.hasNext()) {
            out.add(sc.nextLine());
        }
        sc.close();

        StringBuilder builder= new StringBuilder();
        while (errSc.hasNext()) {
            builder.append(errSc.nextLine());
        }
        errSc.close();

        if (!(builder.isEmpty())) {
            throw new RuntimeException("Error while running java code: " + builder);
        }

        return out.toArray(new String[0]);
    }

    public static void clean(String fileName) {
        File txt = new File(fileName + ".txt");
        File java = new File(fileName + ".java");
        File compiledJava = new File(fileName + ".class");

        txt.delete();
        java.delete();
        compiledJava.delete();
    }

    private static String catParams(String[] params) {
        if (params == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(' ');

        int signalStrength = Integer.parseInt(params[0]);

        String memoryState = params[1];

        builder.append(signalStrength).append(' ').append(memoryState);

        return builder.toString();
    }
}