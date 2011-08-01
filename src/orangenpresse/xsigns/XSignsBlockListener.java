package orangenpresse.xsigns;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

/**
 * XTuringSigns block listener
 * @author Orangenpresse
 */
public class XSignsBlockListener extends BlockListener {
    private final XSigns plugin;
    private Player player;
    
    public XSignsBlockListener(final XSigns plugin) {
        this.plugin = plugin;
    }

    public void onBlockRedstoneChange(BlockRedstoneEvent event) {    	
    	//Check blocks arround
    	for(BlockFace face : BlockFace.values())
    	{
    		checkCurrent(event.getBlock().getFace(face),event.getBlock().getFace(face).isBlockIndirectlyPowered());

    		//check current from behind
    		if(	event.getBlock().getType() == Material.REDSTONE_WIRE && (face == BlockFace.EAST || face == BlockFace.WEST || face == BlockFace.SOUTH || face == BlockFace.NORTH)) {
    			checkCurrent(event.getBlock().getFace(face).getFace(face),event.getBlock().getFace(face).isBlockIndirectlyPowered());
    		}
    	}
    }
    
    private void checkCurrent(Block block, boolean current) {
    	if(block.getType() == Material.SIGN_POST || block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN) {
    		//Cast block into a sign
    		Sign sign = (Sign)block.getState();
    		//get the Strings for the sign
    		XSign xsign = plugin.getXSign(block);
    		//is the Sign a Redstone sign?
    		if(xsign == null)
    			return;
    		//Set next state
    		xsign.nextState(current);

    		//write the new lines
    		for(int i = 0; i < xsign.getStateLines().length; i++)
			{
				if(xsign.getStateLines()[i] != null)
					sign.setLine(i, xsign.getStateLines()[i]);
			}
    	}

    	//Update the Sign
    	((CraftWorld)block.getWorld()).getHandle().notify(block.getX(),block.getY(),block.getZ());
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
    	this.player = event.getPlayer();
    	if(plugin.isXSign(event.getBlock()))
    	{
    	   	if(plugin.getXSign(event.getBlock()).getType() == XSignType.HalfXSign || plugin.getXSign(event.getBlock()).getType() == XSignType.TriggerXSign)
    	   	{
    	   		this.player.sendMessage("Please build the other part of the XSign");
    	   	}
    	   	else
    	   	{
    	   		plugin.removeSign(event.getBlock());
    			this.player.sendMessage("XSign destroyed");
    	   	}
    	}
    }
    
    public void onSignChange(SignChangeEvent event) {
    	Block block = event.getBlock();
    	this.player = event.getPlayer();
    	String[] lines = event.getLines();
    	String[][] newLines = new String[2][4];
       	XSign sign = new XSign(block);
    	
    	for(int i = 0; i < lines.length; i++) {
    		//is the new sign a XSign?
    		if(lines[i].matches("^<.*\\|.*>$") && sign.getType() != XSignType.HalfXSign && sign.getType() != XSignType.BigXSign) {
    			//set to XSign
    			sign.setType(XSignType.XSign);
    			
    			//split the String into the 2 Parts
    			String[] splitString = lines[i].replaceAll("<|>","").split("\\|",2);
    			
    			//save Lines    			
        		newLines[0][i] = splitString[0];
        		newLines[1][i] = splitString[1];
    		}
    		else if(lines[i].matches("^<.*\\|$")) {
    			sign.setType(XSignType.HalfXSign);
    			
    			//replace the commandchars
    			String replacedString = lines[i].replaceAll("<|\\|", "");
    			
    			//save Lines
    			newLines[0][i] = replacedString;
    		}
    		else if(lines[i].matches("^\\|.*>$") || lines[i].matches("^\\|.*\\|$")) {
    			//Is there a half sign? 			
    			if(plugin.getXSign(block) != null) {
    				//get the half sign
    				sign = plugin.getXSign(block);
    				
    				//set the signtype
    				if(lines[i].matches("^\\|.*>$"))
    					sign.setType(XSignType.BigXSign);
    				else if(lines[i].matches("^\\|.*\\|$"))
    					sign.setType(XSignType.TriggerXSign);
        			
        			//replace the commandchars
        			String replacedString = lines[i].replaceAll(">|\\|", "");
        			
    				//set the newLines
    				newLines[0][i] = replacedString;
    			}
    			else {
    				sign.setType(XSignType.Sign);
    				this.player.sendMessage("No half XSign found");
    			}
    		}
    		else if(lines[i].matches("^\\[counter\\]$")) {
    			sign.setType(XSignType.XSignCounter);
    			newLines[0][i] = lines[i]+":"+0;
    		}
    		else {
    			newLines[0][i] = newLines[1][i] = lines[i];
    		}
    	}
    	  	
       	//Save XSign
    	if(sign.getType() == XSignType.XSign) {
    		this.player.sendMessage("XSign created");
        	sign.addState(newLines[0]);
        	sign.addState(newLines[1]);
    		plugin.addSign(event.getBlock(), sign);
    	}
    	else if(sign.getType() == XSignType.HalfXSign) {
    		saveAndAddSign(sign,newLines[0],block,"half XSign created");
    	}
    	else if(sign.getType() == XSignType.TriggerXSign) {
    		saveAndAddSign(sign,newLines[0],block,"TXSign step created");
    	}
    	else if(sign.getType() == XSignType.BigXSign) {
    		saveAndAddSign(sign,newLines[0],block,"Big XSign created");
    	}
    	else if(sign.getType() == XSignType.XSignCounter) {
    		saveAndAddSign(sign,newLines[0],block,"Counter XSign created");
    	}
    	
    }
    
    private void saveAndAddSign(XSign sign, String[] lines, Block block, String info) {
    	sign.addState(lines);
		this.player.sendMessage(info);
		plugin.addSign(block, sign);
    }
}
