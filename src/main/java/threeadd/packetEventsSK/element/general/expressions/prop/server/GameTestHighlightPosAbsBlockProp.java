package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerGameTestHighlightPos;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

@SuppressWarnings("unused")
@Name("General - Game Test Highlight Pos Packet - Absolute Block")
@Description("""
        Represents the absolute block within a game test highlight pos packet.
        This abs block is represented as it's location in a 3d vector.
        """)
@Example("""
        command highlightMyPos:
            trigger:
                create a new game test highlight pos send packet:
                    set packet absolute block of the packet to vector of player's location
                    send packet the packet to the player
        """)
@Since("1.0.0")
public class GameTestHighlightPosAbsBlockProp extends PacketPropertyExpression<WrapperPlayServerGameTestHighlightPos, Vector> {

    static {
        PropertyExpression.register(GameTestHighlightPosAbsBlockProp.class, Vector.class,
                "packet[ ]absolute[ ]block", "packet");
    }

    public GameTestHighlightPosAbsBlockProp() {
        super(Vector.class, PacketType.Play.Server.GAME_TEST_HIGHLIGHT_POS, true, true, null,
                Changer.ChangeMode.SET);
    }

    private Entity getById(int id) {
        return Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(entity -> entity.getEntityId() == id)
                .findAny().orElseThrow(() -> new IllegalArgumentException("Unknow entity with id " + id));
    }

    @Override
    protected Vector get(WrapperPlayServerGameTestHighlightPos input) {
        return new Vector(input.getAbsolutePos().x, input.getAbsolutePos().y, input.getAbsolutePos().z);
    }

    @Override
    protected void change(WrapperPlayServerGameTestHighlightPos wrapper, Changer.ChangeMode mode, Object[] delta) {
        Vector newAbsBlockPos = getDeltaValue(delta, 0, Vector.class);
        Vector3i blockVector = new Vector3i(newAbsBlockPos.getBlockX(), newAbsBlockPos.getBlockY(), newAbsBlockPos.getBlockZ());

        wrapper.setAbsolutePos(blockVector);
    }

    @Override
    public String toString() {
        return "absolute block of game test highlight pos packet";
    }
}
