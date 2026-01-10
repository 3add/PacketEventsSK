package threeadd.packetEventsSK.util.registry;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class PacketTypeRegistry {

    private static final Map<String, PacketTypeCommon> SEND_PACKETS_BY_NAME = new ConcurrentHashMap<>();
    private static final Map<String, PacketTypeCommon> RECEIVE_PACKETS_BY_NAME = new ConcurrentHashMap<>();

    static {
        BiConsumer<Map<String, PacketTypeCommon>, PacketTypeCommon[]> populateMap = (map, types) -> {
            for (PacketTypeCommon type : types) {
                map.put(type.getName().toUpperCase(Locale.ENGLISH), type);
            }
        };

        populateMap.accept(SEND_PACKETS_BY_NAME, PacketType.Play.Server.values());
        populateMap.accept(SEND_PACKETS_BY_NAME, PacketType.Configuration.Server.values());
        populateMap.accept(SEND_PACKETS_BY_NAME, PacketType.Login.Server.values());
        populateMap.accept(SEND_PACKETS_BY_NAME, PacketType.Handshaking.Server.values());
        populateMap.accept(SEND_PACKETS_BY_NAME, PacketType.Status.Server.values());

        populateMap.accept(RECEIVE_PACKETS_BY_NAME, PacketType.Play.Client.values());
        populateMap.accept(RECEIVE_PACKETS_BY_NAME, PacketType.Configuration.Client.values());
        populateMap.accept(RECEIVE_PACKETS_BY_NAME, PacketType.Login.Client.values());
        populateMap.accept(RECEIVE_PACKETS_BY_NAME, PacketType.Handshaking.Client.values());
        populateMap.accept(RECEIVE_PACKETS_BY_NAME, PacketType.Status.Client.values());
    }

    public static @Nullable PacketTypeCommon getPacket(String rawName, boolean isSend) {
        String key = rawName.replace(" ", "_").toUpperCase(Locale.ENGLISH);

        if (isSend) {
            return SEND_PACKETS_BY_NAME.get(key);
        } else {
            return RECEIVE_PACKETS_BY_NAME.get(key);
        }
    }
}