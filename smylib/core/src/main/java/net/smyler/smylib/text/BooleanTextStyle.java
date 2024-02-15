package net.smyler.smylib.text;

/**
 * A boolean parameter for a text style.
 * Styles can be inherited from the parent texts.
 *
 * @author Smyler
 */
public enum BooleanTextStyle {

    /**
     * The value has been explicitly set to true.
     */
    TRUE,

    /**
     * The value has been explicitly set to false.
     */
    FALSE,

    /**
     * The value should be inherited from the parent style.
     */
    INHERIT;

    /**
     * Applies a given value as the parent of this value.
     * If the current is either {@link BooleanTextStyle#TRUE} of {@link BooleanTextStyle#FALSE}, it will be kept.
     * If it is {@link BooleanTextStyle#INHERIT}, it will be overwritten by the parent.
     *
     * @param other the parent value
     * @return      a new value resulting of the application
     */
    public BooleanTextStyle applyParent(BooleanTextStyle other) {
        return this == INHERIT ? other: this;
    }

}
