package dev.klfx;

import java.util.List;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.animal.pig.Pig;

public class KlfxTst implements FabricClientGameTest {
	ClientGameTestContext c;
	TestSingleplayerContext sp;

	void ck(boolean b, String s) {
		if (!b) throw new AssertionError(s);
		System.out.println("KLFX OK: " + s);
	}

	int kls() {
		return c.computeOnClient(m -> KlfxClnt.kls);
	}

	void cmd(String s) {
		sp.getServer().runCommand(s);
	}

	void rst() {
		cmd("kill @e[type=!player]");
		c.waitTicks(10);
		cmd("tp @p 0.5 -60 0.5 0 12");
		cmd("summon pig 0.5 -60 2.5 {NoAI:1b,Silent:1b}");
		c.waitTicks(10);
		c.runOnClient(m -> {
			KlfxClnt.atk.clear();
			KlfxClnt.kls = 0;
			KlfxClnt.lstFx = null;
		});
	}

	void hit() {
		c.runOnClient(m -> {
			Pig p = m.level.getEntitiesOfClass(Pig.class, m.player.getBoundingBox().inflate(10)).get(0);
			m.gameMode.attack(m.player, p);
		});
	}

	void cf(boolean on, boolean plrs, boolean mobs, boolean rnd, Fx... fs) {
		c.runOnClient(m -> {
			Cfg.c.fxs = new java.util.LinkedHashSet<>(java.util.List.of(fs));
			Cfg.c.rnd = rnd;
			Cfg.c.plrs = plrs;
			Cfg.c.mobs = mobs;
			Cfg.c.on = on;
			Cfg.c.snd = true;
		});
	}

	void go(String n, int w) {
		rst();
		c.waitTicks(30);
		hit();
		c.waitTicks(2);
		cmd("tp @p 0.5 -60 -1.2 0 5");
		c.waitTicks(3);
		cmd("kill @e[type=pig]");
		c.waitTicks(3);
		if (n != null) {
			c.takeScreenshot(n);
			c.waitTicks(w);
			c.takeScreenshot(n + "-b");
		} else c.waitTicks(w);
	}

	List<Fx> lf() {
		return c.computeOnClient(m -> KlfxClnt.lstFx);
	}

	void ign(String s) {
		cmd("kill @e[type=pig]");
		c.waitTicks(5);
		ck(kls() == 0, s);
	}

