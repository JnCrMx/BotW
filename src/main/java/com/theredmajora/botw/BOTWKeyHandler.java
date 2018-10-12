package com.theredmajora.botw;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BOTWKeyHandler
{
	public static KeyBinding[] keyBindings;
	
	public static void init()
	{
		keyBindings = new KeyBinding[4]; 

		keyBindings[0] = new KeyBinding("key.offhandequip.desc", Keyboard.KEY_SEMICOLON, "key.gameplay.category");
		
		keyBindings[1] = new KeyBinding("key.throw_entity.desc", Keyboard.KEY_B, "key.botw.category");
		keyBindings[2] = new KeyBinding("key.drop_entity.desc", Keyboard.KEY_V, "key.botw.category");
		keyBindings[3] = new KeyBinding("key.select_sheikah_module.desc", Keyboard.KEY_C, "key.botw.category");

		for (int i = 0; i < keyBindings.length; ++i) 
		{
		    ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}
}
