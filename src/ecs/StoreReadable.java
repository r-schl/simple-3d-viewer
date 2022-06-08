package ecs;
public interface StoreReadable {


    public <T extends Component> T getComponent0(int entityId, Class<T> type);
    public <T extends Component> T getComponent(int sceneId, int entityId, Class<T> type);

    public <T extends Component> T getComponentOrStandard0(int entityId, Class<T> type);
    public <T extends Component> T getComponentOrStandard(int sceneId, int entityId, Class<T> type);

    public int[] filterEntities0(Class<? extends Component>... types);
    public int[] filterEntities(int sceneId, Class<? extends Component>... types);

    public int countEntities0(Class<? extends Component>... types);
    public int countEntities(int sceneId, Class<? extends Component>... types);

/*     public <T> T getStampValue(int entityId, String stampName, Class<T> clazz);

    public int[] filterEntities(String... stampNames); */

}
