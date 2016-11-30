package com.queryio.sysmoncommon.sysmon;

import java.sql.Types;

public interface SystemMonitorConstants {
	String ROOT_NODE = "System Attributes";

	String CPU = "cpu";
	String CPU_TOTAL_USAGE = "Total CPU Usage";
	String CPU_USAGE = "CPU Usage";
	String CPU_SYSTEM_USAGE = "System CPU Usage";
	String CPU_WAIT_USAGE = "Wait CPU Usage";
	String CPU_USER_USAGE = "User CPU Usage";
	String CPU_LOAD_AVG1 = "Load Average(1 min)";
	String CPU_LOAD_AVG5 = "Load Average(5 mins)";
	String CPU_LOAD_AVG15 = "Load Average(15 mins)";
	String CPU_SUFFIX = "cpu";
	String CPU_TOTAL_USAGE_SUFFIX = "total_cpu_usage";
	String CPU_USAGE_SUFFIX = "cpu_usage";
	String CPU_SYSTEM_USAGE_SUFFIX = "system_cpu_usage";
	String CPU_WAIT_USAGE_SUFFIX = "wait_cpu_usage";
	String CPU_USER_USAGE_SUFFIX = "user_cpu_usage";
	String CPU_LOAD_AVG1_SUFFIX = "load_average_1_min";
	String CPU_LOAD_AVG5_SUFFIX = "load_average_5_mins";
	String CPU_LOAD_AVG15_SUFFIX = "load_average_15_mins";

	String NETWORK = "network interface";
	String NETWORK_TOTAL_PACKETS_RECEIVED = "Total Packets Recd";
	String NETWORK_TOTAL_PACKETS_SENT = "Total Packets Sent";
	String NETWORK_PACKETS_RECEIVED = "Packets Recd";
	String NETWORK_PACKETS_SENT = "Packets Sent";
	String NETWORK_TOTAL_DATA_RECEIVED = "Total Network Data Received";
	String NETWORK_TOTAL_DATA_SENT = "Total Network Data Sent";
	String NETWORK_UTILIZATION = "Network Utilization";
	String NETWORK_KB_RECEIVED = "KB Recd";
	String NETWORK_KB_SENT = "KB Sent";
	String NETWORK_SUFFIX = "network_interface";
	String NETWORK_TOTAL_PACKETS_RECEIVED_SUFFIX = "total_packets_recd";
	String NETWORK_TOTAL_PACKETS_SENT_SUFFIX = "total_packets_sent";
	String NETWORK_PACKETS_RECEIVED_SUFFIX = "packets_recd";
	String NETWORK_PACKETS_SENT_SUFFIX = "packets_sent";
	String NETWORK_TOTAL_DATA_RECEIVED_SUFFIX = "total_network_data_received";
	String NETWORK_TOTAL_DATA_SENT_SUFFIX = "total_network_data_sent";
	String NETWORK_UTILIZATION_SUFFIX = "network_utilization";
	String NETWORK_KB_RECEIVED_SUFFIX = "kb_recd";
	String NETWORK_KB_SENT_SUFFIX = "kb_sent";
	// Column name prefix and suffix and
	String NAME_ID_SEPERATOR = "#";
	String NETWORK_COL_PREFIX = "NetworkInterface_interface";
	String NETWORK_RECEIVED_SUFIX = "_PacketsRecd";
	String NETWORK_SENT_SUFIX = "_PacketsSent";

	String DISK = "physical disk";
	String TOTAL_DISK_READS = "Total Disk Reads";
	String TOTAL_DISK_WRITES = "Total Disk Writes";
	String DISK_READS = "Disk Reads";
	String DISK_WRITES = "Disk Writes";
	String DISK_SUFFIX = "physical_disk";
	String TOTAL_DISK_READS_SUFFIX = "total_disk_reads";
	String TOTAL_DISK_WRITES_SUFFIX = "total_disk_writes";
	String DISK_READS_SUFFIX = "disk_reads";
	String DISK_WRITES_SUFFIX = "disk_writes";
	// Column name prefix and suffix and
	String DISK_COL_PREFIX = "PhysicalDisk_disk";
	String DISK_READS_SUFIX = "_DiskReads";
	String DISK_WRITES_SUFIX = "_DiskWrites";

