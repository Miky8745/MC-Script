package net.euport.mcscript.custom.ram;

import com.google.gson.Gson;
import net.euport.mcscript.block.entity.CPUBlockEntity;
import net.euport.mcscript.factories.ModifiedGsonObjectFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RAM {
    public int size;
    private RAMUnit<?>[] values;

    public RAM(int size) {
        this.size = size;
        values = new RAMUnit[size];
    }

    public void secureMemory() {
        this.size = CPUBlockEntity.RAM_SIZE;
        if (values == null) {values = new RAMUnit<?>[size];}
        if (values.length != size) {
            RAMUnit<?>[] temp = values;
            values = new RAMUnit<?>[size];
            for (int i = 0; i < temp.length && i < values.length; i++) {
                values[i] = temp[i];
            }
        }
    }

    public <T> void writeToIndex(T value, Class<T> valueClass, String topic, int index) {
        values[index] = new RAMUnit<>(value, valueClass, topic);
    }

    public void writeToIndex(RAMUnit<?> datapiece, int index) {
        values[index] = datapiece;
    }

    public <T> void writeToFirstEmpty(T value, Class<T> valueClass, String topic) {
        for (int i = 0; i < size; i++) {
            if (values[i] == null || values[i].topic.equals(topic)) {
                values[i] = new RAMUnit<>(value, valueClass, topic);
                return;
            }
        }

        throw new IllegalStateException("Out of memory");
    }

    public void writeToFirstEmpty(RAMUnit<?> datapiece) {
        for (int i = 0; i < size; i++) {
            if (values[i] == null || values[i].topic.equals(datapiece.topic)) {
                values[i] = datapiece;
                return;
            }
        }

        throw new IllegalStateException("Out of memory");
    }

    public int getFirstEmpty(String topic) {
        for (int i = 0; i < size; i++) {
            if (values[i] == null || values[i].topic.equals(topic)) {
                return i;
            }
        }

        throw new RuntimeException("Out of memory.");
    }

    public Data<?> read(int index) {
        return values[index];
    }

    public Data<?> read(String topic) {
        for (RAMUnit<?> datapiece : values) {
            if (topic.equals(datapiece.topic)) {
                return datapiece;
            }
        }
        return null;
    }

    public void clear(int index) {
        values[index] = null;
    }

    public void clear(String topic) {
        for (int i = 0; i < size; i++) {
            if (values[i] == null) {continue;}
            if (topic.equals(values[i].topic)) {
                values[i] = null;
            }
        }
    }

    public String getRAMStateInString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            RAMUnit<?> value = values[i];
            if (value == null || value.topic == null || value.topic.isBlank()) {continue;}
            builder.append(i).append("#").
                append(value.topic).append("#").
                append(value.gettClass().getName()).append("#").
                append(value.get().toString()).append("\n");
        }

        return builder.toString();
    }

    public static void saveState(RAM ram, File file) {
        if (!file.exists()) {return;}

        Gson gsonObject = ModifiedGsonObjectFactory.createGsonObject();

        try(FileWriter writer = new FileWriter(file)) {
            String json = gsonObject.toJson(ram);
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    public static RAM loadState(File file, int size) {
        RAM out = new RAM(size);
        if (!file.exists()) {return out;}
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length < 4) {continue;}
                int index = Integer.parseInt(parts[0]);
                String topic = parts[1];
                String className = parts[2];
                String value = parts[3];

                RAMUnit<?> temp = Utils.getRAMUnitFromString(className, value, topic);
                if (temp != null) {
                    out.values[index] = temp;
                }
            }
        } catch (IOException e) {
            return out;
        }
        return out;
    }
    */

    public static RAM loadFromJSON(File file, int size) {
        if (!file.exists()) {return new RAM(size);}

        Gson gson = ModifiedGsonObjectFactory.createGsonObject();

        try(FileReader reader = new FileReader(file)) {
            RAM ram = gson.fromJson(reader, RAM.class);
            if(ram == null) {return new RAM(size);}
            ram.secureMemory();
            return ram;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] readAll() {
        List<String> out = new ArrayList<>();

        for (RAMUnit<?> current : values) {
            if (current == null) {continue;}
            String line = parseValue(current.topic) + "#" +
                    current.gettClass().getName() + "#" +
                    parseValue(current.get().toString());

            out.add(line);
        }

        return out.toArray(new String[0]);
    }

    public static String parseValue(String value) {
        String[] parts = value.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            builder.append(part);
            if (i < parts.length-1) {
                builder.append("***");
            }
        }
        return builder.toString();
    }

    public void reset() {
        Arrays.fill(values, null);
    }
}
