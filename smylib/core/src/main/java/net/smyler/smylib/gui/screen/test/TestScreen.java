package net.smyler.smylib.gui.screen.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.gui.Font;
import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.ScrollableWidgetContainer;
import net.smyler.smylib.gui.containers.TabbedContainer;
import net.smyler.smylib.gui.screen.BackgroundOption;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.widgets.SpriteWidget;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.TextButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;
import net.smyler.smylib.gui.widgets.sliders.FloatSliderWidget;
import net.smyler.smylib.gui.widgets.sliders.IntegerSliderWidget;
import net.smyler.smylib.gui.widgets.text.TextAlignment;
import net.smyler.smylib.gui.widgets.text.TextFieldWidget;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import net.smyler.smylib.json.TextJsonAdapter;
import net.smyler.smylib.text.Formatting;
import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import net.smyler.smylib.text.TextStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.util.Comparator.comparing;
import static net.smyler.smylib.Color.RED;
import static net.smyler.smylib.Color.WHITE;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.text.ImmutableText.ofPlainText;

public class TestScreen extends Screen {

    private final Screen parent;

    private final SpriteButtonWidget closeButton;
    private final SpriteButtonWidget resetButton;
    private final TabbedContainer tabs;
    private final TabbedContainer.TabContainer infoTab;
    private final TabbedContainer.TabContainer spritesTab;
    private final TabbedContainer.TabContainer widgetTab;
    private final TabbedContainer.TabContainer jsonTextScreen;
    private final FlexibleWidgetContainer spritesContainer = new FlexibleWidgetContainer(0f, 0f, 0, 10f, 10f);

    private final List<InfoText> infoTexts = new ArrayList<>();
    private final Gson textJsonParser = new GsonBuilder().registerTypeAdapter(Text.class, new TextJsonAdapter()).create();

    final static float PADDING = 10f;

    public TestScreen(Screen parent) {
        super(BackgroundOption.DEFAULT);
        this.parent = parent;
        this.closeButton = new SpriteButtonWidget(0, SpriteButtonWidget.ButtonSprites.CROSS, this::closeScreen);
        this.resetButton = new SpriteButtonWidget(0, SpriteButtonWidget.ButtonSprites.PAPER, this::reset);
        this.tabs = new TabbedContainer(0f, 30f, 0, 10, 10);
        this.infoTab = this.tabs.createTab(ofPlainText("About"));
        this.spritesTab = this.tabs.createTab(ofPlainText("Sprites"));
        this.widgetTab = this.tabs.createTab(ofPlainText("Widgets"));
        this.jsonTextScreen = this.tabs.createTab(ofPlainText("JSON texts"));

        this.infoTexts.add(new InfoText("Game version", () -> getGameClient().gameVersion()));
        this.infoTexts.add(new InfoText("Loader", () -> getGameClient().modLoader()));
        this.infoTexts.add(new InfoText("Current FPS", () -> "" + getGameClient().currentFPS()));
        this.infoTexts.add(new InfoText("Game directory", () -> "" + getGameClient().gameDirectory().toAbsolutePath()));
        this.infoTexts.add(new InfoText("Language", () -> getGameClient().translator().language()));
        this.infoTexts.add(new InfoText("Window size", () -> getGameClient().windowWidth() + "x" + getGameClient().windowHeight()));
        this.infoTexts.add(new InfoText("Native window size", () -> getGameClient().nativeWindowWidth() + "x" + getGameClient().nativeWindowHeight()));
        this.infoTexts.add(new InfoText("Scale factor", () -> "" + getGameClient().scaleFactor()));
        this.infoTexts.add(new InfoText("isMac", () -> "" + getGameClient().isMac()));
        this.infoTexts.add(new InfoText("SmyLib debug mode", () -> "" + SmyLib.isDebug()));

    }

    @Override
    public void init() {
        super.init();

        Font font = this.getFont();

        this.removeAllWidgets();
        this.cancelAllScheduled();

        this.addWidget(new TextWidget(this.getWidth() / 2f, PADDING, 0, ofPlainText("SmyLib demo screen"), TextAlignment.CENTER, font));
        this.closeButton.setX(this.getWidth() - PADDING - this.closeButton.getWidth()).setY(PADDING);
        this.addWidget(this.closeButton);
        this.resetButton.setX(this.closeButton.getX() - PADDING - this.resetButton.getWidth()).setY(PADDING);
        this.addWidget(this.resetButton);

        this.infoTab.removeAllWidgets();
        this.infoTab.cancelAllScheduled();
        this.spritesTab.removeAllWidgets();
        this.spritesContainer.removeAllWidgets();
        this.widgetTab.removeAllWidgets();
        this.jsonTextScreen.removeAllWidgets();
        this.jsonTextScreen.cancelAllScheduled();

        this.addWidget(this.tabs);
        this.tabs.setX(PADDING);
        this.tabs.setWidth(this.getWidth() - 2 * PADDING);
        this.tabs.setHeight(this.getHeight() - PADDING - 30f);

        // About tab
        float infoTextY = PADDING;
        for (InfoText infoText: this.infoTexts) {
            infoText.widget.setAnchorY(infoTextY);
            infoTextY += infoText.widget.getHeight() + PADDING;
            this.infoTab.addWidget(infoText.widget);
        }
        this.infoTab.scheduleBeforeEachUpdate(this::updateTexts);

        // Sprites tab
        float y = 0f;
        List<Map.Entry<Identifier, Sprite>> sprites = getGameClient().sprites().getSprites()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(comparing(Identifier::toString)))
                .collect(Collectors.toList());
        for (Map.Entry<Identifier, Sprite> entry: sprites) {
            Text id = ImmutableText.ofPlainText(entry.getKey().toString());
            Sprite sprite = entry.getValue();
            float entryHeight = round(max(font.height(), sprite.height())) + 10f;
            this.spritesContainer.addWidget(new TextWidget(
                    PADDING, y + (entryHeight - font.height()) / 2, 0,
                    id,
                    font
            ));
            SpriteWidget spriteWidget = new SpriteWidget(
                    font.computeWidth(id) + 2*PADDING, (float) (y + (entryHeight - sprite.height()) / 2), 0,
                    sprite
            );
            this.spritesContainer.addWidget(spriteWidget);
            this.spritesContainer.setWidth(max(this.spritesContainer.getWidth(), spriteWidget.getX() + spriteWidget.getWidth() + 10f));
            y += entryHeight;
        }
        this.spritesContainer.setHeight(y + 10f);
        this.spritesTab.addWidget(new ScrollableWidgetContainer(0f, 0f, 0, this.spritesTab.getWidth(), this.spritesTab.getHeight(), this.spritesContainer));

