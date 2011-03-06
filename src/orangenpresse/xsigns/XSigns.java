package orangenpresse.xsigns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Material;
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
    private final HashMap<Block, String[][]> signs = new HashMap<Block, String[][]>();
    
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
        System.out.println( this.name + " info: " + loadSigns() + " XSigns loaded" );
			
        //write plugin info
        System.out.println( this.name + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    
    public void onDisable() {
        System.out.println("XTuringSigns disabled");
    }
    
    public void addSign(Block sign, String[][] lines) {
    	//add Sign to hashmap
    	signs.put(sign, lines);
    	saveSigns();
    }
    
    public boolean isXSign(Block block) {
    	return signs.containsKey(block);
    }
    
    public void removeSign(Block block) {
    	signs.remove(block);
    	saveSigns();
    }
    
    public String[][] getString(Block sign) {
    	if(!signs.containsKey(sign)) {
    		return null;
    	}
    	String[][] temp = signs.get(sign);
    	return temp;
    }
    
    private boolean checkFiles()
    {
        //try to load JSON Library for librarycheck
        try {
			Class.forName("flexjson.JSONSerializer");
		} catch (ClassNotFoundException e1) {
			System.out.println(this.name + " error: flexjson not found!");
			e1.printStackTrace();
			return false;
		}
    	
    	//create|check Plugin folder
        File pluginDir = new File("plugins/"+this.name);

        if(!pluginDir.exists())
        {
        	if(pluginDir.mkdir()) {
        		System.out.println(this.name + " info: create "+this.name+" plugin folder");
        	}
        	else {
        		System.out.println(this.name +" error: can't create "+this.name+" plugin folder, Signs will not be saved!");
        		return false;
        	}
        }
        
        //create|check saveFile
        if(!saveFile.exists())
			try {
				saveFile.createNewFile();
				
				System.out.println(this.name + " info: save file created");
			} catch (IOException e) {
				System.out.println(this.name +" error: can't create "+this.name+" save file, Signs will not be saved!");
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
	    	for(Block block : signs.keySet())
	    	{
	    		XSign sign = new XSign(	block.getLocation().getWorld().getName(), 
	    								block.getLocation().getBlockX(), 
	    								block.getLocation().getBlockY(), 
	    								block.getLocation().getBlockZ(), 
	    								signs.get(block)
	    								);
	    		
	    		JSONSerializer seri = new JSONSerializer();
	    		
	    		//write Signs to file
	    		writer.write(seri.include("text").serialize(sign)+"\n");
	    	}
	    	
	    	writer.close();
	    	
		} catch (IOException e) {
			System.out.println(this.name + " error: can't write save data");
			e.printStackTrace();
		}
    }
    
    private int loadSigns() {
    	int count = 0;
    	
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(saveFile));
			
			//read Data
			while(reader.ready())
			{
				//Deserialize Sign
				XSign sign = new JSONDeserializer<XSign>().deserialize(reader.readLine());
				
				//get Block
				Block block = getServer().getWorld(sign.getWorld()).getBlockAt(sign.getX(), sign.getY(), sign.getZ());
				
				//put sign in array if it a sign
				if(block.getType() == Material.SIGN_POST || block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN) {
					signs.put(block, sign.getText());
					count++;
				}
				else
				{
					System.out.println(this.name + " info: non existend XSign found(world renamed or server crashed?). It will be deleted after building a new XSign one");
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println(this.name + " error: can't read save data");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(this.name + " error: can't read save data");
			e.printStackTrace();
		}
		
		//Save Signs (this will be delete not loaded signs)
		//saveSigns();
		
		return count;
    }
}

