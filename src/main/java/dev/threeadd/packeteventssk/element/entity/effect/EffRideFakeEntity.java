package dev.threeadd.packeteventssk.element.entity.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EffRideFakeEntity extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffRideFakeEntity.class,
                        "(make|let|force) fake[ ]entit(y|ies) %fakeentities% [to] (ride|mount) %fakeentity/livingentity%",
                        "(make|let|force) fake[ ]entit(y|ies) %fakeentities% [to] (dismount|(dismount|leave) as passenger[s]) (from|of) %fakeentity/livingentity%")
                .name("Fake Entity - Ride")
                .description("""
                        Make fake entities ride another entity (fake or real), or remove them as passengers.
                        **Note: Real entities are just a single packet being sent, when a player relogs, changes worlds or some other circumstances the passengers will be altered by the vanilla server.**
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

    private Expression<WrapperEntity> passengerExpr;
    private Expression<Object> vehicleExpr;
    private boolean mount;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.passengerExpr = (Expression<WrapperEntity>) expressions[0];
        this.vehicleExpr = (Expression<Object>) expressions[1];
        mount = matchedPattern == 0;

        return true;
    }

    @Override
    protected void execute(Event event) {
        WrapperEntity[] passengers = passengerExpr.getAll(event);
        Object vehicle = vehicleExpr.getSingle(event);

        if (passengers == null || passengers.length == 0 || vehicle == null) return;

        if (vehicle instanceof WrapperEntity fakeVehicle) {
            for (WrapperEntity passenger : passengers) {
                if (mount) {
                    fakeVehicle.addPassenger(passenger);
                } else {
                    fakeVehicle.removePassenger(passenger);
                }
            }
            return;
        }

        if (vehicle instanceof Entity realVehicle) {
            int vehicleId = realVehicle.getEntityId();

            List<Entity> realPassengers = realVehicle.getPassengers();
            int[] passengerIds;

            if (mount) {
                passengerIds = new int[realPassengers.size() + passengers.length];
                for (int i = 0; i < realPassengers.size(); i++) {
                    passengerIds[i] = realPassengers.get(i).getEntityId();
                }
                for (int i = 0; i < passengers.length; i++) {
                    passengerIds[realPassengers.size() + i] = passengers[i].getEntityId();
                }
            } else {
                passengerIds = new int[realPassengers.size()];
                for (int i = 0; i < realPassengers.size(); i++) {
                    passengerIds[i] = realPassengers.get(i).getEntityId();
                }
            }

            WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicleId, passengerIds);

            // Send the packet to everyone who can see these fake entities
            for (WrapperEntity passenger : passengers) {
                passenger.sendPacketToViewers(packet);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (mount ? "make fake entities " : "remove fake entities ")
                + passengerExpr.toString(event, debug)
                + (mount ? " ride " : " as passengers of ")
                + vehicleExpr.toString(event, debug);
    }
}