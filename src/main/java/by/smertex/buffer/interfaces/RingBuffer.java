package by.smertex.buffer.interfaces;

public interface RingBuffer <T>{

    T read();

    boolean write(T element);

    int capacity();

    int currentLoad();
}
