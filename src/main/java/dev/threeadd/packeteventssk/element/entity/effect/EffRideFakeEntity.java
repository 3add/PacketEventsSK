package dev.threeadd.packeteventssk.element.entity.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.EntityTracker;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class EffRideFakeEntity extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffRideFakeEntity.class,
                        "(make|let|force) fake [entit(y|ies)] %fakeentities% [to] (ride|mount) %fakeentity/livingentity%",
                        "(make|let|force) fake [entit(y|ies)] %fakeentities% [to] (dismount|(dismount|leave) as passenger[s]) (from|of) %fakeentity/livingentity%")
                .name("Fake Entity - Ride")
                .description("""
                          Make fake entities ride another entity (fake or real), or remove them as passengers.
                          For fake entities the server tracks the entities and appends to outgoing packets.
                        """)
                .examples("""
                        command test:
                            trigger:
                                create a new fake text display entity at player for players and store it in {_display}
                                set fake display text of {_display} to "<rainbow>hey"
                                set fake display billboard of {_display} to center
                                make fake entity {_display} ride player
                                wait 5 seconds
                                force fake entity {_display} to dismount as passenger of player
                        """)
                .since("1.0.1", "1.1.0 (added note)")
                .register();
    }

    private Expression<?> passengerExpr;
    private Expression<?> vehicleExpr;
    private boolean mount;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.passengerExpr = expressions[0];
        this.vehicleExpr = expressions[1];
        this.mount = matchedPattern == 0;
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object[] rawPassengers = passengerExpr.getAll(event);
        Object vehicle = vehicleExpr.getSingle(event);

        if (rawPassengers == null || vehicle == null) {
            return;
        }

        if (vehicle instanceof WrapperEntity fakeEntity) {
            handleFakeVehicle(fakeEntity, rawPassengers);
        } else if (vehicle instanceof Entity bukkitEntity) {
            handleVanillaVehicle(bukkitEntity, rawPassengers);
        }
    }

    private void handleFakeVehicle(WrapperEntity fakeEntity, Object[] rawPassengers) {
        for (Object raw : rawPassengers) {
            if (raw instanceof WrapperEntity passenger) {
                if (this.mount) {
                    fakeEntity.addPassenger(passenger);
                } else {
                    fakeEntity.removePassenger(passenger);
                }
            }
        }
    }

    private void handleVanillaVehicle(Entity bukkitEntity, Object[] rawPassengers) {
        int vehicleId = bukkitEntity.getEntityId();

        Set<Player> viewers = Arrays.stream(rawPassengers)
                .filter(WrapperEntity.class::isInstance)
                .map(WrapperEntity.class::cast)
                .flatMap(passenger -> passenger.getViewers().stream())
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .collect(Collectors.toSet());

        if (viewers.isEmpty()) {
            viewers = bukkitEntity.getWorld().getPlayers().stream()
                    .filter(Player::isOnline)
                    .collect(Collectors.toSet());
        }

        int[] vanillaPassengers = bukkitEntity.getPassengers().stream().mapToInt(Entity::getEntityId).toArray();

        for (Player player : viewers) {
            UUID uuid = player.getUniqueId();
            Set<Integer> cachedFake = EntityTracker.getFakePassengers(uuid, vehicleId);
            Set<Integer> targetFakePassengers = new LinkedHashSet<>(cachedFake != null ? cachedFake : Collections.emptySet());

            for (Object raw : rawPassengers) {
                if (raw instanceof WrapperEntity passenger) {
                    if (this.mount) {
                        targetFakePassengers.add(passenger.getEntityId());
                    } else {
                        targetFakePassengers.remove(passenger.getEntityId());
                    }
                }
            }

            int[] blendedArray = new int[vanillaPassengers.length + targetFakePassengers.size()];
            System.arraycopy(vanillaPassengers, 0, blendedArray, 0, vanillaPassengers.length);

            int index = vanillaPassengers.length;
            for (int id : targetFakePassengers) {
                blendedArray[index++] = id;
            }

            WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicleId, blendedArray);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String action = this.mount ? "make fake entities" : "remove fake entities";
        String passenger = this.passengerExpr.toString(event, debug);
        String relation = this.mount ? " ride " : " as passengers of ";
        String vehicle = this.vehicleExpr.toString(event, debug);
        return String.format("%s %s%s%s", action, passenger, relation, vehicle);
    }
}