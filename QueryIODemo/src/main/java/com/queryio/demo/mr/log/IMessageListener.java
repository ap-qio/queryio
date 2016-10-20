package com.queryio.demo.mr.log;

import java.io.IOException;
import java.sql.SQLException;

public interface IMessageListener {
	void messageComplete() throws IOException, InterruptedException, SQLException;
}
