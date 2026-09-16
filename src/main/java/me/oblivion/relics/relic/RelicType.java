package me.oblivion.relics.relic;

public enum RelicType {

    RIFT(
            "Rift Relic",
            "Space and dimensional combat"
    ),

    GRAVITY(
            "Gravity Relic",
            "Control movement and gravity"
    ),

    VOID(
            "Void Relic",
            "Void teleportation and collapse"
    ),

    STORM(
            "Storm Relic",
            "Lightning and high-speed movement"
    ),

    FROST(
            "Frost Relic",
            "Ice attacks and movement control"
    ),

    INFERNO(
            "Inferno Relic",
            "Fire attacks and aggressive mobility"
    ),

    SHADOW(
            "Shadow Relic",
            "Teleportation and dark combat"
    ),

    TIME(
            "Time Relic",
            "Time manipulation and repositioning"
    ),

    PHANTOM(
            "Phantom Relic",
            "Phasing and spectral attacks"
    ),

    SOUL(
            "Soul Relic",
            "Soul control and mark attacks"
    ),

    CHAOS(
            "Chaos Relic",
            "Unpredictable force and displacement"
    ),

    CELESTIAL(
            "Celestial Relic",
            "Powerful celestial energy attacks"
    ),

    AEGIS(
            "Aegis Relic",
            "Defensive force and precision attacks"
    ),

    FORCE(
            "Force Relic",
            "Powerful pushes, launches and shockwaves"
    ),

    ARCANE(
            "Arcane Relic",
            "Magical projectiles and spatial magic"
    );

    private final String displayName;
    private final String description;

    RelicType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
