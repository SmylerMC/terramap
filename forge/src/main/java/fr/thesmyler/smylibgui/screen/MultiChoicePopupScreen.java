package fr.thesmyler.smylibgui.screen;

import java.util.Map;

import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.ScrollableWidgetContainer;
import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.text.ImmutableText.ofTranslation;

/**
 * A {@link PopupScreen} that provides the user with multiple choices, in the form of buttons to click.
 *
 * @author SmylerMC
 */
public class MultiChoicePopupScreen extends PopupScreen {

    public MultiChoicePopupScreen(String titleLangKey, Map<String, Runnable> options) {
        super(300f, 200f);
        WidgetContainer content = this.getContent();
        TextWidget titleWidget = new TextWidget(150f, 7f, 1, ofTranslation(titleLangKey), TextAlignment.CENTER, content.getFont());
        content.addWidget(titleWidget);
        FlexibleWidgetContainer container = new FlexibleWidgetContainer(0f, 0f, 1, 275f, 10f);
        float y = 5f;
        for(String optionLangKey: options.keySet()) {
            Runnable run = options.get(optionLangKey);
            container.addWidget(new TextButtonWidget(45f , y, 0, 200f, getGameClient().translator().format(optionLangKey), () -> {
                this.close();
                run.run();
            }));
            y += 20;
        }
        container.setHeight(y);
        float ty = titleWidget.getY() + titleWidget.getHeight() + 10;
        content.addWidget(new ScrollableWidgetContainer(5f, ty, 0, 290f, 200f - ty - 15f, container));
    }

}
