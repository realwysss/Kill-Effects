package dev.klfx;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.widget.SearchFieldEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.network.chat.Component;

public class ModmenuIntgr implements ModMenuApi {
	static Component t(String s) {
		return Component.literal(s);
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return p -> {
			ConfigBuilder b = ConfigBuilder.create().setParentScreen(p).setTitle(t("Kill Effects")).setSavingRunnable(Cfg::sv);
			b.setAfterInitConsumer(s -> {
				if (s instanceof ClothConfigScreen cs) cs.listWidget.children().removeIf(e -> ((Object) e) instanceof SearchFieldEntry);
			});
			ConfigCategory configctgry = b.getOrCreateCategory(t("Main"));
			ConfigEntryBuilder configIn = b.entryBuilder();
			Cfg c = Cfg.c;
			configctgry.addEntry(configIn.startBooleanToggle(t("Mod On"), c.on).setDefaultValue(true).setSaveConsumer(v -> c.on = v).build());
			SubCategoryBuilder sb = configIn.startSubCategory(t("Effects"));
			for (Fx f : Fx.values())
				sb.add(configIn.startBooleanToggle(t(f.nm), c.fxs.contains(f)).setDefaultValue(f == Fx.LGHTNG).setSaveConsumer(v -> {
					if (v) c.fxs.add(f);
					else c.fxs.remove(f);
				}).build());
			configctgry.addEntry(sb.build());
			configctgry.addEntry(configIn.startBooleanToggle(t("Random Effect"), c.rnd).setDefaultValue(false).setTooltip(t("Picks one random effect on every kill")).setSaveConsumer(v -> c.rnd = v).build());
			configctgry.addEntry(configIn.startBooleanToggle(t("Effects On Players"), c.plrs).setDefaultValue(true).setSaveConsumer(v -> c.plrs = v).build());
			configctgry.addEntry(configIn.startBooleanToggle(t("Effects On Mobs"), c.mobs).setDefaultValue(true).setSaveConsumer(v -> c.mobs = v).build());
			configctgry.addEntry(configIn.startEnumSelector(t("Firework Color"), Clr.class, c.clr).setDefaultValue(Clr.RED).setEnumNameProvider(e -> t(e.toString())).setSaveConsumer(v -> c.clr = v).build());
			configctgry.addEntry(configIn.startColorField(t("Custom Firework Color"), c.cus).setDefaultValue(0xFF3030).setTooltip(t("Used when Firework Color is Custom")).setSaveConsumer(v -> c.cus = v).build());
			configctgry.addEntry(configIn.startBooleanToggle(t("Kill Sound"), c.snd).setDefaultValue(true).setSaveConsumer(v -> c.snd = v).build());
			configctgry.addEntry(configIn.startEnumSelector(t("Sound Type"), Snd.class, c.sndT).setDefaultValue(Snd.LVLUP).setEnumNameProvider(e -> t(e.toString())).setSaveConsumer(v -> c.sndT = v).build());
			configctgry.addEntry(configIn.startIntSlider(t("Sound Volume"), c.vol, 0, 100).setDefaultValue(100).setSaveConsumer(v -> c.vol = v).build());
			return b.build();
		};
	}
}