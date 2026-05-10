package dev.threeadd.packeteventssk.api.entity;

import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;

public record Skin(List<TextureProperty> properties) {
    public static final String TEXTURE_KEY = "textures";

    public Skin(final String value, final String signature) {
        this(List.of(new TextureProperty(TEXTURE_KEY, value, signature)));
    }

    public Skin {
        Preconditions.checkArgument(!properties.isEmpty());
        Preconditions.checkArgument(properties.getFirst().getName().equals(TEXTURE_KEY));
    }

    @Override
    public boolean equals(final Object object) {

        if (object == this) return true;
        else if (object instanceof Skin(List<TextureProperty> properties1)) {

            final boolean sign = Objects.equals(this.properties.getFirst().getSignature(), properties1.getFirst().getSignature());
            final boolean value = Objects.equals(this.properties.getFirst().getValue(), properties1.getFirst().getValue());

            return sign && value;
        }

        return false;

    }
}