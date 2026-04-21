package dev.threeadd.packeteventssk.element.entity.expressions.prop.living;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes.Property;
import dev.threeadd.packeteventssk.util.expressions.CustomPropertyExpression;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@SuppressWarnings("unused")
@Name("Fake Living Entity - Attribute")
@Description("Get or set an attribute value of a fake living entity.")
@Example("""
        command test:
            trigger:
                spawn a new fake player entity at player for players and store it in {_e}
                set {_e}'s scale fake attribute to 2
        """)
@Since("1.0.1")
public class AttributeProp extends CustomPropertyExpression<Object, Number> {

  public static void register(SyntaxRegistry registry) {
    registry.register(
            SyntaxRegistry.EXPRESSION,
            SyntaxInfo.Expression.builder(AttributeProp.class, Number.class)
                    .supplier(AttributeProp::new)
                    .addPatterns(
                            "[the] %attributetype% fake attribute [value] of %fakeentity%",
                            "%fakeentity%'[s] %attributetype% fake attribute [value]"
                    )
                    .build()
    );
  }

  private Expression<Attribute> attributeExpr;

  public AttributeProp() {
    super(Number.class, Object.class, true);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected boolean initialize(SkriptParser.ParseResult parseResult) {
    attributeExpr = (Expression<Attribute>) (patternIndex == 0 ? expressions[0] : expressions[1]);
    return true;
  }

  @Override
  protected @Nullable Number getProperty(Object entity) {
    Attribute bukkitAttr = attributeExpr.getSingle(null);
    if (bukkitAttr == null) return null;

    if (entity instanceof WrapperLivingEntity livingFake) {
      com.github.retrooper.packetevents.protocol.attribute.Attribute peAttr = Attributes.getByName(bukkitAttr.getKey().asString());
      if (peAttr == null) return null;

      return livingFake.getAttributes().getProperties().stream()
              .filter(prop -> prop.getAttribute() == peAttr)
              .findFirst()
              .map(Property::getValue)
              .orElse(null);

    } else if (entity instanceof LivingEntity livingEntity) {
      AttributeInstance instance = livingEntity.getAttribute(bukkitAttr);
      if (instance == null) return null;
      return instance.getBaseValue();
    }

    return null;
  }

  @Override
  public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
    if (mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE)
      return new Class[]{Number.class};
    return null;
  }

  @Override
  public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
    Attribute bukkitAttr = attributeExpr.getSingle(event);
    if (bukkitAttr == null || delta == null) return;

    double deltaValue = ((Number) delta[0]).doubleValue();
    Object entity = getValueOrNull(patternIndex == 0 ? 1 : 0, Object.class, event);
    if (entity == null) return;

    if (entity instanceof WrapperLivingEntity livingFake) {
      com.github.retrooper.packetevents.protocol.attribute.Attribute peAttr = Attributes.getByName(bukkitAttr.getKey().asString());
      if (peAttr == null) return;

      double currentValue = livingFake.getAttributes().getProperties().stream()
              .filter(prop -> prop.getAttribute() == peAttr)
              .findFirst()
              .map(Property::getValue)
              .orElse(0d);

      switch (mode) {
        case SET    -> livingFake.getAttributes().setAttribute(peAttr, deltaValue);
        case ADD    -> livingFake.getAttributes().setAttribute(peAttr, currentValue + deltaValue);
        case REMOVE -> livingFake.getAttributes().setAttribute(peAttr, currentValue - deltaValue);
      }

    } else if (entity instanceof LivingEntity livingEntity) {
      AttributeInstance instance = livingEntity.getAttribute(bukkitAttr);
      if (instance == null) return;

      switch (mode) {
        case SET    -> instance.setBaseValue(deltaValue);
        case ADD    -> instance.setBaseValue(instance.getBaseValue() + deltaValue);
        case REMOVE -> instance.setBaseValue(instance.getBaseValue() - deltaValue);
      }
    }
  }

  @Override
  public String toString(@Nullable Event event, boolean debug) {
    return "fake attribute of entity";
  }
}