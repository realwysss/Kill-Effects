package dev.klfx;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.block.Blocks;

public class Fxs {
	public static List<Fx> run(Minecraft m, LivingEntity e) {
		ClientLevel l = m.level;
		List<Fx> fs = new ArrayList<>();
		if (Cfg.c.rnd) fs.add(Fx.values()[l.random.nextInt(Fx.values().length)]);
		else for (Fx f : Fx.values()) if (Cfg.c.fxs.contains(f)) fs.add(f);
		double x = e.getX(), y = e.getY(), z = e.getZ(), h = e.getBbHeight() / 2;
		for (Fx f : fs) {
			switch (f) {
				case LGHTNG -> {
					LightningBolt b = EntityType.LIGHTNING_BOLT.create(l, EntitySpawnReason.TRIGGERED);
					if (b != null) {
						b.setPos(x, y, z);
						b.setVisualOnly(true);
						l.addEntity(b);
					}
				}
				case FRWRK -> {
					int c = Cfg.c.clr == Clr.CSTM ? Cfg.c.cus : Cfg.c.clr.rgb;
					l.createFireworks(x, y + h, z, 0, 0, 0,
							List.of(new FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL, IntList.of(c), IntList.of(c), false, true)));
				}
				case TOTM -> {
					for (int i = 0; i < 40; i++)
						l.addParticle(ParticleTypes.TOTEM_OF_UNDYING, x, y + h, z, (l.random.nextDouble() - .5) * 1.2, l.random.nextDouble() * 1.2, (l.random.nextDouble() - .5) * 1.2);
				}
				case BLD -> {
					for (int i = 0; i < 40; i++)
						l.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()), x + (l.random.nextDouble() - .5) * .6, y + h + (l.random.nextDouble() - .5) * h, z + (l.random.nextDouble() - .5) * .6, (l.random.nextDouble() - .5) * .3, l.random.nextDouble() * .3, (l.random.nextDouble() - .5) * .3);
				}
				case SOUL -> {
					for (int i = 0; i < 40; i++)
						l.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + (l.random.nextDouble() - .5) * .6, y + l.random.nextDouble() * h, z + (l.random.nextDouble() - .5) * .6, 0, .1 + l.random.nextDouble() * .1, 0);
				}
			}
		}
		if (Cfg.c.snd) {
			SoundEvent s = switch (Cfg.c.sndT) {
				case LVLUP -> SoundEvents.PLAYER_LEVELUP;
				case ORB -> SoundEvents.EXPERIENCE_ORB_PICKUP;
				case TOTM -> SoundEvents.TOTEM_USE;
				case THNDR -> SoundEvents.LIGHTNING_BOLT_THUNDER;
			};
			l.playLocalSound(x, y, z, s, SoundSource.PLAYERS, Cfg.c.vol / 100f, 1f, false);
		}
		return fs;
	}
}