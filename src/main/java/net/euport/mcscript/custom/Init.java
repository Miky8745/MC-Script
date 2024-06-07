package net.euport.mcscript.custom;

import net.euport.mcscript.block.entity.CPUBlockEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static net.euport.mcscript.custom.Utils.print;

public abstract class Init {
    public static void init() {
        CPUBlockEntity.blocks = new HashMap<>();

        createModFolder();

        createMemoryState();
        createHelperFile();
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

    public static void createHelperFile() {
        if (new File("mcscript/MCScriptHelperClass.java").exists()) {
            new File("mcscript/MCScriptHelperClass.java").delete();
        }
        Compiler.downloadFile("https://1url.cz/@MCScriptHelper", "mcscript/MCScriptHelperClass");
        try {
            Compiler.compileJava("mcscript/MCScriptHelperClass");
        } catch (IOException e) {
            throw new RuntimeException("Failed to compile MCScriptHelperClass.java with error code: " + e.getMessage());
        }
    }
}