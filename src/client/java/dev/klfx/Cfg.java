package dev.klfx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public Set<Fx> fxs = new LinkedHashSet<>(List.of(Fx.LGHTNG));
	public boolean rnd = false;
	public boolean plrs = true;
	public boolean mobs = true;
	public Clr clr = Clr.RED;
	public int cus = 0xFF3030;
	public boolean snd = true;
	public Snd sndT = Snd.LVLUP;
	public int vol = 100;

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("klfx.json");
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null && n.fxs != null && n.clr != null) c = n;
			}
		} catch (Exception e) {
			c = new Cfg();
		}
	}

	public static void sv() {
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}
}
