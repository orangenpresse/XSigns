package orangenpresse.xsigns;

public class XSign {
	private String world;
	private int x;
	private int y;
	private int z;
	
	public XSign(String world, int x, int y, int z, String[][] text) {
		super();
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.text = text;
	}
	
	public XSign() {
		super();
	}
	
	String[][] text;
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public String[][] getText() {
		return text;
	}
	public void setText(String[][] text) {
		this.text = text;
	}
	
}
