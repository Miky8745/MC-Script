package net.euport.mcscript.custom.ram;

public class Data<T> {
    private T value;
    private final Class<T> tClass;

    public Data(T value, Class<T> tClass) {
        this.tClass = tClass;
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public Class<T> gettClass() {
        return tClass;
    }
}
