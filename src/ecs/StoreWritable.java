package ecs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StoreWritable implements StoreReadable {

    private Map<Class<? extends Component>, Map<Integer, Component>> componentsMap = Collections
            .synchronizedMap(new HashMap<>());

    @SafeVarargs
    public final <T extends Component> void putComponent(int entityId, T... components) {
        for (T component : components) {
            Class<?> clazz = component.getClass();
            while (clazz != Component.class) {
                @SuppressWarnings("unchecked")
                Class<T> type = (Class<T>) clazz;
                componentsMap.computeIfAbsent(type, k -> new HashMap<>());
                componentsMap.get(type).put(entityId, component);
                clazz = clazz.getSuperclass();
            }
        }
    }

    @Override
    public <T extends Component> T getComponent(int entityId, Class<T> type) {
        if (!this.componentsMap.containsKey(type) || !this.componentsMap.get(type).containsKey(entityId)) {
            return null;
        }
        T component = type.cast(this.componentsMap.get(type).get(entityId));
        return component;
    }

    public <T extends Component> void removeComponent(int entityId, Class<T> type) {
        if (!this.componentsMap.containsKey(type) || !this.componentsMap.get(type).containsKey(entityId)) {
            return;
        }
        this.componentsMap.get(type).remove(entityId);
    }

    public <T extends Component> void removeEntity(int entityId) {
        for (Class<? extends Component> clazz : componentsMap.keySet()) {
            @SuppressWarnings("unchecked")
            Class<T> type = (Class<T>) clazz;
            this.removeComponent(entityId, type);
        }
    }

    @Override
    @SafeVarargs
    public final int[] filterEntities(Class<? extends Component>... types) {
        if (types.length == 0)
            return new int[] {};
        if (types.length == 1) {
            if (!this.componentsMap.containsKey(types[0]))
                return new int[] {};
            Set<Integer> entitySet = this.componentsMap.get(types[0]).keySet();
            return entitySet.stream().mapToInt(x -> x).toArray();
        } else {
            ArrayList<Integer> entityList = new ArrayList<>();
            for (int i = 0; i < types.length; i++) {
                if (!this.componentsMap.containsKey(types[i]))
                    return new int[] {};
                Set<Integer> entitiesWithComponent = this.componentsMap.get(types[i]).keySet();
                if (entitiesWithComponent.size() == 0) {
                    return new int[] {};
                } else {
                    if (i == 0)
                        entityList.addAll(entitiesWithComponent);
                    entityList.removeIf(entityId -> !entitiesWithComponent.contains(entityId));
                }
            }
            return entityList.stream().mapToInt(i -> i).toArray();
        }
    }

    @Override
    public int countEntities(Class<? extends Component>... types) {
        return filterEntities(types).length;
    }

    @Override
    public <T extends Component> T getComponentOrStandard(int entityId, Class<T> type) {
        T component = this.getComponent(entityId, type);
        if (component == null) {
            try {
                Method standard = type.getMethod("standard");
                component = (T) standard.invoke(null);
            } catch (NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return component;
    }

}
