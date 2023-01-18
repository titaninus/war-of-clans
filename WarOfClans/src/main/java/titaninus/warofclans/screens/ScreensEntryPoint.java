package titaninus.warofclans.screens;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import titaninus.warofclans.WarOfClans;
import titaninus.warofclans.client.WarOfClansClient;

public class ScreensEntryPoint {
    private static KeyBinding adminMenuOpen;
    public static AdminDataScreen AdminScreen;

    public static void Initialize() {
        adminMenuOpen  = new KeyBinding(
                "titaninus.warofclans.openadminmenu", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                WarOfClansClient.WOC_CONFIG.OpenAdminMenuKey(), // The keycode of the key
                "titaninus.warofclans.openadminmenu" // The translation key of the keybinding's category.
        );
        KeyBindingHelper.registerKeyBinding(adminMenuOpen);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (adminMenuOpen.wasPressed()) {
                AdminScreen = new AdminDataScreen();
                client.setScreen(AdminScreen);
            }
        });

    }
}
