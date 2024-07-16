package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import static net.smyler.smylib.game.Key.*;
import static org.lwjgl.glfw.GLFW.*;

public class GlfwKeyboard implements Keyboard {

    private final Minecraft client;

    public GlfwKeyboard(Minecraft client) {
        this.client = client;
    }

    @Override
    public boolean isKeyPressed(Key key) {
        return GLFW.glfwGetKey(this.client.getWindow().getWindow(), lookupKey(key)) == GLFW.GLFW_PRESS;
    }

    @Override
    public void setRepeatEvents(boolean repeat) {
        //TODO implement ore remove keyboard setRepeatEvents
    }

    @Override
    public boolean isRepeatingEvents() {
        return true;
    }

    public static Key lookupKeyCode(int keyCode) {
        return switch (keyCode) {
            case GLFW_KEY_0 -> KEY_0;
            case GLFW_KEY_1 -> KEY_1;
            case GLFW_KEY_2 -> KEY_2;
            case GLFW_KEY_3 -> KEY_3;
            case GLFW_KEY_4 -> KEY_4;
            case GLFW_KEY_5 -> KEY_5;
            case GLFW_KEY_6 -> KEY_6;
            case GLFW_KEY_7 -> KEY_7;
            case GLFW_KEY_8 -> KEY_8;
            case GLFW_KEY_9 -> KEY_9;
            case GLFW_KEY_A -> KEY_A;
            case GLFW_KEY_B -> KEY_B;
            case GLFW_KEY_C -> KEY_C;
            case GLFW_KEY_D -> KEY_D;
            case GLFW_KEY_E -> KEY_E;
            case GLFW_KEY_F -> KEY_F;
            case GLFW_KEY_G -> KEY_G;
            case GLFW_KEY_H -> KEY_H;
            case GLFW_KEY_I -> KEY_I;
            case GLFW_KEY_J -> KEY_J;
            case GLFW_KEY_K -> KEY_K;
            case GLFW_KEY_L -> KEY_L;
            case GLFW_KEY_M -> KEY_M;
            case GLFW_KEY_N -> KEY_N;
            case GLFW_KEY_O -> KEY_O;
            case GLFW_KEY_P -> KEY_P;
            case GLFW_KEY_Q -> KEY_Q;
            case GLFW_KEY_R -> KEY_R;
            case GLFW_KEY_S -> KEY_S;
            case GLFW_KEY_T -> KEY_T;
            case GLFW_KEY_U -> KEY_U;
            case GLFW_KEY_V -> KEY_V;
            case GLFW_KEY_W -> KEY_W;
            case GLFW_KEY_X -> KEY_X;
            case GLFW_KEY_Y -> KEY_Y;
            case GLFW_KEY_Z -> KEY_Z;
            case GLFW_KEY_F1 -> KEY_F1;
            case GLFW_KEY_F2 -> KEY_F2;
            case GLFW_KEY_F3 -> KEY_F3;
            case GLFW_KEY_F4 -> KEY_F4;
            case GLFW_KEY_F5 -> KEY_F5;
            case GLFW_KEY_F6 -> KEY_F6;
            case GLFW_KEY_F7 -> KEY_F7;
            case GLFW_KEY_F8 -> KEY_F8;
            case GLFW_KEY_F9 -> KEY_F9;
            case GLFW_KEY_F10 -> KEY_F10;
            case GLFW_KEY_F11 -> KEY_F11;
            case GLFW_KEY_F12 -> KEY_F12;
            case GLFW_KEY_F13 -> KEY_F13;
            case GLFW_KEY_F14 -> KEY_F14;
            case GLFW_KEY_F15 -> KEY_F15;
            case GLFW_KEY_F16 -> KEY_F16;
            case GLFW_KEY_F17 -> KEY_F17;
            case GLFW_KEY_F18 -> KEY_F18;
            case GLFW_KEY_F19 -> KEY_F19;
            case GLFW_KEY_NUM_LOCK -> KEY_NUMLOCK;
            case GLFW_KEY_KP_0 -> KEY_NUMPAD0;
            case GLFW_KEY_KP_1 -> KEY_NUMPAD1;
            case GLFW_KEY_KP_2 -> KEY_NUMPAD2;
            case GLFW_KEY_KP_3 -> KEY_NUMPAD3;
            case GLFW_KEY_KP_4 -> KEY_NUMPAD4;
            case GLFW_KEY_KP_5 -> KEY_NUMPAD5;
            case GLFW_KEY_KP_6 -> KEY_NUMPAD6;
            case GLFW_KEY_KP_7 -> KEY_NUMPAD7;
            case GLFW_KEY_KP_8 -> KEY_NUMPAD8;
            case GLFW_KEY_KP_9 -> KEY_NUMPAD9;
            case GLFW_KEY_KP_DECIMAL -> KEY_NUMPADCOMMA;
            case GLFW_KEY_KP_ENTER -> KEY_NUMPADENTER;
            case GLFW_KEY_KP_EQUAL -> KEY_NUMPADEQUALS;
            case GLFW_KEY_RIGHT -> KEY_RIGHT;
            case GLFW_KEY_LEFT -> KEY_LEFT;
            case GLFW_KEY_DOWN -> KEY_DOWN;
            case GLFW_KEY_UP -> KEY_UP;
            case GLFW_KEY_KP_ADD -> KEY_ADD;
            case GLFW_KEY_APOSTROPHE -> KEY_APOSTROPHE;
            case GLFW_KEY_BACKSLASH -> KEY_BACKSLASH;
            case GLFW_KEY_COMMA -> KEY_COMMA;
            case GLFW_KEY_EQUAL -> KEY_EQUALS;
            case GLFW_KEY_GRAVE_ACCENT -> KEY_GRAVE;
            case GLFW_KEY_LEFT_BRACKET -> KEY_LBRACKET;
            case GLFW_KEY_MINUS -> KEY_MINUS;
            case GLFW_KEY_KP_MULTIPLY -> KEY_MULTIPLY;
            case GLFW_KEY_PERIOD -> KEY_PERIOD;
            case GLFW_KEY_RIGHT_BRACKET -> KEY_RBRACKET;
            case GLFW_KEY_SEMICOLON -> KEY_SEMICOLON;
            case GLFW_KEY_SLASH -> KEY_SLASH;
            case GLFW_KEY_SPACE -> KEY_SPACE;
            case GLFW_KEY_TAB -> KEY_TAB;
            case GLFW_KEY_LEFT_CONTROL -> KEY_LCONTROL;
            case GLFW_KEY_LEFT_SHIFT -> KEY_LSHIFT;
            case GLFW_KEY_LEFT_SUPER -> KEY_LMENU;
            case GLFW_KEY_RIGHT_CONTROL -> KEY_RCONTROL;
            case GLFW_KEY_RIGHT_SHIFT -> KEY_RSHIFT;
            case GLFW_KEY_RIGHT_SUPER -> KEY_RMETA;
            case GLFW_KEY_ENTER -> KEY_RETURN;
            case GLFW_KEY_ESCAPE -> KEY_ESCAPE;
            case GLFW_KEY_BACKSPACE -> KEY_BACK;
            case GLFW_KEY_DELETE -> KEY_DELETE;
            case GLFW_KEY_END -> KEY_END;
            case GLFW_KEY_HOME -> KEY_HOME;
            case GLFW_KEY_INSERT -> KEY_INSERT;
            case GLFW_KEY_PAUSE -> KEY_PAUSE;
            case GLFW_KEY_SCROLL_LOCK -> KEY_SCROLL;
            // LALT
            // RALT
            // PAGEUP
            // PAGEDOWN
            // CAPSLOCK
            // SCROLLLOCK
            // PRINTSCREEN
            default -> null;
        };
    }

