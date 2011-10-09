package me.main__.MultiverseAdventureWorlds;

import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class MVAdventureWorld extends MVWorld {

	public MVAdventureWorld(World world, Configuration config,
			MultiverseCore instance, Long seed, String generatorString) {
		super(world, config, instance, seed, generatorString);
		// TODO Auto-generated constructor stub
	}
	
	public MVAdventureWorld(MVWorld world) {
		super(world.getCBWorld(), MultiverseAdventureWorlds.getInstance().getCore().getConfig(),
				MultiverseAdventureWorlds.getInstance().getCore(), world.getSeed(), null);
	}

}
