package ecs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StoreWritable implements StoreReadable {

    /*
     * Here you can save components for an entity. There are different scenes (they
     * are independent from each other). The main scene is scene number 0. An entity
     * is addressed by an Id. For each scene, each entity can have only one
     * component of the same type (class).
     * 
     */

    private Map<Class<? extends Component>, Map<Integer, Map<Integer, Component>>> componentsMap = Collections
            .synchronizedMap(new HashMap<>());

    // private Map<String, Map<Integer, Object>> stampMap =
    // Collections.synchronizedMap(new HashMap<>());

    @SafeVarargs
    public final <T extends Component> void putComponent0(int entityId, T... components) {
        this.putComponent(0, entityId, components);
    }

    @SafeVarargs
    public final <T extends Component> void putComponent(int sceneId, int entityId, T... components) {
        for (T component : components) {
            Class<?> clazz = component.getClass();
            while (clazz != Component.class) {
                @SuppressWarnings("unchecked")
                Class<T> type = (Class<T>) clazz;
                componentsMap.computeIfAbsent(type, k -> new HashMap<>());
                componentsMap.get(type).computeIfAbsent(sceneId, k -> new HashMap<>());
                componentsMap.get(type).get(sceneId).put(entityId, component);
                clazz = clazz.getSuperclass();
            }
        }
    }

    /*
     * public <T> void putStamp(int entityId, String stampName, T stampValue) {
     * stampMap.computeIfAbsent(stampName, k -> new HashMap<>());
     * stampMap.get(stampName).put(entityId, stampValue);
     * }
     * 
     * @Override
     * public <T> T getStampValue(int entityId, String stampName, Class<T> clazz) {
     * if (!this.stampMap.containsKey(stampName))
     * return null;
     * if (!this.stampMap.get(stampName).containsKey(entityId))
     * return null;
     * Object stampValue = this.stampMap.get(stampName).get(entityId);
     * return clazz.cast(stampValue);
     * }
     * 
     * @Override
     * public int[] filterEntities(String... stampNames) {
     * if (stampNames.length == 0)
     * return new int[] {};
     * if (stampNames.length == 1) {
     * if (!this.stampMap.containsKey(stampNames[0]))
     * return new int[] {};
     * Set<Integer> entitySet = this.stampMap.get(stampNames[0]).keySet();
     * return entitySet.stream().mapToInt(x -> x).toArray();
     * } else {
     * ArrayList<Integer> entityList = new ArrayList<>();
     * for (int i = 0; i < stampNames.length; i++) {
     * if (!this.stampMap.containsKey(stampNames[i]))
     * return new int[] {};
     * Set<Integer> entitiesWithStampName =
     * this.stampMap.get(stampNames[i]).keySet();
     * if (entitiesWithStampName.size() == 0) {
     * return new int[] {};
     * } else {
     * if (i == 0)
     * entityList.addAll(entitiesWithStampName);
     * entityList.removeIf(entityId -> !entitiesWithStampName.contains(entityId));
     * }
     * }
     * return entityList.stream().mapToInt(i -> i).toArray();
     * }
     * }
     */

    @Override
    public <T extends Component> T getComponent0(int entityId, Class<T> type) {
        return this.getComponent(0, entityId, type);
    }

    @Override
    public <T extends Component> T getComponent(int sceneId, int entityId, Class<T> type) {
        if (!this.componentsMap.containsKey(type) || !this.componentsMap.get(type).containsKey(sceneId)
                || !this.componentsMap.get(type).get(sceneId).containsKey(entityId)) {
            return null;
        }
        T component = type.cast(this.componentsMap.get(type).get(sceneId).get(entityId));
        return component;
    }

    public <T extends Component> void removeComponent0(int entityId, Class<T> type) {
        this.removeComponent(0, entityId, type);
    }

    public <T extends Component> void removeComponent(int sceneId, int entityId, Class<T> type) {
        if (!this.componentsMap.containsKey(type) || !this.componentsMap.get(type).containsKey(sceneId)
                || !this.componentsMap.get(type).get(sceneId).containsKey(entityId)) {
            return;
        }
        this.componentsMap.get(type).get(sceneId).remove(entityId);
    }

    public <T extends Component> void removeEntity0(int entityId) {
        this.removeEntity(0, entityId);
    }

    public <T extends Component> void removeEntity(int sceneId, int entityId) {
        for (Class<? extends Component> clazz : componentsMap.keySet()) {
            @SuppressWarnings("unchecked")
            Class<T> type = (Class<T>) clazz;
            this.removeComponent(sceneId, entityId, type);
        }
    }

    @Override
    @SafeVarargs
    public final int[] filterEntities0(Class<? extends Component>... types) {
        return this.filterEntities(0, types);
    }

    @Override
    @SafeVarargs
    public final int[] filterEntities(int sceneId, Class<? extends Component>... types) {
        if (types.length == 0)
            return new int[] {};
        if (types.length == 1) {
            if (!this.componentsMap.containsKey(types[0]))
                return new int[] {};
            Set<Integer> entitySet = this.componentsMap.get(types[0]).get(sceneId).keySet();
            return entitySet.stream().mapToInt(x -> x).toArray();
        } else {
            ArrayList<Integer> entityList = new ArrayList<>();
            for (int i = 0; i < types.length; i++) {
                if (!this.componentsMap.containsKey(types[i]))
                    return new int[] {};
                Set<Integer> entitiesWithComponent = this.componentsMap.get(types[i]).get(sceneId).keySet();
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
    @SafeVarargs
    public final int countEntities0(Class<? extends Component>... types) {
        return filterEntities(0, types).length;
    }

    @Override
    @SafeVarargs
    public final int countEntities(int sceneId, Class<? extends Component>... types) {
        return filterEntities(sceneId, types).length;
    }

    @Override
    public <T extends Component> T getComponentOrStandard0(int entityId, Class<T> type) {
        return this.getComponentOrStandard(0, entityId, type);
    }

    @Override
    public <T extends Component> T getComponentOrStandard(int sceneId, int entityId, Class<T> type) {
        T component = this.getComponent(sceneId, entityId, type);
        if (component == null) {
            try {
                Method standard = type.getMethod("standard");
                component = (T) standard.invoke(null);
            } catch (NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
            }
        }
        return component;
    }

}
