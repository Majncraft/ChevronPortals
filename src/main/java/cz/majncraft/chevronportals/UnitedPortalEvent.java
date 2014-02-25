package cz.majncraft.chevronportals;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class UnitedPortalEvent {
	private boolean type;
	private Event event;
	public UnitedPortalEvent(PlayerPortalEvent event)
	{
		this.event=event;
		type=true;
	}
	public UnitedPortalEvent(EntityPortalEvent event)
	{
		this.event=event;
		type=false;
	}
	public TeleportCause getCause()
	{
		if(type)
			return ((PlayerPortalEvent)event).getCause();
		return TeleportCause.NETHER_PORTAL;
	}
	public Location getFrom()
	{
		if(type)
			return ((PlayerPortalEvent)event).getFrom();
		else
			return ((EntityPortalEvent)event).getFrom();
	}
	public Location getTo()
	{
		if(type)
			return ((PlayerPortalEvent)event).getTo();
		else
			return ((EntityPortalEvent)event).getTo();
	}
	public void useTravelAgent(boolean a)
	{
		if(type)
			((PlayerPortalEvent)event).useTravelAgent(a);
		else
			((EntityPortalEvent)event).useTravelAgent(a);
	}
	public void setTo(Location a)
	{

		if(type)
			((PlayerPortalEvent)event).setTo(a);
		else
			((EntityPortalEvent)event).setTo(a);
	}

}
