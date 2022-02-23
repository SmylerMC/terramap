package fr.thesmyler.smylibgui.toast;

import fr.thesmyler.smylibgui.SmyLibGui;
import net.minecraft.client.gui.toasts.IToast;

public abstract class AbstractToast implements IToast {

    protected boolean justUpdated = true;
    protected long startTime;
    protected final String titleKey;
    protected final String descriptionKey;

    public AbstractToast(String titleKey, String descriptionKey) {
        this.titleKey = titleKey;
        this.descriptionKey = descriptionKey;
    }

    public String getLocalizedDescription() {
        return SmyLibGui.getTranslator().format(this.descriptionKey);
    }

    public String getLocalizedTitle() {
        return SmyLibGui.getTranslator().format(this.titleKey);
    }
}
