package ecs;
public interface StoreReadable {

    public <T extends Component> T getComponent(int entityId, Class<T> type);

    public int[] filterEntities(Class<? extends Component>... types);

    public int countEntities(Class<? extends Component>... types);

}
