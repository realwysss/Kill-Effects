package dev.klfx;

public enum Fx {
	LGHTNG("Lightning"), FRWRK("Firework"), TOTM("Totem of Undying"), BLD("Blood"), SOUL("Soul Fire");

	final String nm;

	Fx(String n) {
		nm = n;
	}

	@Override
	public String toString() {
		return nm;
	}
}