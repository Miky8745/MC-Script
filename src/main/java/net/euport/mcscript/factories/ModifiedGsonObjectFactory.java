package net.euport.mcscript.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.euport.mcscript.gsonadapters.GsonClass;
import net.euport.mcscript.gsonadapters.GsonInteger;

public abstract class ModifiedGsonObjectFactory {

    public static Gson createGsonObject() {
        return new GsonBuilder().
                registerTypeAdapter(Integer.class, new GsonInteger()).
                registerTypeAdapter(Class.class, new GsonClass()).
                create();
    }
}