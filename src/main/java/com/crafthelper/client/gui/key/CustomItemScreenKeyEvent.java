package com.crafthelper.client.gui.key;

import com.crafthelper.CraftHelper;
import com.crafthelper.client.gui.screen.CustomItemScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CustomItemScreenKeyEvent {
    private static KeyBinding openUIKey;
    public static void registerEvent(){
        // 注册ui
        // 创建按键绑定 (Z+X)
        openUIKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + CraftHelper.MOD_ID + ".custom_item", // 翻译键
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z, // 主键
                "key.categories."+ CraftHelper.MOD_ID // 分类
        ));

        // 监听客户端每帧
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 检查 Z 键是否被按下
            if (openUIKey.isPressed()) {
                // 打开界面
                if (client.player != null) {
                    client.setScreen(new CustomItemScreen());
                }
            }
        });
    }
}
