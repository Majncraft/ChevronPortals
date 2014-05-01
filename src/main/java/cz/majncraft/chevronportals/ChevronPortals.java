package cz.majncraft.chevronportals;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;


import cz.majncraft.chevronportals.AddressBook;


public class ChevronPortals extends JavaPlugin implements Listener {
	
	public static ChevronPortals instance;
	public static boolean active=true;
	public static boolean debug=false;
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
    public void onEntityPortal(EntityPortalEvent event1)
    {
    	if(event1.getEntity()==null)
    		return;
    	UnitedPortalEvent ev=new UnitedPortalEvent(event1);
    	if(!chevronFinder(ev))
    	{
    		gateHandler(ev);
    	}
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerPortal(PlayerPortalEvent event1)
    {
    	if(debug) this.getLogger().info("PlayerPortalEvent cast");
    	UnitedPortalEvent ev=new UnitedPortalEvent(event1);
    	if(!chevronFinder(ev))
    	{
    		if(gateHandler(ev))
    		{
            	if(debug) this.getLogger().info("PlayerPortalEvent cast - gate handler");
    	    		ChevronWorld b=AddressBook.readAddress(event1.getTo().getWorld().getName());
    	    		event1.getPlayer().sendMessage(ConfigHandler.lng.get("gate.transfer")+(b.getName().equals("NONE")? b.getWorld().getName():b.getName()));
    		}
    	}
    	else
    	{
        	if(debug) this.getLogger().info("PlayerPortalEvent cast - chevron finded");
    		ChevronWorld b=AddressBook.readAddress(event1.getTo().getWorld().getName());
    		event1.getPlayer().sendMessage(ConfigHandler.lng.get("gate.transfer")+(b.getName().equals("NONE")? b.getWorld().getName():b.getName()));
    	}
    }
    private boolean gateHandler(UnitedPortalEvent event1)
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
    
    private boolean chevronFinder(UnitedPortalEvent event1)
    {
    	if(debug) this.getLogger().info("Teleport cause:"+event1.getCause().toString());
    	if(!active||event1==null || event1.getCause()!=TeleportCause.NETHER_PORTAL || !AddressBook.readAddress(event1.getFrom().getWorld().getName()).canDial())
    	{
    		return false;
    	}
    	Block p1=event1.getFrom().getBlock();
    	if(debug) this.getLogger().info("Block p1:"+p1.getType()+" viva versum "+Material.PORTAL);
    	if(debug) this.getLogger().info("Block p1C:"+p1.getType().name()+" viva versum "+Material.PORTAL.name());
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

    	if(debug) this.getLogger().info("World ref:"+event1.getFrom().getWorld());
    	if(p2==null)
    		return false;
    	if(debug) this.getLogger().info("Block p2:"+p2.getType()+" viva versum "+Material.PORTAL);
    	if(debug) this.getLogger().info("Block p2C:"+p2.getType().name()+" viva versum "+Material.PORTAL.name());
    	int x=p1.getX()-p2.getX();
    	int z=p1.getZ()-p2.getZ();
    	for(ChevronWorld s:AddressBook.getWorlds())
    	{
    		if(event1.getFrom().getWorld().getBlockAt(p1.getX()+x,p1.getY()-1,p1.getZ()+z).getType().toString().equals(s.getGateMaterial()))
    		{
    			if(event1.getFrom().getWorld().getBlockAt(p1.getX()+x,p1.getY()+3,p1.getZ()+z).getType().toString().equals(s.getGateMaterial()) && event1.getFrom().getWorld().getBlockAt(p2.getX()-x,p1.getY()-1,p2.getZ()-z).getType().toString().equals(s.getGateMaterial()) && event1.getFrom().getWorld().getBlockAt(p2.getX()-x,p1.getY()+3,p2.getZ()-z).getType().toString().equals(s.getGateMaterial()))
    			{
    				if(s.getWorld().getName()==event1.getFrom().getWorld().getName())
    				{
    			    	if(debug) this.getLogger().info("Gate connection, sending now.");
    				event1.setTo(s.projection(event1.getFrom()));
    				event1.useTravelAgent(true);
    	    		return true;
    				}
    				else
    					return false;
    			}
    		}
    	}
		return false;
    }
    private String getGate(UnitedPortalEvent event1)
    {
    	int[][] s=new int[][]{{0,0,1,-1},{-1,1,0,0}};
    	Block b=event1.getFrom().getBlock();
    	//Find nearest portal block
    	if(event1.getFrom().getBlock().getType()!=Material.PORTAL)
    	{
    		//for()
    	}
    	
    	return "";
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
	   else if(d[0].equals("scan") && a.hasPermission("chevronportals.op.scan"))
	   {
		   ConfigHandler.scanWorlds();
		   a.sendMessage("ChevronPortals - scan complete.");
	   }
	   else if(d[0].equals("reload") && a.hasPermission("chevronportals.op.reload"))
	   {
		   a.sendMessage(ConfigHandler.lng.get("commands.reload"));
		   AddressBook.Clear();
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
