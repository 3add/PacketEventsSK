package dev.threeadd.packeteventssk.api.util.registry.element;

import com.github.shanebeee.skr.Registration;

public interface SkriptElementRegistration {
    String identifier();

    void load(Registration registration);
}