    public static int lookupKey(Key keyCode) {
        return switch (keyCode) {
            case KEY_0 -> GLFW_KEY_0;
            case KEY_1 -> GLFW_KEY_1;
            case KEY_2 -> GLFW_KEY_2;
            case KEY_3 -> GLFW_KEY_3;
            case KEY_4 -> GLFW_KEY_4;
            case KEY_5 -> GLFW_KEY_5;
            case KEY_6 -> GLFW_KEY_6;
            case KEY_7 -> GLFW_KEY_7;
            case KEY_8 -> GLFW_KEY_8;
            case KEY_9 -> GLFW_KEY_9;
            case KEY_A -> GLFW_KEY_A;
            case KEY_B -> GLFW_KEY_B;
            case KEY_C -> GLFW_KEY_C;
            case KEY_D -> GLFW_KEY_D;
            case KEY_E -> GLFW_KEY_E;
            case KEY_F -> GLFW_KEY_F;
            case KEY_G -> GLFW_KEY_G;
            case KEY_H -> GLFW_KEY_H;
            case KEY_I -> GLFW_KEY_I;
            case KEY_J -> GLFW_KEY_J;
            case KEY_K -> GLFW_KEY_K;
            case KEY_L -> GLFW_KEY_L;
            case KEY_M -> GLFW_KEY_M;
            case KEY_N -> GLFW_KEY_N;
            case KEY_O -> GLFW_KEY_O;
            case KEY_P -> GLFW_KEY_P;
            case KEY_Q -> GLFW_KEY_Q;
            case KEY_R -> GLFW_KEY_R;
            case KEY_S -> GLFW_KEY_S;
            case KEY_T -> GLFW_KEY_T;
            case KEY_U -> GLFW_KEY_U;
            case KEY_V -> GLFW_KEY_V;
            case KEY_W -> GLFW_KEY_W;
            case KEY_X -> GLFW_KEY_X;
            case KEY_Y -> GLFW_KEY_Y;
            case KEY_Z -> GLFW_KEY_Z;
            case KEY_F1 -> GLFW_KEY_F1;
            case KEY_F2 -> GLFW_KEY_F2;
            case KEY_F3 -> GLFW_KEY_F3;
            case KEY_F4 -> GLFW_KEY_F4;
            case KEY_F5 -> GLFW_KEY_F5;
            case KEY_F6 -> GLFW_KEY_F6;
            case KEY_F7 -> GLFW_KEY_F7;
            case KEY_F8 -> GLFW_KEY_F8;
            case KEY_F9 -> GLFW_KEY_F9;
            case KEY_F10 -> GLFW_KEY_F10;
            case KEY_F11 -> GLFW_KEY_F11;
            case KEY_F12 -> GLFW_KEY_F12;
            case KEY_F13 -> GLFW_KEY_F13;
            case KEY_F14 -> GLFW_KEY_F14;
            case KEY_F15 -> GLFW_KEY_F15;
            case KEY_F16 -> GLFW_KEY_F16;
            case KEY_F17 -> GLFW_KEY_F17;
            case KEY_F18 -> GLFW_KEY_F18;
            case KEY_F19 -> GLFW_KEY_F19;
            case KEY_NUMLOCK -> GLFW_KEY_NUM_LOCK;
            case KEY_NUMPAD0 -> GLFW_KEY_KP_0;
            case KEY_NUMPAD1 -> GLFW_KEY_KP_1;
            case KEY_NUMPAD2 -> GLFW_KEY_KP_2;
            case KEY_NUMPAD3 -> GLFW_KEY_KP_3;
            case KEY_NUMPAD4 -> GLFW_KEY_KP_4;
            case KEY_NUMPAD5 -> GLFW_KEY_KP_5;
            case KEY_NUMPAD6 -> GLFW_KEY_KP_6;
            case KEY_NUMPAD7 -> GLFW_KEY_KP_7;
            case KEY_NUMPAD8 -> GLFW_KEY_KP_8;
            case KEY_NUMPAD9 -> GLFW_KEY_KP_9;
            case KEY_NUMPADCOMMA -> GLFW_KEY_KP_DECIMAL;
            case KEY_NUMPADENTER -> GLFW_KEY_KP_ENTER;
            case KEY_NUMPADEQUALS -> GLFW_KEY_KP_EQUAL;
            case KEY_RIGHT -> GLFW_KEY_RIGHT;
            case KEY_LEFT -> GLFW_KEY_LEFT;
            case KEY_DOWN -> GLFW_KEY_DOWN;
            case KEY_UP -> GLFW_KEY_UP;
            case KEY_ADD -> GLFW_KEY_KP_ADD;
            case KEY_APOSTROPHE -> GLFW_KEY_APOSTROPHE;
            case KEY_BACKSLASH -> GLFW_KEY_BACKSLASH;
            case KEY_COMMA -> GLFW_KEY_COMMA;
            case KEY_EQUALS -> GLFW_KEY_EQUAL;
            case KEY_GRAVE -> GLFW_KEY_GRAVE_ACCENT;
            case KEY_LBRACKET -> GLFW_KEY_LEFT_BRACKET;
            case KEY_MINUS -> GLFW_KEY_MINUS;
            case KEY_MULTIPLY -> GLFW_KEY_KP_MULTIPLY;
            case KEY_PERIOD -> GLFW_KEY_PERIOD;
            case KEY_RBRACKET -> GLFW_KEY_RIGHT_BRACKET;
            case KEY_SEMICOLON -> GLFW_KEY_SEMICOLON;
            case KEY_SLASH -> GLFW_KEY_SLASH;
            case KEY_SPACE -> GLFW_KEY_SPACE;
            case KEY_TAB -> GLFW_KEY_TAB;
            case KEY_LCONTROL -> GLFW_KEY_LEFT_CONTROL;
            case KEY_LSHIFT -> GLFW_KEY_LEFT_SHIFT;
            case KEY_LMENU -> GLFW_KEY_LEFT_SUPER;
            case KEY_RCONTROL -> GLFW_KEY_RIGHT_CONTROL;
            case KEY_RSHIFT -> GLFW_KEY_RIGHT_SHIFT;
            case KEY_RMETA -> GLFW_KEY_RIGHT_SUPER;
            case KEY_RETURN -> GLFW_KEY_ENTER;
            case KEY_BACK -> GLFW_KEY_BACKSPACE;
            case KEY_ESCAPE -> GLFW_KEY_ESCAPE;
            case KEY_DELETE -> GLFW_KEY_DELETE;
            case KEY_END -> GLFW_KEY_END;
            case KEY_HOME -> GLFW_KEY_HOME;
            case KEY_INSERT -> GLFW_KEY_INSERT;
            case KEY_PAUSE -> GLFW_KEY_PAUSE;
            case KEY_SCROLL -> GLFW_KEY_SCROLL_LOCK;
            // LALT
            // RALT
            // PAGEUP
            // PAGEDOWN
            // CAPSLOCK
            // SCROLLLOCK
            // PRINTSCREEN
            default -> 0;
        };
    }
}
