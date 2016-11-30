package com.queryio.common.dstruct;

public class AIXProcessInfo extends ProcessInfo {
	private static final long serialVersionUID = -5548974919469483270L;

	private long lUpTime;
	private long lCPUTime;
	private boolean first;

	public AIXProcessInfo(String name, int id, int memUsage, int threadCount, long lUpTime, long lCPUTime) {
		super(name, id, memUsage, 0.0f, memUsage, threadCount);
		this.lUpTime = lUpTime;
		this.lCPUTime = lCPUTime;
		first = true;
	}

	public void update(long lNewUpTime, long lNewCPUTime, int noOfProcessors, int iMemoryUsage, int iThreadCount) {
		if (lUpTime > 0) {
			if (first) {
				// For the first time it will show CPU usage since process
				// start.
				// I think its better than showing ZERO.
				first = false;
				final long divisor = lUpTime * noOfProcessors;
				if (divisor > 0) {
					fProcessorTime = Math.min(100F, (lCPUTime * 100) / (divisor * 1f));
				}
			} else {
				final long elapsedCpu = (lNewCPUTime - lCPUTime) * 100;
				final long elapsedTime = lNewUpTime - lUpTime;
				final long divisor = elapsedTime * noOfProcessors;
				if (divisor > 0) {
					fProcessorTime = Math.min(100F, elapsedCpu / (divisor * 1f));
				}
			}
		}
		lUpTime = lNewUpTime;
		lCPUTime = lNewCPUTime;
		this.iMemoryUsage = iMemoryUsage;
		this.iThreadCount = iThreadCount;
	}

	public long getUpTime() {
		return lUpTime;
	}

	public long getCPUTime() {
		return lCPUTime;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ProcessInfo) {
			return (this.iProcessId == ((ProcessInfo) obj).iProcessId);
		}
		return false;
	}

}
