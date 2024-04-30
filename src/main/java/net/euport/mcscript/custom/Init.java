package net.euport.mcscript.custom;

import java.io.File;

import static net.euport.mcscript.custom.Utils.print;

public class Init {
    public static void init() {
        createModFolder();

        createMemoryState();
    }

    private static void createMemoryState() {
        File file = new File(Utils.MEMORY_STATE_URI);
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                print(e.getMessage());
            }
        }
    }

    private static void createModFolder() {
        File file = new File("mcscript");
        file.mkdir();
    }
}
