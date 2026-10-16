package dev.klfx;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	static String[] nms(Object[] a) {
		String[] r = new String[a.length];
		for (int i = 0; i < a.length; i++) r[i] = a[i].toString();
		return r;
	}

	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Clr[] cl = Clr.values();
		int[] cols = new int[cl.length];
		for (int i = 0; i < cl.length; i++) cols[i] = cl[i] == Clr.CSTM ? c.cus : cl[i].rgb;
		Frm f = Frm.of("Kill Effects", Cfg::sv).tab("Effects").tgl("Kill Effects on", () -> c.on, v -> c.on = v);
		f.btn("Test the effects now", () -> {
			Minecraft m = Minecraft.getInstance();
			if (m.player != null && m.level != null) Fxs.run(m, m.player);
		});
		for (Fx x : Fx.values()) f.tgl(x.nm, () -> c.fxs.contains(x), v -> {
			if (v) c.fxs.add(x);
			else c.fxs.remove(x);
		});
		f.tgl("Random effect", () -> c.rnd, v -> c.rnd = v).tip("Picks one random effect on every kill instead of all the ticked ones.")
				.tab("Targets")
				.tgl("Effects on players", () -> c.plrs, v -> c.plrs = v)
				.tgl("Effects on mobs", () -> c.mobs, v -> c.mobs = v)
				.note("Kills count when you hit a target and it dies within 3 s.")
				.tab("Firework")
				.sw("Color", cols, nms(cl), () -> c.clr.ordinal(), v -> c.clr = cl[v])
				.clr("Custom color", () -> c.cus, v -> {
					c.cus = v & 0xffffff;
					cols[cols.length - 1] = c.cus;
				}).tip("Used when the color above is Custom.")
				.tab("Sound")
				.tgl("Kill sound", () -> c.snd, v -> c.snd = v)
				.cho("Sound", nms(Snd.values()), () -> c.sndT.ordinal(), v -> c.sndT = Snd.values()[v])
				.sld("Volume", () -> c.vol, v -> c.vol = v, 0, 100, "%")
				.pre((g, fm, x, y, w, h) -> {
					g.text("Preview", x + 10, y + 9, 0x99FFFFFF);
					if (!g.player(x + 10, y + 22, w - 20, h - 30)) g.text("join a world", x + 10, y + 40, 0x99FFFFFF);
				});
		return Wl.screen(p, f, "Kill Effects", KlfxClnt.opn);
	}
}
