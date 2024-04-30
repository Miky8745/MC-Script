package net.euport.mcscript.custom;

import java.util.Arrays;

public class RAM {

    public static boolean instantiated = false;
    private final int maxMemory;
    private Data[] values;
    private boolean[] freeMemory;
    private int freeAmount;

    public RAM(int maxMemory) {
        if (instantiated) {
            throw new IllegalStateException("You can't have two RAM units active at the same time");
        }
        instantiated = true;
        this.maxMemory = maxMemory;
        values = new Data[maxMemory];
        freeMemory = new boolean[maxMemory];
        freeAmount = maxMemory;
        Arrays.fill(freeMemory, true);
    }

    public int malloc() throws IllegalStateException, IllegalAccessException {
        if (freeAmount <= 0) {throw new IllegalAccessException("You reached limit with allocated memory.");}
        freeAmount--;
        for (int i = 0; i < freeMemory.length; i++) {
            if (freeMemory[i]) {
                freeMemory[i] = false;
                return i;
            }
        }
        freeAmount++;
        throw new IllegalStateException("Something went wrong while allocating memory.");
    }

    public void free(int address) throws IllegalAccessException {
        if (address > maxMemory || freeAmount == maxMemory || freeMemory[address]) {
            throw new IllegalAccessException("You are trying to free already free memory");
        }

        freeMemory[address] = true;
        freeAmount++;
        values[address] = null;
    }

    public void write(String value, int address) throws IllegalAccessException {
        if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        values[address] = new Data(value);
    }

    public void write(int value, int address) throws IllegalAccessException {
       if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        values[address] = new Data(value);
    }

    public void write(float value, int address) throws IllegalAccessException {
        if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        values[address] = new Data(value);
    }

    public String readString(int address) throws IllegalAccessException {
        if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        return values[address].getStr();
    }

    public int readInt(int address) throws IllegalAccessException {
        if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        return values[address].getNormal();
    }

    public float readFloat(int address) throws IllegalAccessException {
        if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        return values[address].getReal();
    }

    public char readType(int address) throws IllegalAccessException {
        if (freeMemory[address]) {throw new IllegalAccessException("You are trying to access memory that is not allocated.");}

        return values[address].getType();
    }
}
