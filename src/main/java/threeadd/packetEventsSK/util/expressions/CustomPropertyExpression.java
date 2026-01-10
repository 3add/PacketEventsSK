package threeadd.packetEventsSK.util.expressions;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomPropertyExpression<Owner, Return> extends CustomExpression<Return> {

    protected final Class<Owner> inputType;

    public CustomPropertyExpression(Class<Return> returnType, Class<Owner> inputType, boolean isSingle) {
        super(returnType, isSingle);
        this.inputType = inputType;
    }

    @Override
    protected Return getOne(Event event) {
        Owner input = getValueOrNull(0, inputType, event);

        if (input == null)
            return null;

        return getProperty(input);
    }

    @Override
    protected List<Return> getMany(Event event) {
        List<Owner> inputs = getValuesOrNull(0, inputType, event);

        if (inputs == null || inputs.isEmpty())
            return null;

        List<Return> results = new ArrayList<>();

        for (Owner input : inputs) {
            if (input == null) continue;

            List<Return> properties = getProperties(input);
            if (properties != null)
                results.addAll(properties);
        }

        return results.isEmpty() ? null : results;
    }

    @Nullable
    protected Return getProperty(Owner input) {
        return null;
    }

    @Nullable
    protected List<Return> getProperties(Owner input) {
        return null;
    }
}