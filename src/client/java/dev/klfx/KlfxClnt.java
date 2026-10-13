package dev.klfx;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;

public class KlfxClnt implements ClientModInitializer {
	public static Map<LivingEntity, Long> atk = new LinkedHashMap<>();
	public static int kls;
	public static List<Fx> lstFx;
	public static KeyMapping tgl;

	@Override
	public void onInitializeClient() {
		Cfg.ld();
		tgl = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.klfx.tgl", InputConstants.UNKNOWN.getValue(),
				KeyMapping.Category.register(Identifier.fromNamespaceAndPath("klfx", "main"))));
		AttackEntityCallback.EVENT.register((p, w, h, e, r) -> {
			if (w.isClientSide() && p == Minecraft.getInstance().player) {
				long n = w.getGameTime();
				if (e instanceof LivingEntity l) atk.put(l, n);
				else if (e instanceof EndCrystal)
					for (LivingEntity l : w.getEntitiesOfClass(LivingEntity.class, e.getBoundingBox().inflate(12)))
						if (l != p) atk.put(l, n);
			}
			return InteractionResult.PASS;
		});
		ClientTickEvents.END_CLIENT_TICK.register(m -> {
			while (tgl.consumeClick()) {
				Cfg.c.on = !Cfg.c.on;
				Cfg.sv();
				if (m.player != null) m.player.displayClientMessage(Component.literal("Kill Effects: " + (Cfg.c.on ? "ON" : "OFF")), true);
			}
			tk(m);
		});
	}

	public static void tk(Minecraft m) {
		if (atk.isEmpty() || m.level == null) return;
		long n = m.level.getGameTime();
		Iterator<Map.Entry<LivingEntity, Long>> it = atk.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<LivingEntity, Long> k = it.next();
			LivingEntity e = k.getKey();
			if (n - k.getValue() > 60) it.remove();
			else if (e.isRemoved() || e.getHealth() <= 0 || e.isDeadOrDying()) {
				it.remove();
				if (Cfg.c.on && (e instanceof Player ? Cfg.c.plrs : Cfg.c.mobs)) {
					kls++;
					lstFx = Fxs.run(m, e);
				}
			}
		}
	}
}
