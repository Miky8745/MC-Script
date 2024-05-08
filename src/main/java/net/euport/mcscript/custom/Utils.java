package net.euport.mcscript.custom;

import net.euport.mcscript.custom.ram.RAMUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Utils {
    public static final String DOWNLOADED_CODE_URI = "mcscript/Code";
    public static final String MEMORY_STATE_URI = "mcscript/memory_state.txt";

    public static void loadProgram(String url) throws Exception {
        url = url.subSequence(1, url.length()-1).toString();

        String name = DOWNLOADED_CODE_URI;

        Compiler.clean(name);
        Compiler.downloadFile(url, name);
        Compiler.compileJava(name);
    }

    @Nullable
    public static String[] runProgram(@Nullable String[] params) throws Exception {
        return Compiler.runJavaCode(DOWNLOADED_CODE_URI , params);
    }

    public static void print(String e) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal(e), false);
        }
    }

    public static String generateCommandLineCommand(String uri, String secondCommand) {
        String[] parsedUri = uri.split("/");
        String fileName = parsedUri[parsedUri.length-1];
        String path = Arrays.toString(Arrays.copyOfRange(parsedUri, 0, parsedUri.length-1));
        path = path.substring(1, path.length()-1);
        return "cmd /c cd " + path + " && " + secondCommand + " " + fileName;
    }

    public static <T> boolean isInArray(T[] array, T thing) {
        for (T object : array) {
            if (object.equals(thing)) {
                return true;
            }
        }
        return false;
    }

    public static <T> int getIndex(T[] array, T thing) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(thing)) {
                return i;
            }
        }
        return -1;
    }

    public static String getRawClassName(String className) {
        switch (className) {
            case "int" -> {return Integer.class.getName();}
            case "float" -> {return Float.class.getName();}
            case "str" -> {return String.class.getName();}
            default -> {return null;}
        }
    }

    public static RAMUnit<?> getRAMUnitFromString(String tClass, String data, String topic) {
        switch (tClass) {
            case "java.lang.Integer" -> {
                Integer object = Integer.parseInt(data);
                return new RAMUnit<>(object, Integer.class, topic);
            }
            case "java.lang.Float" -> {
                Float object = Float.parseFloat(data);
                return new RAMUnit<>(object, Float.class, topic);
            }
            case "java.lang.String" -> {
                String object = String.valueOf(data);
                return new RAMUnit<>(object, String.class, topic);
            }
            default -> {
                return null;
            }
        }
    }

    public static String formatMemory(String[] raw) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String item : raw) {
            stringBuilder.append(item).append("@@");
        }

        return stringBuilder.toString();
    }
}
