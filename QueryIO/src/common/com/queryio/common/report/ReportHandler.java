package com.queryio.common.report;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;

import com.queryio.common.util.AppLogger;

public class ReportHandler {
	private static IReportEngine reportEngine;
	private static IDesignEngine designEngine;

	static {
		DesignConfig designConfig = new DesignConfig();
		EngineConfig engineConfig = new EngineConfig();

		try {
			Platform.startup(designConfig);
			Platform.startup(engineConfig);

			IDesignEngineFactory designFactory = (IDesignEngineFactory) Platform
					.createFactoryObject(IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY);
			designEngine = designFactory.createDesignEngine(designConfig);

			IReportEngineFactory reportFactory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			reportEngine = reportFactory.createReportEngine(engineConfig);
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		}
	}

	public static IReportEngine getReportEngine() {
		return reportEngine;
	}

	public static IDesignEngine getDesignEngine() {
		return designEngine;
	}

	public static void destroyEngines() {
		reportEngine.destroy();
	}
}
