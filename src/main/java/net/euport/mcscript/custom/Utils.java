package net.euport.mcscript.custom;

import net.euport.mcscript.block.entity.CPUBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;

public class Utils {
    private static final String[] validCommands = {"malloc"};
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

    public static void handleOutput(@Nullable String[] rawOutput) throws IllegalAccessException {
        if (rawOutput == null) {return;}

        //print(Arrays.toString(rawOutput));

        for (String s : rawOutput) {
            if (s == null) {return;}
            if (s.isBlank()) {continue;}
            //print("after continue");
            String[] split = s.split(" ");
            if (split[0].equals("syscall")) {
                if (split.length >= 3) {
                    handleSystemCalls(split[1], split[2]);
                }
            } else {
                print(s);
            }
        }
    }

    private static void handleSystemCalls(@NotNull String command, @Nullable String arg) throws IllegalAccessException {
        for (int i = 0; i < validCommands.length; i++) {
            if (validCommands[i].equals(command)) {
                //print("called execute in utils");
                execute(i, arg);
                return;
            }
        }
    }

    public static void execute(int instruction, String next) throws IllegalAccessException {
        switch (instruction) {
            case 0:
                //print("called malloc in utils");
                malloc(next);
                break;
            case 1:
                break;
            default:
                print(instruction + " is not a valid instruction code.");
        }
    }

    private static void malloc(String usage) throws IllegalAccessException {
        boolean found = false;
        int toSave = CPUBlockEntity.ram.malloc();
        try {
            File memoryState = new File(MEMORY_STATE_URI);
            BufferedReader reader = new BufferedReader(new FileReader(memoryState));

            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                int lineNumber = Integer.parseInt(line.split(" ")[0]);
                if (lineNumber == toSave) {
                    found = true;
                    builder.append(lineNumber).append(" ").append(usage).append(" \n");
                } else {
                    builder.append(line).append("\n");
                }
            }

            if (!found) {
                builder.append(toSave).append(" ").append(usage).append("\n");
            }

            reader.close();

            FileWriter writer = new FileWriter(memoryState);
            writer.write(builder.toString());
            writer.close();

        } catch (IOException e) {
            print(e.getMessage());
        }
    }

    public static String generateCommandLineCommand(String uri, String secondCommand) {
        String[] parsedUri = uri.split("/");
        String fileName = parsedUri[parsedUri.length-1];
        String path = Arrays.toString(Arrays.copyOfRange(parsedUri, 0, parsedUri.length-1));
        path = path.substring(1, path.length()-1);
        return "cmd /c cd " + path + " && " + secondCommand + " " + fileName;
    }
}
