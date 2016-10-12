package com.queryio.demo.mr.generic.parser;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;

import com.queryio.plugin.datatags.UserDefinedTag;

public class TagDataBean implements Writable {

	private static final Log LOG = LogFactory.getLog(TagDataBean.class);

	private List<UserDefinedTag> tags;
	private boolean updateSchema;
    private boolean isError;
	public TagDataBean() {
	}

	public TagDataBean(List<UserDefinedTag> tags, boolean updateSchema, boolean isError) {
		this.tags = tags;
		this.updateSchema = updateSchema;
		this.isError = isError;
	}

	public List<UserDefinedTag> getTags() {
		return tags;
	}

	public void setTags(List<UserDefinedTag> tags) {
		this.tags = tags;
	}

	public boolean isUpdateSchema() {
		return updateSchema;
	}

	public void setUpdateSchema(boolean updateSchema) {
		this.updateSchema = updateSchema;
	}
	
	public void setIsError(boolean isError)
	{
		this.isError = isError; 
	}
	
	public boolean isError()
	{
		return this.isError;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		
		int size = (tags==null)?0:tags.size();
		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			UserDefinedTag tag = tags.get(i);
			out.writeUTF(tag.getKey());
			Object value = tag.getValue();
			LOG.info("Value : "+value);
			out.writeUTF(value.getClass().getCanonicalName());
			if (value instanceof String) {
				out.writeUTF(String.valueOf(value));
			} else if (value instanceof Double) {
				out.writeDouble((Double) value);
			} else if (value instanceof Long) {
				out.writeLong((Long) value);
			} else if (value instanceof Boolean) {
				out.writeBoolean((Boolean) value);
			} else if (value instanceof Integer) {
				out.writeInt((Integer) value);
			}
			//out.writeUTF(type);
		}
		out.writeBoolean(this.updateSchema);
		out.writeBoolean(this.isError);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		String cls = null;
		try {
			this.tags = new ArrayList<UserDefinedTag>();
			int size = in.readInt();
			for (int i = 0; i < size; i++) {
				String key = in.readUTF();
				cls = in.readUTF();
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(cls);
				Object value;
				if (c == Integer.class)
					value = in.readInt();
				else if (c == Long.class)
					value = in.readLong();
				else if (c == Boolean.class)
					value = in.readBoolean();
				else if (c == Double.class)
					value = in.readDouble();
				else
					value = in.readUTF();
				UserDefinedTag t = new UserDefinedTag(key, value);
				this.tags.add(t);
			}
			this.updateSchema = in.readBoolean();
			this.isError = in.readBoolean();
		} catch (ClassNotFoundException e) {
			LOG.error("Failed to read fields for class : " + cls, e);
			throw new IOException(e);
		}

	}
}
