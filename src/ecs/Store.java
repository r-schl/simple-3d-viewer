package ecs;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class Store {

    private ReentrantReadWriteLock secLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = secLock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = secLock.writeLock();

    StoreWritable storeWritable;

    public Store() {
        this.storeWritable = new StoreWritable();
    }

    public void read(Consumer<StoreReadable> query) {
        this.readLock.lock();
        query.accept(this.storeWritable);
        this.readLock.unlock();
    }

    public void write(Consumer<StoreWritable> query) {
        this.writeLock.lock();
        query.accept(this.storeWritable);
        this.writeLock.unlock();
    }

}
