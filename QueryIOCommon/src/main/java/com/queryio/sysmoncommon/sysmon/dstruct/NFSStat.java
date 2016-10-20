/**
 * 
 */
package com.queryio.sysmoncommon.sysmon.dstruct;

import java.util.HashMap;
import java.util.Map;

public class NFSStat
{
	private Map clientStats = new HashMap();
	private Map serverStats = new HashMap();

	private Map rawClientStats = new HashMap();
	private Map rawServerStats = new HashMap();

	public void setClientStatsValue(String stats, String key, Number value) 
	{
		this.setValue(rawClientStats, clientStats, stats, key, value);
	}

	public void setServerStatsValue(String stats, String key, Number value) 
	{
		this.setValue(rawServerStats, serverStats, stats, key, value);
	}
	
	public void setValue(Map rawStatsMap, Map statsMap, String stats, String key, Number value) 
	{
		Map map1 = (Map) rawStatsMap.get(stats);
		if (map1 == null) 
		{
			map1 = new HashMap();
			rawStatsMap.put(stats, map1);
		}

		Map map2 = (Map) statsMap.get(stats);
		if (map2 == null) 
		{
			map2 = new HashMap();
			statsMap.put(stats, map2);
		}
		Number prevValue = (Number)map1.put(key, value);
		map2.put(key, new Integer(prevValue != null ? (int)(value.longValue() - prevValue.longValue()):0));
	}

	public Map getClientStats() 
	{
		return clientStats;
	}

	public Map getServerStats() 
	{
		return serverStats;
	}

}
