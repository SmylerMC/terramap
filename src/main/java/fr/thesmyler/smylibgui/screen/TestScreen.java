package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Animation;
import fr.thesmyler.smylibgui.util.Animation.AnimationState;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.buttons.OptionButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.FloatSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TestScreen extends Screen {

    private static boolean wasShown = false;

    private final GuiScreen parent;
    private final Animation animation;
    private int counter = 0;
    private final TextWidget fpsCounter;
    private final TextWidget focus;
    private TextWidget hovered;
    private TextWidget colored;
    private final TextFieldWidget textField;
    private TextButtonWidget testButton;
    private WidgetContainer[] subScreens;
    private int currentSubScreen = 0;

    private final TexturedButtonWidget previous;
    private final TexturedButtonWidget next;

    public TestScreen(GuiScreen parent) {
        super(BackgroundOption.DEFAULT);
        this.parent = parent;
        this.animation = new Animation(5000); // We will use an animation to set the color of one of the displayed strings
        this.animation.start(AnimationState.CONTINUOUS_ENTER);
        this.next = new TexturedButtonWidget(10, IncludedTexturedButtons.RIGHT, this::nextPage);
        this.previous = new TexturedButtonWidget(10, IncludedTexturedButtons.LEFT, this::previousPage);

        this.fpsCounter = new TextWidget(10, new TextComponentString("FPS: 0"), SmyLibGui.DEFAULT_FONT);
        this.focus = new TextWidget(10, new TextComponentString("Focused: null"), SmyLibGui.DEFAULT_FONT);
        this.hovered = new TextWidget(10, new TextComponentString("Hovered: null"), SmyLibGui.DEFAULT_FONT);
        this.textField = new TextFieldWidget(1, "Text field",SmyLibGui.DEFAULT_FONT);
        this.textField.setText("Write and right click");
        this.textField.setCursor(0);
    }

    @Override
    public void initGui() {
        WidgetContainer content = this.getContent();
        content.removeAllWidgets(); // Remove the widgets that were already there
        content.cancellAllScheduled(); // Cancel all callbacks that were already there

        //Main screen
        WidgetContainer textScreen = new FlexibleWidgetContainer(20, 50, 1, this.width - 40, this.height - 70);
        WidgetContainer buttonScreen = new FlexibleWidgetContainer(20, 50, 1, this.width - 40, this.height - 70);
        WidgetContainer sliderScreen = new FlexibleWidgetContainer(20, 50, 1, this.width - 40, this.height - 70);
        WidgetContainer menuScreen = new FlexibleWidgetContainer(20, 50, 1, this.width - 40, this.height - 70);
        this.subScreens = new WidgetContainer[] { textScreen, buttonScreen, sliderScreen, menuScreen};
        for(WidgetContainer container: this.subScreens) container.setDoScissor(false);

        TextWidget title = new TextWidget(this.width / 2f, 20, 10, new TextComponentString("SmyLibGui demo test screen"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        content.addWidget(title);
        content.addWidget(new TexturedButtonWidget(this.width - 20, 5, 10, IncludedTexturedButtons.CROSS, () -> Minecraft.getMinecraft().displayGuiScreen(this.parent)));
        content.addWidget(next.setX(this.width - 20).setY(this.height - 20));
        content.addWidget(previous.setX(5).setY(this.height - 20));
        content.addWidget(
                new TextButtonWidget(13, 13, 10, 100, "Reset screen",
                        () -> Minecraft.getMinecraft().displayGuiScreen(new TestScreen(this.parent)))
                );

        // === Text related stuff and general features examples === //
        this.hovered = new TextWidget(0, 50, 10, new TextComponentString("Hovered: null"), SmyLibGui.DEFAULT_FONT);

        TextWidget counterStr = new TextWidget(0, 100, 10, SmyLibGui.DEFAULT_FONT);
        this.colored = new TextWidget(0, 120, 10, new TextComponentString("Color animated text"), SmyLibGui.DEFAULT_FONT);
        this.colored.setBaseColor(animation.rainbowColor());
        textScreen.addWidget(fpsCounter.setAnchorX(0).setAnchorY(10));
        textScreen.addWidget(focus.setAnchorX(0).setAnchorY(30));
        textScreen.addWidget(hovered);
        textScreen.addWidget(this.textField.setX(0).setY(70).setWidth(150).setOnPressEnterCallback(s -> {this.textField.setText("You pressed enter :)"); return true;}));
        textScreen.addWidget(counterStr);
        textScreen.addWidget(colored);
        ITextComponent compo = ITextComponent.Serializer.jsonToComponent("[\"\",{\"text\":\"This is red, with a hover event,\",\"color\":\"dark_red\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"I said it's red\"}},{\"text\":\" \"},{\"text\":\"and this is green with an other hover event.\",\"color\":\"dark_green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Don't you trust me? This is green!\"}},{\"text\":\"\\n\"},{\"text\":\"And this is blue, with a click event!\",\"color\":\"dark_blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://example.com\"}},{\"text\":\"\\n\"},{\"text\":\"And finally, this is \",\"color\":\"white\"},{\"text\":\"black\",\"strikethrough\":true,\"color\":\"white\"},{\"text\":\" white, \",\"color\":\"white\"},{\"text\":\"with\",\"underlined\":true,\"color\":\"white\"},{\"text\":\" various\",\"italic\":true,\"color\":\"white\"},{\"text\":\" styles \",\"bold\":true,\"color\":\"white\"},{\"text\":\"and I bet you can't read that.\",\"obfuscated\":true,\"color\":\"white\"}]");
        textScreen.addWidget(new TextWidget(textScreen.getWidth()/2, 140, 1, compo, TextAlignment.CENTER, new Font(2)).setMaxWidth(textScreen.getWidth()).setBackgroundColor(Color.DARK_OVERLAY).setPadding(10));

        // === Button screen: examples on how to use button widgets === //

        this.testButton = new TextButtonWidget(0, 0, 1, 150, "Click me!",
                () -> this.testButton.setText("Nice, double click me now!"),
                () -> {
                    this.testButton.setText("I'm done now :(");
                    this.testButton.disable();
                }
                );
        buttonScreen.addWidget(testButton);
        buttonScreen.addWidget(new TexturedButtonWidget(0, 30, 1, IncludedTexturedButtons.BLANK_15, null));
        buttonScreen.addWidget(new TexturedButtonWidget(30, 30, 1, IncludedTexturedButtons.CROSS, null));
        buttonScreen.addWidget(new TexturedButtonWidget(30, 30, 1, IncludedTexturedButtons.PLUS, null));
        buttonScreen.addWidget(new TexturedButtonWidget(60, 30, 1, IncludedTexturedButtons.MINUS, null));
        buttonScreen.addWidget(new TexturedButtonWidget(90, 30, 1, IncludedTexturedButtons.LEFT, null));
        buttonScreen.addWidget(new TexturedButtonWidget(120, 30, 1, IncludedTexturedButtons.UP, null));
        buttonScreen.addWidget(new TexturedButtonWidget(150, 30, 1, IncludedTexturedButtons.DOWN, null));
        buttonScreen.addWidget(new TexturedButtonWidget(180, 30, 1, IncludedTexturedButtons.RIGHT, null));
        ToggleButtonWidget tb1 = new ToggleButtonWidget(0, 60, 1, true);
        buttonScreen.addWidget(tb1);
        buttonScreen.addWidget(new ToggleButtonWidget(30, 60, 1, true, tb1::setEnabled));
        buttonScreen.addWidget(new OptionButtonWidget<>(0, 90, 2, 150, new String[]{"Option 1", "Option 2", "Option 3", "Option 4"}));


        // === Slider screen: examples on how to use slider widgets === //

        sliderScreen.addWidget(new IntegerSliderWidget(0, 0, 1, 150, 0, 100, 50));
        sliderScreen.addWidget(new FloatSliderWidget(0, 30, 1, 150, 0, 1, 0.5));
        sliderScreen.addWidget(new OptionSliderWidget<>(0, 60, 1, 150, new String[]{"Option 1", "Option 2", "Option 3", "Option 4"}));
        sliderScreen.addWidget(new IntegerSliderWidget(0, 90, 1, 150, 30, 0, 100, 50));
        sliderScreen.addWidget(new IntegerSliderWidget(0, 140, 1, 150, 10, 0, 100, 50));


        // === Menu screen: example on how to use menu widgets === //

        MenuWidget rcm = new MenuWidget(50, SmyLibGui.DEFAULT_FONT); //This will be used as our right click menu, the following are it's sub menus
        MenuWidget animationMenu = new MenuWidget(1, SmyLibGui.DEFAULT_FONT);
        MenuWidget here = new MenuWidget(50, SmyLibGui.DEFAULT_FONT);
        MenuWidget is = new MenuWidget(50, SmyLibGui.DEFAULT_FONT);
        MenuWidget a = new MenuWidget(50, SmyLibGui.DEFAULT_FONT);
        MenuWidget very = new MenuWidget(50, SmyLibGui.DEFAULT_FONT);
        MenuWidget nested = new MenuWidget(50, SmyLibGui.DEFAULT_FONT);
        animationMenu.addEntry("Show", () -> animation.start(AnimationState.ENTER));
        animationMenu.addEntry("Hide", () -> animation.start(AnimationState.LEAVE));
        animationMenu.addEntry("Flash", () -> animation.start(AnimationState.FLASH));
        animationMenu.addEntry("Continuous", () -> animation.start(AnimationState.CONTINUOUS_ENTER));
        animationMenu.addEntry("Continuous backward", () -> animation.start(AnimationState.CONTINUOUS_LEAVE));
        animationMenu.addEntry("Back and forth", () -> animation.start(AnimationState.BACK_AND_FORTH));
        animationMenu.addEntry("Stop", () -> animation.start(AnimationState.STOPPED));
        rcm.addEntry("Close", () -> Minecraft.getMinecraft().displayGuiScreen(this.parent));
        rcm.addEntry("Disabled Entry");
        rcm.addEntry("Here", here);
        here.addEntry("is", is);
        is.addEntry("a", a);
        a.addEntry("very", very);
        very.addEntry("nested", nested);
        nested.addEntry("menu");
        rcm.addSeparator();
        rcm.addEntry("Animation", animationMenu);
        rcm.useAsRightClick(); // Calling this tells the menu to open whenever it's parent screen is right clicked
        menuScreen.addWidget(new TextWidget(menuScreen.getWidth() / 2, menuScreen.getHeight() / 2, 1, new TextComponentString("Please right click anywhere"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT));
        menuScreen.addWidget(rcm);


        // ==== Getting everything ready and setting up scheduled tasks === //

        content.addWidget(subScreens[this.currentSubScreen]); // A screen is also a widget, that allows for a lot of flexibility

        // Same as Javascript's setInterval
        content.scheduleAtIntervalBeforeUpdate(() -> counterStr.setText(new TextComponentString("Scheduled callback called " + this.counter++)), 1000);
        content.scheduleBeforeEachUpdate(() -> { // Called at every update
            this.animation.update();
            this.fpsCounter.setText(new TextComponentString("FPS: " + Minecraft.getDebugFPS()));
            this.focus.setText(new TextComponentString("Focused: " + content.getFocusedWidget()));
            this.hovered.setText(new TextComponentString("Hovered: " + content.getHoveredWidget()));
            this.colored.setBaseColor(animation.rainbowColor());
        });
        this.updateButtons();
    }

    private void nextPage() {
        this.getContent().removeWidget(this.subScreens[this.currentSubScreen]);
        this.currentSubScreen++;
        this.getContent().addWidget(this.subScreens[this.currentSubScreen]);
        this.updateButtons();
    }

    private void previousPage() {
        this.getContent().removeWidget(this.subScreens[this.currentSubScreen]);
        this.currentSubScreen--;
        this.getContent().addWidget(this.subScreens[this.currentSubScreen]);
        this.updateButtons();
    }

    private void updateButtons() {
        if(this.currentSubScreen <= 0) this.previous.disable();
        else this.previous.enable();
        if(this.currentSubScreen >= this.subScreens.length - 1) this.next.disable();
        else this.next.enable();
    }

    @SubscribeEvent
    public static void onGuiScreenInit(GuiScreenEvent.InitGuiEvent event) {
        if(!wasShown && !(event.getGui() instanceof Screen)) {
            Minecraft.getMinecraft().displayGuiScreen(new TestScreen(event.getGui()));
            wasShown = true;
        }
    }

}
