package cz.majncraft.chevronportals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
	public static String mainworld;
	@Override
    public void onLoad(){
        
    	getLogger().info("Started");
    }
	
   
    @Override
    public void onEnable(){
    	instance=this;
        Init();
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
        ConfigHandler.load(this.getDataFolder());
        
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerPortal(PlayerPortalEvent event1)
    {
    	if(!chevronFinder(event1))
    	{
    		if(gateHandler(event1))
    		{
    	    		ChevronWorld b=AddressBook.readAddress(event1.getTo().getWorld().getName());
    	    		event1.getPlayer().sendMessage(ConfigHandler.lng.get("gate.transfer")+(b.getName().equals("NONE")? b.getWorld().getName():b.getName()));
    		}
    	}
    	else
    	{
    		ChevronWorld b=AddressBook.readAddress(event1.getTo().getWorld().getName());
    		event1.getPlayer().sendMessage(ConfigHandler.lng.get("gate.transfer")+(b.getName().equals("NONE")? b.getWorld().getName():b.getName()));
    	}
    }
    private boolean easteregg=true;
    private boolean gateHandler(PlayerPortalEvent event1)
    {
    	if(AddressBook.readAddress(event1.getFrom().getWorld().getName()).isHandled())
    	{
        	ChevronWorld b=AddressBook.readAddress(ChevronPortals.mainworld);
			event1.setTo(b.projection(event1.getFrom()));
			event1.useTravelAgent(true);
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean chevronFinder(PlayerPortalEvent event1)
    {
    	if(event1.getPlayer().getDisplayName().equals("NepsterCZ") && easteregg)
    	{
    		event1.getPlayer().sendMessage("====== NepsterCZ detekovan ======");
    		event1.getPlayer().sendMessage("|IRIS uzavren. Brana nepruchozi.|");
    		event1.getPlayer().sendMessage("|      NETHER je jen jeden!     |");
    		event1.getPlayer().sendMessage("=================================");
    		event1.getPlayer().damage(0.5);
    		return false;
    	}
    	if(!active||event1==null || event1.getCause()!=TeleportCause.NETHER_PORTAL || !AddressBook.readAddress(event1.getFrom().getWorld().getName()).canDial())
    	{
    		return false;
    	}
    	Block p1=event1.getFrom().getBlock();
    	while(true)
    	{
    		if(event1.getFrom().getWorld().getBlockAt(p1.getX(),p1.getY()-1,p1.getZ()).getType()==Material.PORTAL)
    		{
    			p1=event1.getFrom().getWorld().getBlockAt(p1.getX(),p1.getY()-1,p1.getZ());
    		}
    		else
    			break;
    	}
    	Block p2=null;
    	if(event1.getFrom().getWorld().getBlockAt(p1.getX()+1, p1.getY(), p1.getZ()).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX()+1, p1.getY(), p1.getZ());
    	else if(event1.getFrom().getWorld().getBlockAt(p1.getX()-1, p1.getY(), p1.getZ()).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX()-1, p1.getY(), p1.getZ());
    	else if(event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()+1).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()+1);
    	else if(event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()-1).getType()==Material.PORTAL)
    		p2=event1.getFrom().getWorld().getBlockAt(p1.getX(), p1.getY(), p1.getZ()-1);
    	if(p2==null)
    		return false;
    	int x=p1.getX()-p2.getX();
    	int z=p1.getZ()-p2.getZ();
    	for(ChevronWorld s:AddressBook.getWorlds())
    	{
    		if(event1.getFrom().getWorld().getBlockAt(p1.getX()+x,p1.getY()-1,p1.getZ()+z).getType().toString().equals(s.getGateMaterial()))
    		{
    			if(event1.getFrom().getWorld().getBlockAt(p1.getX()+x,p1.getY()+3,p1.getZ()+z).getType().toString().equals(s.getGateMaterial()) && event1.getFrom().getWorld().getBlockAt(p2.getX()-x,p1.getY()-1,p2.getZ()-z).getType().toString().equals(s.getGateMaterial()) && event1.getFrom().getWorld().getBlockAt(p2.getX()-x,p1.getY()+3,p2.getZ()-z).getType().toString().equals(s.getGateMaterial()))
    			{
    				event1.setTo(s.projection(event1.getFrom()));
    				event1.useTravelAgent(true);
    	    		return true;
    			}
    		}
    	}
		return false;
    }
   @Override
   public boolean onCommand(CommandSender a, Command b, String c, String[] d)
   {
	   if(!c.toLowerCase().equals("chevron") && !c.toLowerCase().equals("chevronportals"))
		   return false;
	   if(d.length==0)
	   {
		   a.sendMessage("ChevronPortals");
		   a.sendMessage("==============================");
		   a.sendMessage(ConfigHandler.lng.get("commands.infoscan"));
		   a.sendMessage(ConfigHandler.lng.get("commands.infomain"));
		   a.sendMessage(ConfigHandler.lng.get("commands.infoinfo"));
		   a.sendMessage(ConfigHandler.lng.get("commands.infoworld"));
	   }
	   else if(d[0].equals("scan") && a.hasPermission("chevronportals.mod.scan"))
	   {
		   ConfigHandler.scanWorlds();
		   a.sendMessage("ChevronPortals - scan complete.");
	   }
	   else if(d[0].equals("reload") && a.hasPermission("chevronportals.op.reload"))
	   {
		   a.sendMessage(ConfigHandler.lng.get("commands.reload"));
		   Init();
		   a.sendMessage(ConfigHandler.lng.get("commands.reload2"));
	   }
	   else if(d[0].equals("info") && d.length==2)
	   {
		   if(AddressBook.containsWorld(d[1]))
		   {
			   
		   }
		   else
			   a.sendMessage(ConfigHandler.lng.get("commands.infoNo"));
			   
	   }
	   return false;
				   
   }
}
