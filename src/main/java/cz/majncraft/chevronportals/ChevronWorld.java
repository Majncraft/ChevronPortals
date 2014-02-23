package cz.majncraft.chevronportals;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class ChevronWorld {

	private boolean nether,creategate,handlenormal,dialing=false;
	private String gateMaterial=null;
	private float multiplier;
	private String world;
	private String name;
	public static ChevronWorld Default()
	{
		return new ChevronWorld("Overworld",ChevronPortals.mainworld, "NONE", 1, false, true, false,false);
	}
	public Location projection(Location arg0)
	{
		float d=this.multiplier/AddressBook.readAddress(arg0.getWorld().getName()).multiplier;
		int x=(int)Math.floor(arg0.getBlockX()*d);
		int z=(int)Math.floor( arg0.getBlockZ()*d);
		int y=((this.isNether() && arg0.getBlockY()>128)? 0:arg0.getBlockY() );
		return new Location(this.getWorld(),x,y,z);
	}
	public boolean isHandled()
	{
		return handlenormal;
	}
	public ChevronWorld(String name,String world,String gateMaterial,float multiplier,boolean nether,boolean creategate, boolean dialing, boolean handlenormal)
	{
		this.name=name;
		this.world=world;
		this.gateMaterial=gateMaterial;
		this.multiplier=multiplier;
		this.nether=nether;
		this.creategate=creategate;
		this.dialing=dialing;
		this.handlenormal=handlenormal;
	}
	public World getWorld()
	{
		return Bukkit.getWorld(world);
	}
	public float getMultiplier()
	{
		return multiplier;
	}
	public String getName()
	{
		return name;
	}
	public String getGateMaterial()
	{
		return gateMaterial;
	}
	public boolean isNether()
	{
		return nether;
	}
	public boolean canCreateGate()
	{
		return creategate;
	}
	public boolean canDial()
	{
		return dialing;
	}
}
