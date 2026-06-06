package dev.threeadd.packeteventssk.config;

import java.util.ArrayList;
import java.util.List;

public class Configurable<T> {

    public static final Configurable<Boolean> UPDATE_CHECKER_ENABLED = register("update-checker.enabled", Boolean.class);
    public static final Configurable<Boolean> UPDATE_CHECKER_ASYNC = register("update-checker.async", Boolean.class);
    public static final Configurable<Boolean> ELEMENTS_ENTITY = register("elements.entity", Boolean.class);
    public static final Configurable<Boolean> ELEMENTS_SIMPLE = register("elements.simple", Boolean.class);

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
        return this.id;
    }

    public Class<T> getType() {
        return this.type;
    }

    public static List<Configurable<?>> getList() {
        return list;
    }
}