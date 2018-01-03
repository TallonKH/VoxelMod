package data;

/**
 * Created by tt on 7/13/17.
 */
public final class Byte3 {
	public final byte x;
	public final byte y;
	public final byte z;

	public Byte3(int x, int y, int z) {
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
	}

	public static short bytesToShort(byte a, byte b) {
		return (short) ((a << 8) | b);
	}

	public Byte3 multiply(int val) {
		return new Byte3(x * val, y * val, z * val);
	}


	public Byte3 multiply(Byte3 val) {
		return new Byte3(x * val.x, y * val.y, z * val.z);
	}

	public Byte3(int val) {
		x = (byte) ((val >> 16) & 0xFF);
		y = (byte) ((val >> 8) & 0xFF);
		z = (byte) (val & 0xFF);
	}

	public Byte3 combine(Byte3 val) {
		return new Byte3(x + val.x, y + val.y, z + val.z);
	}

	public Byte3 combine(int x, int y, int z) {
		return new Byte3(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public int hashCode() {
		return asInt(x, y, z);
	}

	public static int asInt(byte x, byte y, byte z) {
		return ((int) x << 16) | ((int) y << 8) | (z);
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "," + z + "}";
	}
}
