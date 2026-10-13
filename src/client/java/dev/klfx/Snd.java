package dev.klfx;

public enum Snd {
	LVLUP("Level Up"), ORB("Experience Orb"), TOTM("Totem of Undying"), THNDR("Thunder");

	final String nm;

	Snd(String n) {
		nm = n;
	}

	@Override
	public String toString() {
		return nm;
	}
}