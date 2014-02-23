package cz.majncraft.chevronportals;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;

public class TravelingAgency implements TravelAgent {

	private Location from;
	private ChevronWorld to;
	public TravelingAgency(Location from, ChevronWorld to)
	{
		this.from=from;
	}
	@Override
	public boolean createPortal(Location arg0) {
		if(!to.canCreateGate())
			return false;
		int preX=(int)Math.floor(arg0.getBlockX()*(AddressBook.readAddress(arg0.toString()).getMultiplier() /to.getMultiplier()));
		int preZ=(int)Math.floor(arg0.getBlockX()*(AddressBook.readAddress(arg0.toString()).getMultiplier() /to.getMultiplier()));
		
		int x=1;
		int z=1;
		if(to.getWorld().getBlockAt(arg0.getBlockX()+1, arg0.getBlockY(), arg0.getBlockZ()).getType()==Material.PORTAL
		 && (to.getWorld().getBlockAt(arg0.getBlockX()-1, arg0.getBlockY(), arg0.getBlockZ()).getType()==Material.PORTAL))
		 {
			z=0;
		 }
		else
		{
			x=0;
		}
		Location out=freePortalSpace(preX,preZ,x,z);
		if(out==null)
		{
			out=new Location(to.getWorld(), preX, (((to.isNether() ?128 :256)<arg0.getBlockY())? 120: arg0.getBlockY()), preZ);
		}
		buildPortal();
		return true;
		
	}
	private void buildPortal()
	{
		
	}
	private Location freePortalSpace(int x,int z,int sx,int sz)
	{
		for(int px=x-16;px<=x+16;px=px+1)
		{
			for(int pz=z-16;pz<z+16;pz=pz+1)
			{
				for(int y=0;y<(to.isNether()? 128:256);y++)
				{
					if(to.getWorld().getBlockAt(x, y, z).getType()==Material.AIR)
					{
						if(to.getWorld().getBlockAt(x-1*sx, y, z-1*sz).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x+1*sx, y, z+1*sz).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x+2*sx, y, z+2*sz).getType()==Material.AIR
										&& to.getWorld().getBlockAt(x, y+1, z).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x-1*sx, y+1, z-1*sz).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x+1*sx, y+1, z+1*sz).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x+2*sx, y+1, z+2*sz).getType()==Material.AIR
										&& to.getWorld().getBlockAt(x, y+2, z).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x-1*sx, y+2, z-1*sz).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x+1*sx, y+2, z+1*sz).getType()==Material.AIR
								&& to.getWorld().getBlockAt(x+2*sx, y+2, z+2*sz).getType()==Material.AIR)
						{
							return new Location(to.getWorld(), x, y, z);
						}
							
							
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public Location findOrCreate(Location arg0) {
		Location out=findPortal(arg0);
		if(out==null && to.canCreateGate())
		{
			createPortal(arg0);
		}
		return out;
	}

	@Override
	public Location findPortal(Location arg0) {
		int preX=(int)Math.floor(arg0.getBlockX()*(AddressBook.readAddress(arg0.toString()).getMultiplier() / to.getMultiplier()));
		int preZ=(int)Math.floor(arg0.getBlockX()*(AddressBook.readAddress(arg0.toString()).getMultiplier() / to.getMultiplier()));
		int postX=Integer.MIN_VALUE;
		int postY=Integer.MIN_VALUE;
		int postZ=Integer.MIN_VALUE;
		for(int x=preX-128;x<=preX+128;x++)
		{
			for(int z=preZ-128;z<=preZ+128;z++)
			{
				for(int y=0;y<((to.isNether()) ? 128 : 256);y++)
				{
					if(to.getWorld().getBlockAt(x,y,z).getType()==Material.PORTAL)
					{
						y=y+3;
						if((preX-postX)*(preX-postX)+(preZ-postZ)*(preZ-postZ)>=(preX-x)*(preX-x)+(preZ-z)*(preZ-z))
						{
							postX=x;
							postY=y;
							postZ=z;
						}
					}
				}
			}
		}
		if(postZ==Integer.MIN_VALUE)
		return null;
		else
			return new Location(to.getWorld(), postX, postY, postZ);
	}

	@Override
	public boolean getCanCreatePortal() {
		// TODO Auto-generated method stub
		return to.canCreateGate();
	}

	@Override
	public int getCreationRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSearchRadius() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCanCreatePortal(boolean arg0) {
		
	}

	@Override
	public TravelAgent setCreationRadius(int arg0) {
		return null;
	}

	@Override
	public TravelAgent setSearchRadius(int arg0) {
		return null;
	}

}
