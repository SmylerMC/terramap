package net.smyler.smylib.gui.gl;

public enum ColorLogic {
    CLEAR,         // 0
    SET,           // 1
    COPY,          // s
    COPY_INVERTED, // ~s
    NOOP,          // d
    INVERT,        // ~d
    AND,           // s & d
    NAND,          // ~(s & d)
    OR,            // s | d
    NOR,           // ~(s | d)
    XOR,           // s ^ d
    EQUIV,         // ~(s ^ d)
    AND_REVERSE,   // s & ~d
    AND_INVERTED,  // ~s & d
    OR_REVERSE,    // s | ~d
    OR_INVERTED    // ~s | d
}
