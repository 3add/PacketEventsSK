package threeadd.packetEventsSK.config;

import java.util.ArrayList;
import java.util.List;

public class Configurable<T> {
    public static final Configurable<Boolean> ELEMENTS_ENTITY = register("elements.entity", Boolean.class);
    public static final Configurable<Boolean> ELEMENTS_SIMPLE = register("elements.simple", Boolean.class);
    public static final Configurable<Boolean> ELEMENTS_TEAM = register("elements.team", Boolean.class);

    private static List<Configurable<?>> list;

    private static <T> Configurable<T> register(String id, Class<T> type) {
        Configurable<T> configurable = new Configurable<>(id, type);
        if (list == null) list = new ArrayList<>();
        list.add(configurable);
        return configurable;
    }

    private final String id;
    private final Class<T> type;

    private Configurable(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return type;
    }

    public static List<Configurable<?>> getList() {
        return list;
    }
}