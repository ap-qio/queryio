package com.queryio.core.monitor.events;

public abstract class NodeEvent extends BaseEvent
{
	private final String nodeId;
	
	public NodeEvent(final String nodeId, final long timeStamp, final EventDispatcher dispatcher)
	{
		super(timeStamp, dispatcher);
		this.nodeId = nodeId;
	}

	public String getNodeId()
	{
		return this.nodeId;
	}
}
