package fr.thesmyler.terramap.maps;

import net.minecraft.util.text.ITextComponent;

public interface ICopyrightHolder {

    /**
     * Gets a copyright notice for this map, translated in the appropriate language,
     * or English if it isn't available.
     * 
     * @param localeKey - the language key to get the copyright for
     * @return a copyright as a {@link ITextComponent}, translated to the appropriate language.
     */
    public ITextComponent getCopyright(String localeKey);

}
