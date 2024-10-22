package net.smyler.smylib.resources;

import org.jetbrains.annotations.Nullable;


import static java.util.Arrays.stream;


/**
 * Villager texture metadata.
 * Gives the game indications on how to use a villager texture depending on the villager properties.
 *
 * @see <a href="https://minecraft.wiki/w/Resource_pack#Villagers">the Minecraft wiki</a> for more information.
 *
 * @author Smyler
 */
public class VillagerMetadata {

    private final HatType hat;

    public VillagerMetadata(HatType hat) {
        this.hat = hat;
    }

    public HatType hat() {
        return this.hat;
    }

    public enum HatType {

        FULL, PARTIAL, NONE;

        private final String value;

        HatType() {
            this.value = this.name().toLowerCase();
        }

        public String value() {
            return this.value;
        }

        public static @Nullable HatType fromValue(String value) {
            return stream(HatType.values())
                    .filter(h -> h.value.equals(value))
                    .findFirst()
                    .orElse(null);
        }

    }

}
