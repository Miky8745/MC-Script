package net.euport.mcscript.custom.ram;

public class RAMUnit<T> extends Data<T> {
    public String topic;

    public RAMUnit(T value, Class<T> tClass, String topic) {
        super(value, tClass);
        this.topic = topic;
    }
}
