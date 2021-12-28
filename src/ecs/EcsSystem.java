
package ecs;

import java.util.function.Consumer;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class EcsSystem {

    private Store store = null;
    private EventDispatcher eventDispatcher = null;
    private Thread thread;

    private ConcurrentLinkedQueue<Object> eventQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Consumer<Object>> consumerQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Runnable> runnableQueue = new ConcurrentLinkedQueue<>();

    private Object synchronizedObject = new Object();

    public Store store() {
        return this.store;
    }

    public void start() {
        this.init();
    }

    public void startOnThread() {
        this.thread = new Thread(() -> {
            this.init();
            while (true) {
                Object event = eventQueue.poll();
                if (event != null && event instanceof String) {
                    Runnable runnable = runnableQueue.poll();
                    if (runnable != null)
                        runnable.run();
                } else if (event != null) {
                    Consumer<Object> consumer = consumerQueue.poll();
                    if (consumer != null)
                        consumer.accept(event);
                }

                synchronized (synchronizedObject) {
                    try {
                        synchronizedObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public abstract void init();

    public <T extends Event> void register(Class<T> clazz, int priority, Consumer<?> consumer) {
        this.eventDispatcher.register(clazz, priority, consumer);
    }

    public <T extends Event> void registerOwnThread(Class<T> clazz, int priority, Consumer<?> consumer) {
        this.eventDispatcher.register(clazz, priority, new Consumer<T>() {
            public void accept(T t) {
                eventQueue.add(t);
                @SuppressWarnings("unchecked")
                Consumer<Object> c = (Consumer<Object>) consumer;
                consumerQueue.add(c);
                synchronized (synchronizedObject) {
                    synchronizedObject.notify();
                }
            }
        });
    }

    public void register(String message, int priority, Runnable runnable) {
        this.eventDispatcher.register(message, priority, runnable);
    }

    public void registerOwnThread(String message, int priority, Runnable runnable) {
        this.eventDispatcher.register(message, priority, new Runnable() {
            public void run() {
                eventQueue.add(message);
                runnableQueue.add(runnable);
                synchronized (synchronizedObject) {
                    synchronizedObject.notify();
                }
            }
        });
    }

    public void trigger(Event event) {
        event.setSource(this);
        this.eventDispatcher.trigger(event);
    }

    public void trigger(String messsage) {
        this.eventDispatcher.trigger(messsage);
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void print(String text) {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            System.out.println("[" + enclosingClass.getName() + "] " + text);
        } else {
            System.out.println("[" + getClass().getName() + "] " + text);
        }
    }

}