	@Override
	public void runTest(ClientGameTestContext ctx) {
		c = ctx;
		try (TestSingleplayerContext s = ctx.worldBuilder().create()) {
			sp = s;
			s.getClientWorld().waitForChunksRender();
			cmd("time set midnight");
			cmd("weather clear");
			cmd("gamerule doDaylightCycle false");
			cmd("gamerule doMobSpawning false");
			cmd("gamerule doWeatherCycle false");
			ctx.waitTicks(20);

			for (Fx f : Fx.values()) {
				cf(true, true, true, false, f);
				go("fx-" + f.name().toLowerCase(), 8);
				ck(kls() == 1, "kill detected " + f);
				ck(lf().equals(List.of(f)), "only " + f + " ran");
			}

			cf(true, true, true, false, Fx.LGHTNG, Fx.TOTM, Fx.BLD, Fx.SOUL);
			go("fx-stack", 8);
			ck(kls() == 1, "kill detected stack");
			ck(lf().size() == 4, "stack ran 4 effects " + lf());

			cf(true, true, true, true, Fx.LGHTNG, Fx.TOTM);
			go("fx-random", 8);
			ck(kls() == 1 && lf().size() == 1, "random ran 1 effect " + lf());

			cf(true, true, true, false);
			go(null, 5);
			ck(kls() == 1 && lf().isEmpty(), "no effects selected runs nothing");

			cf(true, true, true, false, Fx.FRWRK);
			for (Clr k : new Clr[] {Clr.BLUE, Clr.GRN, Clr.CSTM}) {
				ctx.runOnClient(m -> {
					Cfg.c.clr = k;
					Cfg.c.cus = 0xFF00FF;
				});
				go("fw-" + k.name().toLowerCase(), 8);
				ck(kls() == 1, "firework " + k);
			}

			cf(true, true, true, false, Fx.TOTM);
			rst();
			ign("no hit no fx");

			rst();
			cmd("summon zombie 3.5 -60 3.5 {NoAI:1b,Silent:1b,PersistenceRequired:1b}");
			ctx.waitTicks(5);
			cmd("damage @e[type=pig,limit=1] 100 minecraft:mob_attack by @e[type=zombie,limit=1]");
			ctx.waitTicks(5);
			ck(kls() == 0, "mob kill ignored");

			rst();
			cmd("summon sheep 3.5 -60 3.5 {NoAI:1b,Silent:1b}");
			ctx.waitTicks(5);
			hit();
			ctx.waitTicks(2);
			cmd("kill @e[type=sheep]");
			ctx.waitTicks(5);
			ck(kls() == 0, "other entity dying ignored");

			rst();
			hit();
			ctx.waitTicks(80);
			ign("late kill ignored");

			cf(false, true, true, false, Fx.TOTM);
			rst();
			hit();
			ctx.waitTicks(2);
			ign("mod off ignored");

			cf(true, true, false, false, Fx.TOTM);
			rst();
			hit();
			ctx.waitTicks(2);
			ign("mobs off ignored");

			cf(true, false, true, false, Fx.TOTM);
			rst();
			hit();
			ctx.waitTicks(2);
			cmd("kill @e[type=pig]");
			ctx.waitTicks(5);
			ck(kls() == 1, "players off but mobs on still works");

			cf(true, true, true, false, Fx.TOTM);
			rst();
			cmd("effect give @p minecraft:resistance 1000 4 true");
			cmd("data merge entity @e[type=pig,limit=1] {Health:1f}");
			cmd("summon end_crystal 0.5 -60 3.5 {ShowBottom:0b}");
			ctx.waitTicks(10);
			ctx.runOnClient(m -> m.gameMode.attack(m.player, m.level.getEntitiesOfClass(net.minecraft.world.entity.boss.enderdragon.EndCrystal.class, m.player.getBoundingBox().inflate(10)).get(0)));
			ctx.waitTicks(10);
			ck(kls() == 1, "crystal kill counted");

			rst();
			cmd("summon end_crystal 0.5 -60 3.5 {ShowBottom:0b}");
			cmd("data merge entity @e[type=pig,limit=1] {Health:1f}");
			ctx.waitTicks(5);
			cmd("kill @e[type=end_crystal]");
			ctx.waitTicks(10);
			ck(kls() == 0, "crystal not hit by me ignored");
			cmd("kill @e[type=end_crystal]");
			ctx.waitTicks(10);
			cmd("effect clear @p");
			cf(true, true, true, false, Fx.TOTM);
			rst();
			ctx.runOnClient(m -> {
				KlfxClnt.tgl.setKey(InputConstants.Type.KEYSYM.getOrCreate(74));
				KeyMapping.resetMapping();
			});
			ctx.getInput().pressKey(74);
			ctx.waitTicks(3);
			ck(!ctx.computeOnClient(m -> Cfg.c.on), "key toggled off");
			ctx.takeScreenshot("key-off");
			ctx.getInput().pressKey(74);
			ctx.waitTicks(3);
			ck(ctx.computeOnClient(m -> Cfg.c.on), "key toggled on");
			ctx.takeScreenshot("key-on");

			ctx.runOnClient(m -> {
				Screen sc = (Screen) new ModmenuIntgr().getModConfigScreenFactory().create(null);
				m.setScreen(sc);
			});
			ctx.waitTicks(5);
			ctx.takeScreenshot("cfg-main");
			ctx.getInput().setCursorPos(120, 160);
			ctx.getInput().pressMouse(0);
			ctx.waitTicks(10);
			ctx.takeScreenshot("cfg-open");
			ctx.getInput().scroll(-10);
			ctx.waitTicks(10);
			ctx.takeScreenshot("cfg-low");
		}
	}
}