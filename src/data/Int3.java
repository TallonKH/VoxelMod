package data;

public class Int3 {
	public final int x;
	public final int y;
	public final int z;

	public Int3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Int3 multiply(int val) {
		return new Int3(x * val, y * val, z * val);
	}

	public Int3 multiply(Byte3 val) {
		return new Int3(x * val.x, y * val.y, z * val.z);
	}

	public Int3 combine(Int3 val) {
		return new Int3(x + val.x, y + val.y, z + val.z);
	}

	public Int3 combine(int x, int y, int z) {
		return new Int3(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public int hashCode() {
		return asOneInt(x, y, z);
	}

	public static int asOneInt(int x, int y, int z) {
		return (x << 20) | (y << 10) | (z);
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "," + z + "}";
	}
}
