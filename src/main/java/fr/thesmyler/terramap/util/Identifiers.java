package fr.thesmyler.terramap.util;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Class with helper methods to deal with identifiers of different forms
 * 
 * @author SmylerMC
 *
 */
public final class Identifiers {

    private Identifiers() {}
    
    public static UUID stringIdToUUID(String id) {
        byte[] strBytes = id.getBytes(Charset.forName("UTF8"));
        return UUID.nameUUIDFromBytes(strBytes);
    }
    
}
