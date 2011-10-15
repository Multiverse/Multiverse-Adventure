package me.main__.MultiverseAdventure;

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
		super(world.getCBWorld(), MultiverseAdventure.getInstance().getCore().getConfig(),
				MultiverseAdventure.getInstance().getCore(), world.getSeed(), null);
	}

}
