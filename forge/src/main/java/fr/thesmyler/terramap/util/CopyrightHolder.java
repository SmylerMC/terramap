package fr.thesmyler.terramap.util;

import net.minecraft.util.text.ITextComponent;

public interface CopyrightHolder {

    /**
     * Gets a copyright notice for this map, translated in the appropriate language,
     * or English if it isn't available.
     * 
     * @param localeKey - the language key to get the copyright for
     * @return a copyright as a {@link ITextComponent}, translated to the appropriate language.
     */
    ITextComponent getCopyright(String localeKey);

}
