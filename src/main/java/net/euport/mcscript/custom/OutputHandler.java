package net.euport.mcscript.custom;

import net.euport.mcscript.block.entity.CPUBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static net.euport.mcscript.custom.Utils.*;

public abstract class OutputHandler {
    private static final String[] validCommands = {"writeIndex", "write", "delete", "clear", "turnOn", "setSignalStrength", "setExecutionInterval", "turnOff"};

    public static void handleOutput(@Nullable String[] rawOutput, String UUID) throws IllegalAccessException {
        if (rawOutput == null || rawOutput.length == 0 || rawOutput[0] == null || rawOutput[0].isBlank()) {return;}

        rawOutput = rawOutput[0].split("@@");

        for (String s : rawOutput) {
            if (s == null) {return;}
            if (s.isBlank()) {continue;}
            s = toNormal(s);
            String[] split = s.split("&");
            if (split[0].equals("syscall")) {
                split = split[1].split("#");
                if (split.length >= 2) {
                    String[] args = Arrays.copyOfRange(split, 1, split.length);
                    handleSystemCalls(split[0], args, UUID);
                }
            } else {
                print(s);
            }
        }
    }

    private static void handleSystemCalls(@NotNull String command, @Nullable String[] args, String UUID) throws IllegalAccessException {
        for (int i = 0; i < validCommands.length; i++) {
            if (validCommands[i].equals(command)) {
                execute(i, args, UUID);
                return;
            }
        }
        throw new RuntimeException(command + " is not a valid command");
    }

    public static void execute(int instruction, String[] next, String UUID) throws IllegalAccessException {
        switch (instruction) {
            case 0 -> writeToIndex(next);
            case 1 -> write(next);
            case 2 -> delete(next);
            case 3 -> clear();
            case 4 -> turnOn(UUID);
            case 5 -> signalStrength(next, UUID);
            case 6 -> setExecutionInterval(next, UUID);
            case 7 -> turnOff(UUID);
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

        CPUBlockEntity.ram.writeToIndex(getRAMUnitFromString(rawClassName, data, topic, index), index);
    }

    private static void write(String[] next) {
        if (next.length < 3) {throw new IllegalArgumentException("Too few arguments for write.");}

        String rawClassName = getRawClassName(next[0]);
        if (rawClassName == null) {throw new IllegalArgumentException(next[0] + " is not a valid datatype.");}

        String data= next[1];
        String topic = next[2].strip();
        if ((data) == null || (topic) == null) {throw new IllegalArgumentException("Data or topic is null.");}

        CPUBlockEntity.ram.writeToFirstEmpty(getRAMUnitFromString(rawClassName, data, topic, CPUBlockEntity.ram.getFirstEmpty(topic)));
    }

    private static void delete(String[] next) {
        if (next.length < 2) {throw new IllegalArgumentException("Too few arguments for delete.");}

        int isAddress = Integer.parseInt(next[0]);
        if (isAddress == 1) {
            CPUBlockEntity.ram.clear(Integer.parseInt(next[1]));
        } else {
            CPUBlockEntity.ram.clear(next[1]);
        }
    }

    private static void clear() {
        CPUBlockEntity.ram.reset();
    }

    private static void turnOn(String UUID) {
        CPUBlockEntity.setPowered(true, UUID);
    }

    private static void turnOff(String UUID) {
        CPUBlockEntity.setPowered(false, UUID);
    }

    private static void signalStrength(String[] next, String UUID) {
        if (next.length < 1) {throw new RuntimeException("Too few arguments for setSignalStrength");}

        try {
            int strength = Integer.parseInt(next[0]);
            CPUBlockEntity.setPower(strength, UUID);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Argument in setSignalStrength is not an Integer");
        }
    }

    private static void setExecutionInterval(String[] next, String UUID) {
        if (next.length < 1) {throw new RuntimeException("Too few arguments for setExecutionInterval");}

        try {
            int interval = Integer.parseInt(next[0]);
            CPUBlockEntity.setExecutionInterval(interval, UUID);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Argument in setExecutionInterval is not an Integer");
        }
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
