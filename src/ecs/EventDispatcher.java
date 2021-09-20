package ecs;

import java.util.*;
import java.util.function.Consumer;

public class EventDispatcher {

    // Mapping order: event type => priority => set of consumers
    Map<Class<? extends Event>, Map<Integer, Set<Consumer<Object>>>> mapEventObjects = Collections
            .synchronizedMap(new HashMap<>());

    // Mapping order: message => priority => set of runnables
    Map<String, Map<Integer, Set<Runnable>>> mapEventMessages = Collections.synchronizedMap(new HashMap<>());

    public void register(Class<? extends Event> clazz, int priority, Consumer<?> consumer) {
        this.mapEventObjects.computeIfAbsent(clazz, k -> new HashMap<>());
        this.mapEventObjects.get(clazz).computeIfAbsent(priority, k -> new HashSet<>());
        @SuppressWarnings("unchecked")
        Consumer<Object> c = (Consumer<Object>) consumer;
        this.mapEventObjects.get(clazz).get(priority).add(c);
    }

    public void register(String message, int priority, Runnable runnable) {
        this.mapEventMessages.computeIfAbsent(message, k -> new HashMap<>());
        this.mapEventMessages.get(message).computeIfAbsent(priority, k -> new HashSet<>());
        this.mapEventMessages.get(message).get(priority).add(runnable);
    }

    public void trigger(Event event) {
        if (this.mapEventObjects.containsKey(event.getClass())) {
            Map<Integer, Set<Consumer<Object>>> map = this.mapEventObjects.get(event.getClass());
            List<Integer> sortedPriorities = new ArrayList<>(map.keySet());
            Collections.sort(sortedPriorities);
            for (Integer priority : sortedPriorities) {
                for (Consumer<Object> consumer : map.get(priority)) {
                    consumer.accept(event);
                }
            }
        }
    }

    public void trigger(String message) {
        if (this.mapEventMessages.containsKey(message)) {
            Map<Integer, Set<Runnable>> map = this.mapEventMessages.get(message);
            List<Integer> sortedPriorities = new ArrayList<>(map.keySet());
            Collections.sort(sortedPriorities, Collections.reverseOrder());
            for (Integer priority : sortedPriorities) {
                for (Runnable runnable : map.get(priority)) {
                    runnable.run();
                }
            }
        }
    }

}
