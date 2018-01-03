package data;

/**
 * Created by TT on 7/31/2017.
 */
public class Short3 {
	public final short x;
	public final short y;
	public final short z;

	public Short3(int x, int y, int z) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (short) z;
	}

	public static Short3 decodeSigned(int n) {
		return new Short3(((n >>> 20) & 0x1FF) * ((n & 0x20000000) > 0 ? -1 : 1),
						  ((n >>> 10) & 0x1FF) * ((n & 0x80000) > 0 ? -1 : 1),
						  (n & 0x1FF) * ((n & 0x200) > 0 ? -1 : 1));
	}

	public static Short3 decodeUnsigned(int n) {
		return new Short3(((n >> 20) & 0x3FF),
						  ((n >> 10) & 0x3FF),
						  (n & 0x3FF));
	}

	public static int asIntSigned(short x, short y, short z) {
		return ((Math.abs(x) & 0x1FF) << 20) | (x < 0 ? 0x20000000 : 0) |
			   ((Math.abs(y) & 0x1FF) << 10) | (y < 0 ? 0x80000 : 0) |
			   (Math.abs(z) & 0x1FF) | (z < 0 ? 0x200 : 0);
	}

	public static int asIntUnsigned(short x, short y, short z){
		return ((Math.abs(x) & 0x3FF) << 20) |
			   ((Math.abs(y) & 0x3FF) << 10) |
			   (Math.abs(z) & 0x3FF);
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "," + z + "}";
	}
}
