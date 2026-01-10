package threeadd.packetEventsSK.element.entity.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.effect.CustomEffect;

import java.util.List;

@SuppressWarnings("unused")
@Name("Fake Entity - Kill Fake Entity")
@Description("Used to kill a spawned fake entity")
@Example("""
        command spawn:
            trigger:
                create new fake dropped item entity at player for players and store it in {_e}:
                    set fake item stack of the fake entity to dirt
                wait 5 seconds
                kill fake entity {_e}
        """)
@Since("1.0.0")
public class EffKill extends CustomEffect {

    static {
        Skript.registerEffect(EffKill.class,
                "kill fake[ ]entit(y|ies) %fakeentities%");
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        List<WrapperEntity> entities = getValuesOrNull(0, WrapperEntity.class, event);
        if (entities == null) return;

        entities.forEach(WrapperEntity::remove);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "kill packet entity effect";
    }
}
