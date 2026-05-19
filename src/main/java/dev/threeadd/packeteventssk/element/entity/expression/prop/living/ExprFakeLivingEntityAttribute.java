package dev.threeadd.packeteventssk.element.entity.expression.prop.living;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes.Property;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprFakeLivingEntityAttribute extends PropertyExpression<WrapperEntity, Number> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeLivingEntityAttribute.class, Number.class, "fake %attributetype% attribute [value]", "fakeentity")
                .name("Fake Living Entity Property - Attribute")
                .description("The attribute of a fake living entity.")
                .examples("""
                        command test:
                            trigger:
                                spawn a new fake player entity at player for players and store it in {_e}
                                set {_e}'s fake scale attribute to 2
                        """)
                .since("1.0.1")
                .register();
    }

    private Expression<Attribute> attributeExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (matchedPattern == 0) {
            this.attributeExpr = (Expression<Attribute>) expressions[0];
            setExpr((Expression<? extends WrapperEntity>) expressions[1]);
        } else {
            setExpr((Expression<? extends WrapperEntity>) expressions[0]);
            this.attributeExpr = (Expression<Attribute>) expressions[1];
        }
        return true;
    }

    @Override
    protected Number[] get(Event event, WrapperEntity[] source) {
        Attribute bukkitAttr = this.attributeExpr.getSingle(event);
        if (bukkitAttr == null) return new Number[0];

        Number[] values = new Number[source.length];
        for (int i = 0; i < source.length; i++) {
            values[i] = getAttributeValue(source[i], bukkitAttr);
        }
        return values;
    }

    private @Nullable Number getAttributeValue(WrapperEntity entity, Attribute bukkitAttr) {
        if (entity instanceof WrapperLivingEntity livingFake) {
            com.github.retrooper.packetevents.protocol.attribute.Attribute peAttr = Attributes.getByName(bukkitAttr.getKey().asString());

            return livingFake.getAttributes().getProperties().stream()
                    .filter(prop -> prop.getAttribute() == peAttr)
                    .findFirst()
                    .map(Property::getValue)
                    .orElse(null);
        }
        return null;
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        Attribute bukkitAttr = this.attributeExpr.getSingle(event);
        if (bukkitAttr == null) return;

        WrapperEntity[] entities = getExpr().getAll(event);
        if (entities == null || entities.length == 0) return;

        double deltaValue = 0;

        if (mode != Changer.ChangeMode.RESET) {
            if (delta == null || delta.length == 0 || !(delta[0] instanceof Number newNumber)) return;
            deltaValue = newNumber.doubleValue();
        }

        for (WrapperEntity entity : entities) {
            if (entity instanceof WrapperLivingEntity livingFake) {
                com.github.retrooper.packetevents.protocol.attribute.Attribute peAttr = Attributes.getByName(bukkitAttr.getKey().asString());

                double currentValue = livingFake.getAttributes().getProperties().stream()
                        .filter(prop -> prop.getAttribute() == peAttr)
                        .findFirst()
                        .map(Property::getValue)
                        .orElse(0d);

                switch (mode) {
                    case SET -> livingFake.getAttributes().setAttribute(peAttr, deltaValue);
                    case ADD -> livingFake.getAttributes().setAttribute(peAttr, currentValue + deltaValue);
                    case REMOVE -> livingFake.getAttributes().setAttribute(peAttr, currentValue - deltaValue);
                    case RESET -> livingFake.getAttributes().setAttribute(peAttr, peAttr.getDefaultValue());
                }
            }
        }
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String attribute = this.attributeExpr.toString(event, debug);
        String entity = getExpr().toString(event, debug);
        return String.format("fake %s attribute of %s", attribute, entity);
    }
}