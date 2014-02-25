package cz.majncraft.chevronportals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddressBook {
	public static void writeAddress(String name,ChevronWorld world)
	{
		book.put(name, world);
	}
	private static Map<String,ChevronWorld> book=new HashMap<String,ChevronWorld>();
	public static ChevronWorld readAddress(String name)
	{
		if(book.containsKey(name))
		return book.get(name);
		else
			return ChevronWorld.Default();
	}
	public static boolean containsWorld(String name)
	{
		return book.containsKey(name);
	}
	public static Collection<ChevronWorld> getWorlds()
	{
		return book.values();
	}
	

}
