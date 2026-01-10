package threeadd.packetEventsSK.util.registry;

import java.util.ArrayList;
import java.util.List;

public abstract class Registry<T> {

    private final List<T> registeredItems = new ArrayList<>();

    @SafeVarargs
    public final Registry<T> register(T... items) {
        registeredItems.addAll(List.of(items));
        return this;
    }

    public final Registry<T> register(List<T> items) {
        registeredItems.addAll(items);
        return this;
    }

    public final <E extends T> E register(E item) {
        registeredItems.add(item);
        return item;
    }

    public final void unRegister(T item) {
        registeredItems.remove(item);
    }

    public final List<T> getRegisteredItems() {
        return List.copyOf(registeredItems);
    }
}
