package by.smertex;

import by.smertex.buffer.interfaces.RingBuffer;
import by.smertex.buffer.realisation.RingBufferImpl;
import org.junit.jupiter.api.Test;

public class RingBufferTest {

    @Test
    void readWriteTest(){
        RingBuffer<Integer> ringBuffer = new RingBufferImpl<>(7);
        for(int i = 0; i < 20; i++){
            ringBuffer.write(i);
            if(i > 5)
                assert ringBuffer.read() == i - 6;
        }
    }

    @Test
    @SuppressWarnings("all")
    void multithreadingTest() throws InterruptedException {
        RingBuffer<Integer> ringBuffer = new RingBufferImpl<>(5);

        new Thread(()->{
            int write = 0;
            while (true){
                ringBuffer.write(write);
                System.out.println(Thread.currentThread().getName() + " write " + write);
                write++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(()->{
            while (true){
                System.out.println(Thread.currentThread().getName() + " read: " + ringBuffer.read());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(()->{
            int write = 100;
            while (true){
                ringBuffer.write(write);
                System.out.println(Thread.currentThread().getName() + " write " + write);
                write++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Thread.sleep(10000);
    }
}