        // Widgets
        this.widgetTab.addWidget(new TextWidget(PADDING, 10f, 0, ImmutableText.ofPlainText("Text widget"),  this.getFont()));
        this.widgetTab.addWidget(new TextWidget(
                this.widgetTab.getWidth() / 2f, 10f, 0,
                ImmutableText.ofPlainText("Centered colored text widget").withStyle(new TextStyle(Formatting.BLUE.color())),
                TextAlignment.CENTER,
                this.getFont()
        ));
        this.widgetTab.addWidget(new TextWidget(
                this.widgetTab.getWidth() - PADDING, 10f, 0,
                ImmutableText.ofPlainText("Small text").withStyle(new TextStyle(Formatting.RED.color())),
                TextAlignment.LEFT,
                getGameClient().smallestFont()
        ));
        TextButtonWidget textButton = new TextButtonWidget(PADDING, 30f, 0, 150f, "Click me");
        textButton.setOnClick(() -> textButton.setText("Double-click me now"));
        textButton.setOnDoubleClick(textButton::disable);
        textButton.enable();
        this.widgetTab.addWidget(textButton);
        this.widgetTab.addWidget(new SpriteButtonWidget(180f, 30f, 0, SpriteButtonWidget.ButtonSprites.BURGER_20).enable());
        this.widgetTab.addWidget(new ToggleButtonWidget(220f, 32f, 0, true).enable());
        this.widgetTab.addWidget(new IntegerSliderWidget(PADDING, 60f, 0, 150f, 20f, 0, 100, 50));
        this.widgetTab.addWidget(new FloatSliderWidget(180f, 60f, 0, 150f, 10f, 0, 100, 50f));
        this.widgetTab.addWidget(new TextFieldWidget(PADDING, 90f, 0, 150f, this.getFont()).setText("TextFieldWidget"));

        // JSON text
        final TextFieldWidget inputField = new TextFieldWidget(PADDING, PADDING, 0, jsonTextScreen.getWidth() - 2*PADDING, getGameClient().defaultFont());
        final TextWidget text = new TextWidget(
                this.jsonTextScreen.getWidth() / 2,
                (this.jsonTextScreen.getHeight() - inputField.getHeight()) / 2,
                0,
                ImmutableText.EMPTY,
                TextAlignment.CENTER, getGameClient().defaultFont()
        );
        this.jsonTextScreen.addWidget(inputField);
        this.jsonTextScreen.addWidget(text);
        this.jsonTextScreen.scheduleBeforeEachUpdate(() -> {
            try {
                Text component = this.textJsonParser.fromJson(inputField.getText(), Text.class);
                if (component == null) {
                    throw new JsonParseException("");
                }
                text.setText(component);
                text.setAnchorY((this.jsonTextScreen.getHeight() - inputField.getHeight() - text.getHeight()) / 2 + inputField.getHeight());
                inputField.setFocusedTextColor(WHITE);
            } catch (JsonParseException e) {
                inputField.setFocusedTextColor(RED);
            }
        });
    }

    public void closeScreen() {
        getGameClient().displayScreen(this.parent);
    }

    public void reset() {
        getGameClient().displayScreen(new TestScreen(this.parent));
    }

    private void updateTexts() {
        for (InfoText infoText: this.infoTexts) {
            ImmutableText text = ofPlainText(infoText.name);
            text = text.withNewSiblings(ofPlainText(": "));
            ImmutableText valueText = ofPlainText(infoText.value.get());
            valueText = valueText.withStyle(new TextStyle(Formatting.GRAY.color()));
            text = text.withNewSiblings(valueText);
            infoText.widget.setText(text);
        }
    }

    private static class InfoText {
        final String name;
        final Supplier<String> value;
        final TextWidget widget = new TextWidget(PADDING, 0f, 0, getGameClient().defaultFont());
        public InfoText(String name, Supplier<String> value) {
            this.name = name;
            this.value = value;
        }
    }

}
