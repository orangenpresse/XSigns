package orangenpresse.xsigns;

import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.block.CraftBlock;

public class Block extends CraftBlock {

	public Block(CraftChunk chunk, int x, int y, int z) {
		super(chunk, x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		CraftBlock block = (CraftBlock)o;
		
		if( block.getChunk().equals(block.getChunk()) &&
			block.getX() == block.getX() &&
			block.getY() == block.getY() &&
			block.getZ() == block.getZ()
		) 
			return true;
		else
			return false;
		

	}

	
}
