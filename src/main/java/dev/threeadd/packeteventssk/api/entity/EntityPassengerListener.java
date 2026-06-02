package dev.threeadd.packeteventssk.api.entity;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import dev.threeadd.packeteventssk.api.general.EntityTracker;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class EntityPassengerListener implements PacketListener {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SET_PASSENGERS) return;

        Player player = event.getPlayer();
        UUID userUuid = event.getUser().getUUID();
        if (player == null || userUuid == null) return;

        WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(event);
        int vehicleId = packet.getEntityId();

        Set<Integer> cachedFake = EntityTracker.getFakePassengers(userUuid, vehicleId);
        if (cachedFake == null || cachedFake.isEmpty()) return;

        int[] rawPassengers = packet.getPassengers();

        LinkedHashSet<Integer> updated = new LinkedHashSet<>();
        for (int id : rawPassengers) updated.add(id);

        boolean changed = false;

        for (int fakePassengerId : new LinkedHashSet<>(cachedFake)) {
            WrapperEntity activeFake = EntityLib.getApi().getEntity(fakePassengerId);
            if (activeFake == null) continue;

            if (!activeFake.getViewers().contains(player.getUniqueId())) continue;

            if (updated.add(fakePassengerId)) {
                changed = true;
            }
        }

        if (changed) {
            packet.setPassengers(updated.stream().mapToInt(Integer::intValue).toArray());
        }
    }
}