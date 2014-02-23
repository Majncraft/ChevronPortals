package cz.majncraft.chevronportals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {

	private static File data;
	public static void load(File data)
	{
		ConfigHandler.data=data;
		firstrun();
		loadWorlds();
		loadLng();
		
	}
	public static void loadWorlds()
	{
		File f = new File(data + "/worlds.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        config.set("mainworld", "world");
        try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
        ChevronPortals.instance.mainworld=config.getString("mainworld");
        if(config.contains("worlds"))
        for(String a:config.getConfigurationSection("worlds").getKeys(false))
        {
        	String c1="NONE";
        	if(config.isSet("worlds."+a+".GateMaterial"))
        		c1=config.getString("worlds."+a+".GateMaterial");
        	float c2=1;
        	if(config.isSet("worlds."+a+".Multiplier"))
        		c2=config.getInt("worlds."+a+".Multiplier");
        	Boolean c3=false;
        	if(config.isSet("worlds."+a+".isNether"))
        		c3=config.getBoolean("worlds."+a+".isNether");
        	Boolean c4=true;
        	if(config.isSet("worlds."+a+".autoGateCreating"))
        		c4=config.getBoolean("worlds."+a+".autoGateCreating");
        	Boolean c5=true;
        	if(config.isSet("worlds."+a+".canDial"))
        		c5=config.getBoolean("worlds."+a+".canDial");
        	Boolean c6=false;
        	if(config.isSet("worlds."+a+".handleNormal"))
        		c6=config.getBoolean("worlds."+a+".handleNormal");

        	String c7="NONE";
        	if(config.isSet("worlds."+a+".DisplayName"))
        		c7=config.getString("worlds."+a+".DisplayName");
        	for(ChevronWorld d:AddressBook.getWorlds())
        	{
        		if(c1.equals(d.getGateMaterial()) && !c1.equals("NONE"))
        			c1="NONE";
        	}
        	AddressBook.writeAddress(a,new ChevronWorld(c7,a,c1 , c2, c3, c4, c5,c6));
        }
    	
		
	}
	public static void scanWorlds()
	{
		for(World a:Bukkit.getWorlds())
		{
			if(!AddressBook.containsWorld(a.getName()))
			{
				AddressBook.writeAddress(a.getName(), new ChevronWorld(a.getName(),a.getName(),"NONE",1,(a.getName().equals("world_nether")? true:false),false,false,false));
				File f = new File(data + "/worlds.yml");
		        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		        config.set("worlds."+a.getName()+".GateMaterial", "NONE");
		        config.set("worlds."+a.getName()+".Multiplier", 1);
		        config.set("worlds."+a.getName()+".isNether", (a.getName().equals("world_nether")? true:false));
		        config.set("worlds."+a.getName()+".autoGateCreating", false);
		        config.set("worlds."+a.getName()+".canDial", false);
		        config.set("worlds."+a.getName()+".handleNormal", false);
		        config.set("worlds."+a.getName()+".DisplayName", "NONE");
		        try {
					config.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static Map<String,String> lng=new HashMap<String,String>();
	public static void loadLng()
	{
		File f = new File(data + "/lang.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        for(String d:config.getKeys(true))
        {
        	lng.put(d, config.getString(d));
        }
	}
	private static void firstrun()
    {
    	File f = new File(data + "/");
    	if(!f.exists())
    	    f.mkdir();
    	f = new File(data + "/config.yml");
    	if(!f.exists())
    	{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	f = new File(data + "/lang.yml");
    	if(!f.exists())
    	{
			try {
				f.createNewFile();
		        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		        config.set("gate.transfer", "Teleportujes se do sveta ");
		        config.set("gate.beforeTransfer", "Brana do sveta ");
		        config.set("commands.infoscan", "/chevron scan - skenování bukkitu pro nezname svety");
		        config.set("commands.infomain", "/chevron mainworld {new mainworld} - nastaveni hlavního sveta");
		        config.set("commands.infoinfo", "/chevron info [world] - info o svete");
		        config.set("commands.infoworld", "/chevron set [world] [type] [data] - nastaveni vlastnosti svetu");
		        config.set("commands.inforeload", "/chevron reload - restart pluginu");
		        config.set("commands.reload", "ChevronPortals se restartuje");
		        config.set("commands.reload2", "ChevronPortals se restartoval");
		        config.set("commands.infoNo", "Zadne informace o tomto sveta. Pokud jste moderator/admin zkuste /chevron scan");
		        config.set("commands.info1", "Svet:");
		        config.set("commands.info2", "Blok chevronu:");
		        config.set("commands.info3", "Nasobic pozice:");
		        config.set("commands.info4", "Je to nether svìt:");
		        config.set("commands.info5", "Povolená automatická tvorba bran:");
		        config.set("commands.info6", "Muze vytacet chevrony:");
		        config.set("commands.info7", "Ovladani vanilla brany pres ChevronPortals:");
		        config.set("commands.info8", "Viditelny nazev:");
		        config.set("commands.setinfo", "1 - GateMaterial(string), 2 - Multiplier(float), 3 - nether(bool), 4 - autogatespawn(bool)"
		        		+ ", 5- canDial(bool), 6 - handleNormal");
		        config.set("commands.yes", "Ano");
		        config.set("commands.no", "Ne");
		        config.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	f = new File(data + "/langEN.yml");
    	if(!f.exists())
    	{
			try {
				f.createNewFile();
		        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		        config.set("gate.transfer", "Teleporting to world ");
		        config.set("gate.beforeTransfer", "Gate to world ");
		        config.set("commands.scaninfo", "");
		        config.set("commands.", "");
		        config.set("commands.", "");
		        config.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	f = new File(data + "/worlds.yml");
    	if(!f.exists())
    	{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
}
