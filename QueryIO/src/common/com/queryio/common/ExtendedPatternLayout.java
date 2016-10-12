package com.queryio.common;

import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class ExtendedPatternLayout extends PatternLayout 
{
	private StringBuffer buffer;
	private int exceptionLevel = 1;

	public ExtendedPatternLayout() 
	{
		super();
		buffer = new StringBuffer(256);
	}

	public ExtendedPatternLayout(String pattern) 
	{
		super(pattern);
		buffer = new StringBuffer(256);
	}
	
	public boolean ignoresThrowable() 
	{
		return (exceptionLevel == 2);
	}
	
	public String format(LoggingEvent event) 
	{
		if (buffer.capacity() > 1024)
		{
			buffer = new StringBuffer(256);
		}
		else
		{
			buffer.setLength(0);
		}
		buffer.append(super.format(event));
		
		if (exceptionLevel == 1)
		{
			String s[] = event.getThrowableStrRep();
			if (s != null) 
			{
				int len = s.length;
				boolean breakAferAppend = false;
				for (int i = 0; i < len; i++) 
				{
					breakAferAppend = false;
					if (i >= 1)
					{
						if (s[i].indexOf("com.appperfect.") != -1)
						{
							breakAferAppend = true;
						}
						else if (i > 1)
						{
							continue;
						}
					}
					buffer.append(s[i]);
					buffer.append(Layout.LINE_SEP);
					if (breakAferAppend)
					{
						break;
					}
				}
			}
		}
		return buffer.toString();
	}

	public void setExceptionLevel(String execptionLevel) 
	{
		if ("none".equalsIgnoreCase(execptionLevel))
		{
			this.exceptionLevel = 0;
		}
		else if ("all".equalsIgnoreCase(execptionLevel))
		{
			this.exceptionLevel = 2;
		}
		else
		{
			this.exceptionLevel = 1;
		}
	}

}
