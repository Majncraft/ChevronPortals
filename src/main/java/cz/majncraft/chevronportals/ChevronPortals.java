package cz.majncraft.chevronportals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;

public class ChevronPortals extends JavaPlugin implements Listener {
	
	public static ChevronPortals instance;
	public static boolean active=true;
	private static String mainworld;
	private static Map<String,String> addressBook=new HashMap<String,String>();
	@Override
    public void onLoad(){
        Init();
        
    	getLogger().info("Started");
    }
	
   
    @Override
    public void onEnable(){
    	instance=this;
        getServer().getPluginManager().registerEvents(this, this);
    	active=true;
    	getLogger().info("Enabled");
    }
 
    @Override
    public void onDisable() {
    	getLogger().info("Disabled");
    	active=false;
    }
    private void Init()
    {
    	firstrun();
        File conf = new File(this.getDataFolder() + "/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(conf);
        if(!config.isSet("BaseConfig.MainWorld"))
        config.set("BaseConfig.MainWorld", "world");
        if(!config.isSet("Chevrons"))
        config.set("Chevrons."+Material.REDSTONE_BLOCK.toString(), "nether2");
        try {
			config.save(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
        mainworld=config.getString("BaseConfig.MainWorld");
        Set<String> a=config.getConfigurationSection("Chevrons").getKeys(false);
        for(String b:a)
        {
        	addressBook.put(b, config.getString("Chevrons."+b));
        }
        
        
        
    }
    private void firstrun()
    {
    	File f = new File(this.getDataFolder() + "/");
    	if(!f.exists())
    	    f.mkdir();
    	f = new File(this.getDataFolder() + "/config.yml");
    	if(!f.exists())
    	{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerPortal(PlayerPortalEvent event1)
    {
    	if(!active||event1==null || event1.getCause()!=TeleportCause.NETHER_PORTAL || !event1.getFrom().getWorld().getName().equals(mainworld))
    	{
    		return;
    	}
    	Block p1=event1.getFrom().getBlock();
    	Block p2=null;
    	if(event1.getFrom().getWorld().getBlockAt(p1.getX()+1, p1.getY(), p1.getZ()).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX()+1, p1.getY(), p1.getZ());
    	else if(event1.getFrom().getWorld().getBlockAt(p1.getX()-1, p1.getY(), p1.getZ()).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX()-1, p1.getY(), p1.getZ());
    	else if(event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()+1).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()+1);
    	else if(event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()-1).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()-1);
    	int x=p1.getX()-p2.getX();
    	int z=p1.getZ()-p2.getZ();
    	for(String s:addressBook.keySet())
    	{
    		if(event1.getFrom().getWorld().getBlockAt(p1.getX()+x,p1.getY()-1,p1.getZ()+z).getType().toString().equals(s))
    		{
    			if(event1.getFrom().getWorld().getBlockAt(p1.getX()+x,p1.getY()+3,p1.getZ()+z).getType().toString().equals(s) && event1.getFrom().getWorld().getBlockAt(p2.getX()-x,p1.getY()-1,p2.getZ()-z).getType().toString().equals(s) && event1.getFrom().getWorld().getBlockAt(p2.getX()-x,p1.getY()+3,p2.getZ()-z).getType().toString().equals(s))
    			{
    				event1.getTo().setWorld(Bukkit.getWorld(addressBook.get(s)));
    				event1.useTravelAgent(true);
    				break;
    			}
    		}
    	}
    }
   
}
