package threeadd.packetEventsSK.element.general.api;

import ch.njol.skript.lang.Trigger;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import threeadd.packetEventsSK.element.general.structures.PacketEventStruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketEventRegistry {

    public record RegisteredListener(ListenerData data, Trigger trigger) {}

    private static final Map<PacketTypeCommon, List<RegisteredListener>> registry = new ConcurrentHashMap<>();

    private PacketEventRegistry() {}

    public static void addTrigger(ListenerData listenerData, Trigger trigger) {
        PacketTypeCommon type = listenerData.packetType;

        if (type == null)
            throw new IllegalStateException("Cannot register trigger: PacketWrapper class is null in PacketEventData.");

        registry.computeIfAbsent(type, k -> new ArrayList<>())
                .add(new RegisteredListener(listenerData, trigger));
    }

    public static void removeTrigger(Trigger trigger) {
        for (List<RegisteredListener> listeners : registry.values())
            listeners.removeIf(entry -> entry.trigger().equals(trigger));
    }

    public static List<RegisteredListener> getListeners(PacketTypeCommon packetType) {
        return registry.getOrDefault(packetType, new ArrayList<>());
    }

    public static boolean hasTrigger(PacketTypeCommon packetType) {
        return registry.containsKey(packetType);
    }

    public record ListenerData(PacketTypeCommon packetType, PacketEventStruct.ProcessWay processWay) {

        public ListenerData(PacketEventStruct.PacketEventParserData data) {
            this(data.getPacketType(), data.getProcessWay());
        }

    }
}
