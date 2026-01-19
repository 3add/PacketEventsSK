package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete.CommandMatch;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Name("General - Tab Completions Packet - Completions")
@Description("""
        Represents all the tab completions within a tab completions packet.
        These commands are represented by a simple string.
        """)
@Example("""
        on tab complete send netty processed:
            loop packet completions of event-packet:
                if loop-value is "disable":
                    remove loop-value from packet completions of event-packet
                    send "Why would you want to disable me?" to event-player
        """)
@Since("1.0.0")
public class TabCompleteTabCompletionsProp extends PacketPropertyExpression<WrapperPlayServerTabComplete, String> {

    static {
        PropertyExpression.register(TabCompleteTabCompletionsProp.class, String.class, "packet[ ]completions", "packet");
    }

    public TabCompleteTabCompletionsProp() {
        super(String.class, PacketType.Play.Server.TAB_COMPLETE, false, true, null,
                Changer.ChangeMode.SET,
                Changer.ChangeMode.ADD,
                Changer.ChangeMode.REMOVE,
                Changer.ChangeMode.REMOVE_ALL,
                Changer.ChangeMode.RESET);
    }

    @Override
    protected @Nullable List<String> getMany(WrapperPlayServerTabComplete packet) {
        return packet.getCommandMatches().stream()
                .map(CommandMatch::getText)
                .toList();
    }

    @Override
    protected void change(WrapperPlayServerTabComplete wrapper, Changer.ChangeMode mode, Object[] delta) {
        List<CommandMatch> matches = new ArrayList<>(wrapper.getCommandMatches());
        List<String> values = getDeltaValues(delta, String.class);

        switch (mode) {
            case SET -> {
                matches.clear();
                for (String val : values) {
                    matches.add(new CommandMatch(val));
                }
            }

            case ADD -> {
                for (String val : values) {
                    matches.add(new CommandMatch(val));
                }
            }

            case REMOVE -> matches.removeIf(match -> values.contains(match.getText()));
            case REMOVE_ALL, RESET, DELETE -> matches.clear();
        }

        wrapper.setCommandMatches(matches);
    }

    @Override
    public String toString() {
        return "tab completions of tab completions packet";
    }
}
