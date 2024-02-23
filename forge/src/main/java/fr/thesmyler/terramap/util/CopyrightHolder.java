package fr.thesmyler.terramap.util;

import net.smyler.smylib.text.Text;

public interface CopyrightHolder {

    /**
     * Gets a copyright notice for this map, translated in the appropriate language,
     * or English if it isn't available.
     * 
     * @param localeKey - the language key to get the copyright for
     * @return a copyright as a {@link Text}, translated to the appropriate language.
     */
    Text getCopyright(String localeKey);

}
