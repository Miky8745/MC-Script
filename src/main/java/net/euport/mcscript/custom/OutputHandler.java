package net.euport.mcscript.custom;

import net.euport.mcscript.block.entity.CPUBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

import static net.euport.mcscript.custom.Utils.*;

public class OutputHandler {
    private static final String[] validCommands = {"writeIndex", "write"};

    public static void handleOutput(@Nullable String[] rawOutput) throws IllegalAccessException {
        if (rawOutput == null || rawOutput.length == 0 || rawOutput[0] == null || rawOutput[0].isBlank()) {return;}

        rawOutput = rawOutput[0].split("@@");

        for (String s : rawOutput) {
            if (s == null) {return;}
            if (s.isBlank()) {continue;}

            String[] split = s.split("&");
            if (split[0].equals("syscall")) {
                split = split[1].split("#");
                if (split.length >= 2) {
                    String[] args = Arrays.copyOfRange(split, 1, split.length);
                    handleSystemCalls(split[0], args);
                }
            } else {
                s = toNormal(s);
                print(s);
            }
        }
    }

    private static void handleSystemCalls(@NotNull String command, @Nullable String[] args) throws IllegalAccessException {
        for (int i = 0; i < validCommands.length; i++) {
            if (validCommands[i].equals(command)) {
                execute(i, args);
                return;
            }
        }
        throw new RuntimeException(command + " is not a valid command");
    }

    public static void execute(int instruction, String[] next) throws IllegalAccessException {
        switch (instruction) {
            case 0 -> writeToIndex(next);
            case 1 -> write(next);
            default -> throw new IllegalAccessException(instruction + " is not a valid instruction code.");
        }
    }

    private static void writeToIndex(String[] next) {
        if (next.length < 4) {throw new IllegalArgumentException("Too few arguments for writeIndex.");}
        int index = Integer.parseInt(next[0]);

        String rawClassName = getRawClassName(next[1]);
        if (rawClassName == null) {throw new IllegalArgumentException(next[1] + " is not a valid datatype.");}

        String data= next[2];
        String topic = next[3];
        if ((data) == null || (topic) == null) {throw new IllegalArgumentException("Data or topic is null.");}

        CPUBlockEntity.ram.writeToIndex(getRAMUnitFromString(rawClassName, data, topic), index);
    }

    private static void write(String[] next) {
        if (next.length < 3) {return;}

        String rawClassName = getRawClassName(next[0]);
        if (rawClassName == null) {throw new IllegalArgumentException(next[0] + " is not a valid datatype.");}

        String data= next[1];
        String topic = next[2];
        if ((data) == null || (topic) == null) {throw new IllegalArgumentException("Data or topic is null.");}

        CPUBlockEntity.ram.writeToFirstEmpty(getRAMUnitFromString(rawClassName, data, topic));
    }

    private static String toNormal(String text) {
        String[] parts = text.split("\\*\\*\\*");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(part).append(" ");
        }
        return parts.length > 1 ? builder.toString() : text;
    }
}
