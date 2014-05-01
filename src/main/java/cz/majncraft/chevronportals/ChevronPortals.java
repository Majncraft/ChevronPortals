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
    	if(!active||event1==null || event1.getCause()!=TeleportCause.NETHER_PORTAL || !AddressBook.readAddress(event1.getFrom().getWorld().getName()).canDial())
    	{
    		return false;
    	}
    	String dd=getGate(event1);
    	for(ChevronWorld s:AddressBook.getWorlds())
    	{
    		if(dd.equals(s.getGateMaterial()))
    		{
		    		if(debug) this.getLogger().info(s.getWorld().getName()+" w "+event1.getFrom().getWorld().getName());
    				if(AddressBook.readAddress(event1.getFrom().getWorld().getName()).canDial())
    				{
    				event1.setTo(s.projection(event1.getFrom()));
    				event1.useTravelAgent(true);
    	    		return true;
    				}
    				else
    					return false;
    			}
    		}
		return false;
    }
    private String getGate(UnitedPortalEvent event1)
    {
    	int[][] s=new int[][]{{0,0,1,-1},{-1,1,0,0}};
    	Block b=event1.getFrom().getBlock();
    	int x=b.getX(); int y=b.getY(); int z=b.getZ();
    	//Find nearest portal block
    	if(event1.getFrom().getBlock().getType()!=Material.PORTAL)
    	{
    		for(int i=0;i<4;i++)
    		{
    			b=event1.getFrom().getWorld().getBlockAt(x+s[0][i], y,z+s[1][i] );
    			if(b.getType()==Material.PORTAL)
    				{x+=s[0][i]; z+=s[1][i];break;}
    		}
    		if(b.getType()!=Material.PORTAL)
    			return "";
    	}
    	//Get siding
    	Block c=event1.getFrom().getWorld().getBlockAt(x+1,y,z); Block d=event1.getFrom().getWorld().getBlockAt(x-1,y,z);
    	int[] e=new int[]{0,0};
    	if(c.getType()==Material.PORTAL || d.getType()==Material.PORTAL)
    	{
    		e[0]=1;
    	}
    	else
    	{
    		c=event1.getFrom().getWorld().getBlockAt(x,y,z+1); d=event1.getFrom().getWorld().getBlockAt(x,y,z-1);
        	if(c.getType()==Material.PORTAL || d.getType()==Material.PORTAL)
        	{
        		e[1]=1;
        	}
        	else 
        		return "";
    	}
    	//Get chevron
    	Block[] ch=new Block[4];
    	c=d=b;
    	while(true)
    	{
    		d=c;
    		c=event1.getFrom().getWorld().getBlockAt(c.getX()+e[0],y,c.getZ()+e[1]);
    		if(c.getType()!=Material.PORTAL)
    		{	b=c=d;
    			while(true)
    			{
    				d=c;
    	    		c=event1.getFrom().getWorld().getBlockAt(c.getX(),c.getY()+1,c.getZ());
    	    		if(c.getType()!=Material.PORTAL)
    	    			{ch[0]=event1.getFrom().getWorld().getBlockAt(c.getX()+e[0],c.getY(),c.getZ()+e[1]); break;}
    			}
    			while(true)
    			{
    				d=c;
    	    		c=event1.getFrom().getWorld().getBlockAt(c.getX(),c.getY()-1,c.getZ());
    	    		if(c.getType()!=Material.PORTAL)
    	    			{ch[1]=event1.getFrom().getWorld().getBlockAt(c.getX()+e[0],c.getY(),c.getZ()+e[1]); break;}
    			}
    			break;
    		}
    	}
    	while(true)
    	{
    		d=c;
    		c=event1.getFrom().getWorld().getBlockAt(c.getX()-e[0],y,c.getZ()-e[1]);
    		if(c.getType()!=Material.PORTAL)
    		{	b=c=d;
    			while(true)
    			{
    				d=c;
    	    		c=event1.getFrom().getWorld().getBlockAt(c.getX(),c.getY()+1,c.getZ());
    	    		if(c.getType()!=Material.PORTAL)
    	    			{ch[2]=event1.getFrom().getWorld().getBlockAt(c.getX()-e[0],c.getY(),c.getZ()-e[1]); break;}
    			}
    			while(true)
    			{
    				d=c;
    	    		c=event1.getFrom().getWorld().getBlockAt(c.getX(),c.getY()-1,c.getZ());
    	    		if(c.getType()!=Material.PORTAL)
    	    			{ch[3]=event1.getFrom().getWorld().getBlockAt(c.getX()-e[0],c.getY(),c.getZ()-e[1]); break;}
    			}
    			break;
    		}
    	}
    	//Same check
    	if(ch[0].getType()==ch[1].getType()&& ch[0].getType()==ch[2].getType()&& ch[0].getType()==ch[2].getType())
    		return ch[0].getType().toString();
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
