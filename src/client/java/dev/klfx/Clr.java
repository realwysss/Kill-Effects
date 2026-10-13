package dev.klfx;

public enum Clr {
	RED("Red", 0xE02020), ORNG("Orange", 0xFF8000), YLW("Yellow", 0xFFE020), LIME("Lime", 0x80FF20), GRN("Green", 0x20B020),
	CYAN("Cyan", 0x20E0E0), BLUE("Blue", 0x2060FF), PRPL("Purple", 0x9020E0), PINK("Pink", 0xFF60C0), WHT("White", 0xFFFFFF),
	CSTM("Custom", 0);

	final String nm;
	final int rgb;

	Clr(String n, int r) {
		nm = n;
		rgb = r;
	}

	@Override
	public String toString() {
		return nm;
	}
}