	String DRIVE = "logical disk";
	String TOTAL_SPACE = "Total Space";
	String TOTAL_USED_SPACE = "Total Used Space";
	String USED_SPACE = "Used Space";
	String SPACE = "Space";
	String DRIVE_SUFFIX = "logical_disk";
	String TOTAL_SPACE_SUFFIX = "total_space";
	String TOTAL_USED_SPACE_SUFFIX = "total_used_space";
	String SPACE_SUFFIX = "space";
	String USED_SPACE_SUFFIX = "used_space";
	// Column name prefix and suffix and
	String DRIVE_COL_PREFIX = "drive";
	String DRIVE_TOTAL_SUFIX = "_TotalSpace";
	String DRIVE_USED_SUFIX = "_UsedSpace";

	String PHYSICAL_MEMORY = "physical memory";
	String PHYSICAL_MEMORY_TOTAL = "Total Physical Memory";
	String PHYSICAL_MEMORY_USED = "Used Physical Memory";
	String PHYSICAL_MEMORY_AVAILABLE = "Available Physical Memory";
	String PHYSICAL_MEMORY_SUFFIX = "physical_memory";
	String PHYSICAL_MEMORY_TOTAL_SUFFIX = "total_physical_memory";
	String PHYSICAL_MEMORY_USED_SUFFIX = "used_physical_memory";
	String PHYSICAL_MEMORY_AVAILABLE_SUFFIX = "available_physical_memory";
	String VIRTUAL_MEMORY = "virtual memory";
	String VIRTUAL_MEMORY_TOTAL = "Total Virtual Memory";
	String VIRTUAL_MEMORY_USED = "Used Virtual Memory";
	String VIRTUAL_MEMORY_SUFFIX = "virtual_memory";
	String VIRTUAL_MEMORY_TOTAL_SUFFIX = "total_virtual_memory";
	String VIRTUAL_MEMORY_USED_SUFFIX = "used_virtual_memory";

	String PROCESSES = "processes";
	String PROCESSES_SUFFIX = "processes";

	String PROCESS_NAME = "Process Name";
	String PROCESS_ID = "Process Id";
	String MEMORY_USAGE = "Memory Usage";
	String PROCESSOR_TIME = "Processor Time";
	String PROCESSOR_THT = "Thread Count";
	String PROCESSOR_OWNER = "Owner";
	String PROCESS_NAME_SUFFIX = "process_name";
	String PROCESS_ID_SUFFIX = "process_id";
	String MEMORY_USAGE_SUFFIX = "memory_usage";
	String PROCESSOR_TIME_SUFFIX = "processor_time";
	String PROCESSOR_THT_SUFFIX = "thread_count";
	String PROCESSOR_OWNER_SUFFIX = "owner";
	String[] PROCESS_COLUMNS = { PROCESS_ID, PROCESS_NAME, MEMORY_USAGE, PROCESSOR_TIME };
	int[] PROCESS_COL_TYPES = { Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.DOUBLE };

	String[] PROCESS_COLUMNS_WITH_OWNER = { PROCESS_ID, PROCESS_NAME, MEMORY_USAGE, PROCESSOR_TIME, PROCESSOR_OWNER };
	int[] PROCESS_COL_TYPES_WITH_OWNER = { Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.DOUBLE, Types.VARCHAR };

	// Data Units
	// String NETWORK_RATE = "packet per sec";
	String NETWORK_RATE_KB = "KB per sec";
	String DISK_RATE = "KB per sec";
	String DISK_SPACE_UNIT = "MB";

	String SEPARATOR = "+";
	String SEPARATOR_REGEX = "\\+";
	String SEPARATOR_DISPLAY = ".";
	String START_SEPARATOR_DISPLAY = " (";
	String END_SEPARATOR_DISPLAY = ")";
	String UNDERSCORE = "_";

	/*
	 * Attributes specific to MAC
	 */
	String NFS_CLIENT = "nfs client";
	String NFS_CLIENT_SUFFIX = "nfs_client";
}
