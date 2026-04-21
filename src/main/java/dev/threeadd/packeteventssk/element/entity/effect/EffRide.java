package dev.threeadd.packeteventssk.element.entity.effect;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import dev.threeadd.packeteventssk.util.effect.CustomEffect;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@SuppressWarnings("unused")
@Name("Fake Entity - Ride")
@Description("Make fake entities ride another entity (fake or real), or remove them as passengers.")
@Example("""
        command test:
            trigger:
                spawn a new fake text display entity at player for players and store it in {_display}
                make fake entity {_display} ride player
                wait 5 seconds
                remove fake entity {_display} as passenger of player
        """)
@Since("1.0.1")
public class EffRide extends CustomEffect {

  public static void register(SyntaxRegistry registry) {
    registry.register(
            SyntaxRegistry.EFFECT,
            SyntaxInfo.builder(EffRide.class)
                    .addPatterns(
                            "(make|let|force) fake[ ]entit(y|ies) %fakeentities% [to] (ride|mount) %fakeentity/livingentity%",
                            "(make|let|force) fake[ ]entit(y|ies) %fakeentities% [to] (dismount|(dismount|leave) as passenger[s]) (from|of) %fakeentity/livingentity%"
                    )
                    .build()
    );
  }

  @Override
  protected boolean initialize(SkriptParser.ParseResult parseResult) {
    return true;
  }

  @Override
  protected void execute(Event event) {
    List<WrapperEntity> passengers = getValuesOrNull(0, WrapperEntity.class, event);
    if (passengers == null) return;

    Object vehicle = getValueOrNull(1, Object.class, event);
    if (vehicle == null) return;

    if (vehicle instanceof WrapperEntity fakeVehicle) {
      if (patternIndex == 0) {
        passengers.forEach(fakeVehicle::addPassenger);
      } else {
        passengers.forEach(fakeVehicle::removePassenger);
      }
      return;
    }

    if (vehicle instanceof org.bukkit.entity.Entity realVehicle) {
      int vehicleId = realVehicle.getEntityId();
      int[] passengerIds = patternIndex == 0
              ? passengers.stream().mapToInt(WrapperEntity::getEntityId).toArray()
              : new int[0];

      WrapperPlayServerSetPassengers packet = new WrapperPlayServerSetPassengers(vehicleId, passengerIds);
      passengers.forEach(passenger -> passenger.sendPacketToViewers(packet));
    }
  }

  @Override
  public String toString(@Nullable Event event, boolean debug) {
    return "fake entity ride effect";
  }
}