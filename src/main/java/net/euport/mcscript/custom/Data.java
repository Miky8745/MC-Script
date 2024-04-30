package net.euport.mcscript.custom;

public class Data {

    private static int total = 0;
    private String str;
    private float real;
    private int normal;
    private final char type;
    public int address;

    public Data(String str) {
        total++;
        this.address = total;
        this.str = str;
        this.type = 's';
    }

    public Data(int normal) {
        this.type = 'i';
        this.address = total;
        this.normal = normal;
    }

    public Data(float real) {
        this.type = 'f';
        this.address = total;
        this.real = real;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        if (type != 's') {return;}
        this.str = str;
    }

    public float getReal() {
        return real;
    }

    public void setReal(float real) {
        if (type != 'f') {return;}
        this.real = real;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        if (type != 'i') {return;}
        this.normal = normal;
    }

    public char getType() {
        return type;
    }
}
