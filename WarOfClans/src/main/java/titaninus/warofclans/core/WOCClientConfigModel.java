package titaninus.warofclans.core;

import io.wispforest.owo.config.annotation.Config;
import org.lwjgl.glfw.GLFW;

@Config(name = "war-of-clans-client-config", wrapperName = "WOCClientConfig")
public class WOCClientConfigModel {

    public int OpenAdminMenuKey = GLFW.GLFW_KEY_Y;
}
