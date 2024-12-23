package by.smertex.buffer.realisation;

import by.smertex.buffer.interfaces.RingBuffer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RingBufferImpl<T> implements RingBuffer<T> {

    private final ReadWriteLock lock;

    private final int capacity;

    private final T[] buffer;

    private final AtomicInteger readPointer = new AtomicInteger(0);

    private final AtomicInteger writePointer = new AtomicInteger(-1);

    @Override
    public T read() {
        try {
            lock.readLock().lock();
            if (!(writePointer.get() < readPointer.get())) {
                T nextValue = buffer[readPointer.get() % capacity];
                readPointer.getAndIncrement();
                return nextValue;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean write(T element) {
        try {
            lock.writeLock().lock();
            boolean isFull = (writePointer.get() - readPointer.get()) + 1 == capacity;
            if (!isFull) {
                int nextWriteSeq = writePointer.get() + 1;
                buffer[nextWriteSeq % capacity] = element;
                writePointer.getAndIncrement();
            }
            return !isFull;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int currentLoad() {
        return writePointer.get() - readPointer.get();
    }

    @SuppressWarnings("unchecked")
    public RingBufferImpl(int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
        this.lock = new ReentrantReadWriteLock();
    }
}
