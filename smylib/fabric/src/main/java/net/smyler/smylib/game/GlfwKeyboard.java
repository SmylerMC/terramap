package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import static net.smyler.smylib.game.Key.*;

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
            case 48 -> KEY_0;
            case 49 -> KEY_1;
            case 50 -> KEY_2;
            case 51 -> KEY_3;
            case 52 -> KEY_4;
            case 53 -> KEY_5;
            case 54 -> KEY_6;
            case 55 -> KEY_7;
            case 56 -> KEY_8;
            case 57 -> KEY_9;
            case 65 -> KEY_A;
            case 66 -> KEY_B;
            case 67 -> KEY_C;
            case 68 -> KEY_D;
            case 69 -> KEY_E;
            case 70 -> KEY_F;
            case 71 -> KEY_G;
            case 72 -> KEY_H;
            case 73 -> KEY_I;
            case 74 -> KEY_J;
            case 75 -> KEY_K;
            case 76 -> KEY_L;
            case 77 -> KEY_M;
            case 78 -> KEY_N;
            case 79 -> KEY_O;
            case 80 -> KEY_P;
            case 81 -> KEY_Q;
            case 82 -> KEY_R;
            case 83 -> KEY_S;
            case 84 -> KEY_T;
            case 85 -> KEY_U;
            case 86 -> KEY_V;
            case 87 -> KEY_W;
            case 88 -> KEY_X;
            case 89 -> KEY_Y;
            case 90 -> KEY_Z;
            case 290 -> KEY_F1;
            case 291 -> KEY_F2;
            case 292 -> KEY_F3;
            case 293 -> KEY_F4;
            case 294 -> KEY_F5;
            case 295 -> KEY_F6;
            case 296 -> KEY_F7;
            case 297 -> KEY_F8;
            case 298 -> KEY_F9;
            case 299 -> KEY_F10;
            case 300 -> KEY_F11;
            case 301 -> KEY_F12;
            case 302 -> KEY_F13;
            case 303 -> KEY_F14;
            case 304 -> KEY_F15;
            case 305 -> KEY_F16;
            case 306 -> KEY_F17;
            case 307 -> KEY_F18;
            case 308 -> KEY_F19;
            case 282 -> KEY_NUMLOCK;
            case 320 -> KEY_NUMPAD0;
            case 321 -> KEY_NUMPAD1;
            case 322 -> KEY_NUMPAD2;
            case 323 -> KEY_NUMPAD3;
            case 324 -> KEY_NUMPAD4;
            case 325 -> KEY_NUMPAD5;
            case 326 -> KEY_NUMPAD6;
            case 327 -> KEY_NUMPAD7;
            case 328 -> KEY_NUMPAD8;
            case 329 -> KEY_NUMPAD9;
            case 330 -> KEY_NUMPADCOMMA;
            case 335 -> KEY_NUMPADENTER;
            case 336 -> KEY_NUMPADEQUALS;
            case 262 -> KEY_RIGHT;
            case 263 -> KEY_LEFT;
            case 264 -> KEY_DOWN;
            case 265 -> KEY_UP;
            case 334 -> KEY_ADD;
            case 39 -> KEY_APOSTROPHE;
            case 92 -> KEY_BACKSLASH;
            case 44 -> KEY_COMMA;
            case 61 -> KEY_EQUALS;
            case 96 -> KEY_GRAVE;
            case 91 -> KEY_LBRACKET;
            case 45 -> KEY_MINUS;
            case 332 -> KEY_MULTIPLY;
            case 46 -> KEY_PERIOD;
            case 93 -> KEY_RBRACKET;
            case 59 -> KEY_SEMICOLON;
            case 47 -> KEY_SLASH;
            case 32 -> KEY_SPACE;
            case 258 -> KEY_TAB;
            case 341 -> KEY_LCONTROL;
            case 340 -> KEY_LSHIFT;
            case 343 -> KEY_LMENU;
            case 345 -> KEY_RCONTROL;
            case 344 -> KEY_RSHIFT;
            case 347 -> KEY_RMETA;
            case 257 -> KEY_RETURN;
            case 256 -> KEY_ESCAPE;
            case 259 -> KEY_BACK;
            case 261 -> KEY_DELETE;
            case 269 -> KEY_END;
            case 268 -> KEY_HOME;
            case 260 -> KEY_INSERT;
            case 284 -> KEY_PAUSE;
            case 281 -> KEY_SCROLL;
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
            case KEY_0 -> 48;
            case KEY_1 -> 49;
            case KEY_2 -> 50;
            case KEY_3 -> 51;
            case KEY_4 -> 52;
            case KEY_5 -> 53;
            case KEY_6 -> 54;
            case KEY_7 -> 55;
            case KEY_8 -> 56;
            case KEY_9 -> 57;
            case KEY_A -> 65;
            case KEY_B -> 66;
            case KEY_C -> 67;
            case KEY_D -> 68;
            case KEY_E -> 69;
            case KEY_F -> 70;
            case KEY_G -> 71;
            case KEY_H -> 72;
            case KEY_I -> 73;
            case KEY_J -> 74;
            case KEY_K -> 75;
            case KEY_L -> 76;
            case KEY_M -> 77;
            case KEY_N -> 78;
            case KEY_O -> 79;
            case KEY_P -> 80;
            case KEY_Q -> 81;
            case KEY_R -> 82;
            case KEY_S -> 83;
            case KEY_T -> 84;
            case KEY_U -> 85;
            case KEY_V -> 86;
            case KEY_W -> 87;
            case KEY_X -> 88;
            case KEY_Y -> 89;
            case KEY_Z -> 90;
            case KEY_F1 -> 290;
            case KEY_F2 -> 291;
            case KEY_F3 -> 292;
            case KEY_F4 -> 293;
            case KEY_F5 -> 294;
            case KEY_F6 -> 295;
            case KEY_F7 -> 296;
            case KEY_F8 -> 297;
            case KEY_F9 -> 298;
            case KEY_F10 -> 299;
            case KEY_F11 -> 300;
            case KEY_F12 -> 301;
            case KEY_F13 -> 302;
            case KEY_F14 -> 303;
            case KEY_F15 -> 304;
            case KEY_F16 -> 305;
            case KEY_F17 -> 306;
            case KEY_F18 -> 307;
            case KEY_F19 -> 308;
            case KEY_NUMLOCK -> 282;
            case KEY_NUMPAD0 -> 320;
            case KEY_NUMPAD1 -> 321;
            case KEY_NUMPAD2 -> 322;
            case KEY_NUMPAD3 -> 323;
            case KEY_NUMPAD4 -> 324;
            case KEY_NUMPAD5 -> 325;
            case KEY_NUMPAD6 -> 326;
            case KEY_NUMPAD7 -> 327;
            case KEY_NUMPAD8 -> 328;
            case KEY_NUMPAD9 -> 329;
            case KEY_NUMPADCOMMA -> 330;
            case KEY_NUMPADENTER -> 335;
            case KEY_NUMPADEQUALS -> 336;
            case KEY_RIGHT -> 262;
            case KEY_LEFT -> 263;
            case KEY_DOWN -> 264;
            case KEY_UP -> 265;
            case KEY_ADD -> 334;
            case KEY_APOSTROPHE -> 39;
            case KEY_BACKSLASH -> 92;
            case KEY_COMMA -> 44;
            case KEY_EQUALS -> 61;
            case KEY_GRAVE -> 96;
            case KEY_LBRACKET -> 91;
            case KEY_MINUS -> 45;
            case KEY_MULTIPLY -> 332;
            case KEY_PERIOD -> 46;
            case KEY_RBRACKET -> 93;
            case KEY_SEMICOLON -> 59;
            case KEY_SLASH -> 47;
            case KEY_SPACE -> 32;
            case KEY_TAB -> 258;
            case KEY_LCONTROL -> 341;
            case KEY_LSHIFT -> 340;
            case KEY_LMENU -> 343;
            case KEY_RCONTROL -> 345;
            case KEY_RSHIFT -> 344;
            case KEY_RMETA -> 347;
            case KEY_RETURN -> 257;
            case KEY_ESCAPE -> 256;
            case KEY_BACK -> 259;
            case KEY_DELETE -> 261;
            case KEY_END -> 269;
            case KEY_HOME -> 268;
            case KEY_INSERT -> 260;
            case KEY_PAUSE -> 284;
            case KEY_SCROLL -> 281;
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
