package orangenpresse.xsigns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * XSigns for Bukkit
 *
 * @author Orangenpresse
 */
public class XSigns extends JavaPlugin {
	private String name;
	private File saveFile;
    private final XSignsBlockListener blockListener = new XSignsBlockListener(this);
    private final HashMap<Block, XSign> signs = new HashMap<Block, XSign>();
    
    public void onEnable() {
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
        
        //Get Description and set variables
        PluginDescriptionFile pdfFile = this.getDescription();
        this.name = pdfFile.getName();
        saveFile = new File("plugins/"+this.name+"/data.sav");
        
        //check Files
        if(!checkFiles()) {
           	pm.disablePlugin(this);
           	return;
        }

		//load Signs
        getServer().getLogger().info( this.name + " info: " + loadSigns() + " XSigns loaded" );
			
        //write plugin info
        getServer().getLogger().info( this.name + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    public void onDisable() {
    	//save signs before disable
    	if(signs.size() != 0)
    		this.saveSigns();

        getServer().getLogger().info(this.name + " disabled");
    }
    
    public void addSign(Block block, XSign sign) {
    	//add Sign to hashmap
    	signs.put(block, sign);
    	
    	saveSigns();
    }
    
    public boolean isXSign(Block block) {
    	return signs.containsKey(block);
    }
    
    public void removeSign(Block block) {
    	signs.remove(block);
    	saveSigns();
    }
    
    public XSign getXSign(Block block) {
    	if(!signs.containsKey(block)) {
    		return null;
    	}
    	return signs.get(block);
    }
    
    private boolean checkFiles()
    {
        //try to load JSON Library for librarycheck
        try {
			Class.forName("flexjson.JSONSerializer");
		} catch (ClassNotFoundException e1) {
			getServer().getLogger().info(this.name + " error: flexjson not found!");
			e1.printStackTrace();
			return false;
		}
    	
    	//create|check Plugin folder
        File pluginDir = new File("plugins/"+this.name);

        if(!pluginDir.exists())
        {
        	if(pluginDir.mkdir()) {
        		getServer().getLogger().info(this.name + " info: create "+this.name+" plugin folder");
        	}
        	else {
        		getServer().getLogger().info(this.name +" error: can't create "+this.name+" plugin folder, Signs will not be saved!");
        		return false;
        	}
        }
        
        //create|check saveFile
        if(!saveFile.exists())
			try {
				saveFile.createNewFile();
				
				getServer().getLogger().info(this.name + " info: save file created");
			} catch (IOException e) {
				getServer().getLogger().info(this.name +" error: can't create "+this.name+" save file, Signs will not be saved!");
				e.printStackTrace();
				return false;
			}
			
		return true;
    }
    
    
    private void saveSigns() {
    	//open savefile
		try {
			FileWriter writer = new FileWriter(saveFile);
			
			//write data
	    	for(XSign sign : this.signs.values())
	    	{
	    		JSONSerializer seri = new JSONSerializer();
	    		
	    		//write Signs to file
	    		writer.write(seri.include("text").serialize(sign)+"\n");
	    	}
	    	
	    	writer.close();
	    	
		} catch (IOException e) {
			getServer().getLogger().info(this.name + " error: can't write save data");
			e.printStackTrace();
		}
    }
    
    private int loadSigns() {
    	int count = 0;
		
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(saveFile));
			Block block = null;
			World world = null;
			
			//read Data
			while(reader.ready())
			{
				//Deserialize Sign
				XSign sign = new JSONDeserializer<XSign>().deserialize(reader.readLine());
				
				if(sign.getEnvironment() != null)
					world = getServer().createWorld(sign.getWorld(),World.Environment.valueOf(sign.getEnvironment()));
				else
					world = getServer().getWorld(sign.getWorld());
					
				//get Block
				if(world != null)
				{
					block = world.getBlockAt(sign.getX(), sign.getY(), sign.getZ());
				}
				else
				{
					break;
				}
				
				//put sign in array if it a sign
				if(block != null && block.getType() == Material.SIGN_POST || block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN) {
					signs.put(block, sign);
					count++;
				}
				else
				{
					getServer().getLogger().info(this.name + " info: non existend XSign or unfinished Big XSign found. It will be deleted after building a new XSign");
				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			getServer().getLogger().info(this.name + " error: can't read save data");
			e.printStackTrace();
		} catch (IOException e) {
			getServer().getLogger().info(this.name + " error: can't read save data");
			e.printStackTrace();
		}
		
		return count;
    }
}

