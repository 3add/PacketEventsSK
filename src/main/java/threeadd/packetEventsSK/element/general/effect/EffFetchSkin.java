package threeadd.packetEventsSK.element.general.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.skin.Skin;
import threeadd.packetEventsSK.element.entity.api.skin.Skins;

@SuppressWarnings("unused")
@Name("General - Fetch Skin")
@Description("""
        Used to fetch a player skin.
        This is processed on an async thread and thus will pause your code until it's been loaded.
        """)
@Example("""
        command changeMySkinForMe <text>:
            trigger:
                fetch skin of player named arg-1 and store it in {_skin}
                if {_skin} isn't set:
                    send "Couldn't find a skin!"
                    stop
        
                set displayed skin of player to {_skin} for player
        """)
@Since("1.0.0")
public class EffFetchSkin extends AsyncEffect {

    static {
        Skript.registerEffect(EffFetchSkin.class,
                "fetch skin (from|of) player named %string% and store (it|the result) in %-~objects%",
                "set %objects% to skin (from|of) player named %string%");
    }

    private Expression<String> nameExpr;
    private Variable<?> variable;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        if (matchedPattern == 0) {
            this.nameExpr = (Expression<String>) expressions[0];
            this.variable = (Variable<?>) expressions[1];

        } else {
            this.variable = (Variable<?>) expressions[0];
            this.nameExpr = (Expression<String>) expressions[1];
        }

        return true;
    }

    @Override
    protected void execute(Event event) {

        String name = nameExpr.getSingle(event);

        Skin skin;
        try {
            skin  = Skins.getOfflinePlayer(name);
        } catch (IllegalStateException error) {
            return;
        }

        variable.change(event, new Skin[]{skin}, Changer.ChangeMode.SET);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "";
    }
}
