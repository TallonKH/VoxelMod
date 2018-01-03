package data;

/**
 * Created by TT on 7/18/2017.
 */
public enum Side {
	FORWARD(0, 0, 1) {
		@Override
		public Object select(Object a, Object b, Object c) {
			return c;
		}
	},
	BACKWARD(0, 0, -1) {
		@Override
		public Object select(Object a, Object b, Object c) {
			return c;
		}
	},
	DOWN(0, -1, 0) {
		@Override
		public Object select(Object a, Object b, Object c) {
			return b;
		}
	},
	UP(0, 1, 0) {
		@Override
		public Object select(Object a, Object b, Object c) {
			return b;
		}
	},
	RIGHT(1, 0, 0) {
		@Override
		public Object select(Object a, Object b, Object c) {
			return a;
		}
	},
	LEFT(-1, 0, 0) {
		@Override
		public Object select(Object a, Object b, Object c) {
			return a;
		}
	};

	public final byte x;
	public final byte y;
	public final byte z;
	public Side sideA;
	public Side sideB;
	public Side opposite;

	static {
		FORWARD.sideA = RIGHT;
		FORWARD.sideB = UP;
		FORWARD.opposite = BACKWARD;
		BACKWARD.sideA = RIGHT;
		BACKWARD.sideB = UP;
		BACKWARD.opposite = FORWARD;
		UP.sideA = RIGHT;
		UP.sideB = FORWARD;
		UP.opposite = DOWN;
		DOWN.sideA = RIGHT;
		DOWN.sideB = FORWARD;
		DOWN.opposite = UP;
		RIGHT.sideA = FORWARD;
		RIGHT.sideB = UP;
		RIGHT.opposite = LEFT;
		LEFT.sideA = FORWARD;
		LEFT.sideB = UP;
		LEFT.opposite = RIGHT;
	}

	public abstract Object select(Object a, Object b, Object c);

	Side(int x, int y, int z) {
		this.x = (byte) x;
		this.y = (byte) y;
		this.z = (byte) z;
	}
}
