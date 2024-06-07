package net.euport.mcscript.block.entity.properties;

import net.euport.mcscript.custom.ram.RAM;

public class CPUBlockEntityProperties {
    public int power;
    public boolean loaded;
    public RAM ram;
    public boolean on;
    public int maxPower;
    public int executionInterval;

    public CPUBlockEntityProperties() {
        power = 0;
        loaded = false;
        on = false;
        maxPower = 15;
        executionInterval = 20;
    }
}
