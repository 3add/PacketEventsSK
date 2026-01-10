package threeadd.packetEventsSK.element.entity.api.skin;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;

public class Skin {
    public static final String TEXTURE_KEY = "textures";

    private final List<TextureProperty> properties;

    public Skin(final String value, final String signature) {
        this(List.of(new TextureProperty(TEXTURE_KEY, value, signature)));
    }

    public Skin(final List<TextureProperty> properties) {
        Preconditions.checkArgument(!properties.isEmpty());
        Preconditions.checkArgument(properties.getFirst().getName().equals(TEXTURE_KEY));
        this.properties = properties;
    }

    @Override
    public boolean equals(final Object object) {

        if (object == this) return true;
        else if (object instanceof Skin other) {

            final boolean sign = Objects.equals(this.properties.getFirst().getSignature(), other.properties.getFirst().getSignature());
            final boolean value = Objects.equals(this.properties.getFirst().getValue(), other.properties.getFirst().getValue());

            return sign && value;
        }

        return false;

    }

    public List<TextureProperty> getProperties() {
        return properties;
    }
}