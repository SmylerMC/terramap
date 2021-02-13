package fr.thesmyler.smylibgui.toast;

import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.resources.I18n;

public abstract class AbstractToast implements IToast {
	
	protected boolean justUpdated = true;
	protected long startTime;
	protected String titleKey;
	protected String descriptionKey;

	public AbstractToast(String titleKey, String descriptionKey) {
		this.titleKey = titleKey;
		this.descriptionKey = descriptionKey;
	}

	public String getLocalizedDescription() {
		return I18n.format(this.descriptionKey);
	}

	public String getLocalizedTitle() {
		return I18n.format(this.titleKey);
	}
}
