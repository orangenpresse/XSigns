package orangenpresse.xsigns;

import org.bukkit.block.Block;

public class XSign {
	//Private variables
	private String world;
	private String environment;
	private int x;
	private int y;
	private int z;
	private int state = 0;
	private boolean flank = false;
	private XSignType type = XSignType.Sign;
	String[][] text;

	//Constructors	
	public XSign(Block block) {
		super();
		this.world = block.getLocation().getWorld().getName();
		this.environment = block.getLocation().getWorld().getEnvironment().toString();
		this.x = block.getLocation().getBlockX();
		this.y = block.getLocation().getBlockY();
		this.z = block.getLocation().getBlockZ();
	}
	
	public XSign() {
		super();
	}
	
	//Methods
	public void addState(String[] lines) {
		String[][] temp;
		//copy array
		if(this.text != null) {
			temp = new String[text.length+1][];
			System.arraycopy(text, 0, temp, 0, text.length);
		}
		else
		{
			temp = new String[1][];
		}

		temp[temp.length-1] = lines;
		this.text = temp;
	}

	public void nextState(boolean flank) {
		if(flank != this.flank) {
			if(state < text.length-1)
				state++;
			else
				state = 0;
			
			//Check is XSignCounter
			if(this.type == XSignType.XSignCounter && this.flank == false) {
				counterStep();
			}
			
			this.flank = flank;
		}
	}
	
	private void counterStep() {
		for(int i=0; i < text[0].length; i++) {
			if(text[0][i].matches("^\\[counter\\]:.*$")) {
				//get counter value
				int count = Integer.valueOf(text[0][i].split("\\:")[1]).intValue();
				//inkrement counter
				count++;
				//save counter
				text[0][i] = "[counter]:"+count;
			}
		}
	}
	
	private String[] counterOutput() {
		String[] temp = new String[text[0].length];
		
		for(int i=0; i < text[0].length; i++) {
			if(text[0][i].matches("^\\[counter\\]:.*$"))
				temp[i] = text[0][i].split("\\:")[1];
			else
				temp[i] = text[0][i];
		}
		
		return temp;
	}
	
	public String[] getStateLines() {
		if(this.type == XSignType.XSignCounter)
			return counterOutput();
		else
			return text[state];
	}
	
	
	//Getters and Setters
	public int getState() {
		return state;
	}
	public boolean isFlank() {
		return flank;
	}

	public void setFlank(boolean flank) {
		this.flank = flank;
	}

	public String[][] getText() {
		return text;
	}

	public void setText(String[][] text) {
		this.text = text;
	}

	public void setState(int state) {
		this.state = state;
	}
	public XSignType getType() {
		return this.type;
	}
	public void setType(XSignType type) {
		this.type = type;
	}
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
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
	
}
