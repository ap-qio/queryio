-injars <obfuscate.build>/originalJars/QueryIOUI.jar
-outjars <obfuscate.build>/newJars/QueryIOUI.jar

-injars <obfuscate.build>/originalJars/QueryIOCommon.jar
-outjars <obfuscate.build>/newJars/QueryIOCommon.jar

-injars <obfuscate.build>/originalJars/QueryIOAgent.jar
-outjars <obfuscate.build>/newJars/QueryIOAgent.jar

-injars <obfuscate.build>/originalJars/QueryIOCommonAgent.jar
-outjars <obfuscate.build>/newJars/QueryIOCommonAgent.jar

-injars <obfuscate.build>/originalJars/hdfs-over-ftp.jar
-outjars <obfuscate.build>/newJars/hdfs-over-ftp.jar

-injars <obfuscate.build>/originalJars/os3server.jar
-outjars <obfuscate.build>/newJars/os3server.jar

-injars <obfuscate.build>/originalJars/QueryIOPlugins.jar
-outjars <obfuscate.build>/newJars/QueryIOPlugins.jar


-keepattributes LineNumberTable, SourceFile

#dont obfuscate
-keep class org.apache.hadoop.hdfs.server.blockmanagement.QueryIOBlockPlacementPolicy { public *; }

-keep class com.queryio.userdefinedtags.common.DatabaseFunctions { public *; }

-keep class com.queryio.userdefinedtags.common.ExecuteTagParser { public *; }

-keep class com.queryio.userdefinedtags.common.UserDefinedTagDAO { public *; }

-keep class com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory { public *; }

-keep class com.queryio.userdefinedtags.common.UserDefinedTagUtils { public *; }

-keep class com.queryio.plugins.utils.EncryptionHandler { public *; }

-keep class com.queryio.plugin.dstruct.ColumnMetaData { public *; }

-keep class com.queryio.plugin.dstruct.IDataDefinition { public *; }

-keep class com.queryio.plugin.dstruct.IDataManager { public *; }

-keep class com.queryio.plugin.dstruct.IQueryProcessor { public *; }

-keep class com.queryio.plugin.groupinfo.GroupInfoContainer { public *; }

-keep class com.queryio.plugin.groupinfo.GroupInfoUpdater { public *; }

-keep class com.queryio.plugin.groupinfo.QueryIOGroupInfoServiceProvider { public *; }

-keep class com.queryio.plugin.datatags.ColumnMetadata { public *; }

-keep class com.queryio.plugin.datatags.IUserDefinedParser { public *; }

-keep class com.queryio.plugin.datatags.TableMetadata { public *; }

-keep class com.queryio.plugin.datatags.UserDefinedTag { public *; }



#CLASSES TO KEEP

-keep public class com.queryio.web.listener.StartupListener { public *; }

-keep public class com.queryio.common.database.DDLDownloadServlet { public *; }

-keep public class com.queryio.common.servlet.ServerLogViewer { public *; }

-keep public class com.queryio.config.db.DBConnectionManager { public *; }

-keep public class com.queryio.core.dataanalyzer.DataAnalyzerManager { *; }

-keep public class com.queryio.common.servlet.AddUserServlet { public *; }

-keep public class com.queryio.file.download.FileDownload { public *; }

-keep public class com.queryio.core.jobs.SubmitJob { public *; }

-keep public class com.queryio.file.upload.TagParserFileUpload { public *; }

-keep public class com.queryio.core.permissions.GroupInformationProvider { public *; }

-keep public class com.queryio.file.upload.FileUpload { public *; }

-keep public class com.queryio.core.databrowser.DataBrowserManager { *; }

-keep public class com.queryio.common.report.GenerateReportServlet { public *; }
 
-keep class * implements java.io.Serializable { public *; }

-keep public class com.queryio.core.notifier.notifiers.impl.MCEmailNotifier { public *; }

-keep public class com.queryio.core.notifier.notifiers.impl.CustomProgramNotifier { public *; }

-keep public class com.queryio.core.notifier.notifiers.impl.LogNotifier { public *; }

-keep public class com.queryio.core.notifier.notifiers.impl.DatabaseNotifier { public *; }

-keep public class com.queryio.core.notifier.notifiers.impl.MCSmsNotifier { public *; }

-keep public class com.queryio.config.db.DBConfigBean { *; }

-keep public class com.queryio.core.monitor.controllers.ControllerAttribute { public *; }

-keep public class com.queryio.core.bean.BigQuery { *; }

-keep public class com.queryio.core.bean.BillingReportInfo { *; }

-keep public class com.queryio.core.bean.CheckpointNode { *; }

-keep public class com.queryio.core.bean.CustomTagDatabase { *; }

-keep public class com.queryio.core.bean.DWRResponse { *; }

-keep public class com.queryio.core.bean.DashboardCell { *; }

-keep public class com.queryio.core.bean.DiskMonitoredData { *; }

-keep public class com.queryio.core.bean.HAStatus { *; }

-keep public class com.queryio.core.bean.HadoopConfig { *; }

-keep public class com.queryio.core.bean.HadoopService { *; }

-keep public class com.queryio.core.bean.Host { *; }

-keep public class com.queryio.core.bean.MapRedJobConfig { *; }

-keep public class com.queryio.core.bean.MigrationInfo { *; }

-keep public class com.queryio.core.bean.Node { *; }

-keep public class com.queryio.core.bean.NotifyBean { *; }

-keep public class com.queryio.core.bean.QueryExecution { *; }

-keep public class com.queryio.core.bean.QueryIOService { *; }

-keep public class com.queryio.core.bean.RuleBean { *; }

-keep public class com.queryio.core.bean.RuleItem { *; }

-keep public class com.queryio.core.bean.Snapshot { *; }

-keep public class com.queryio.core.bean.Status { *; }

-keep public class com.queryio.core.bean.StatusTreeBean { *; }

-keep public class com.queryio.core.bean.TagParser { *; }

-keep public class com.queryio.core.bean.TagParserConfig { *; }

-keep public class com.queryio.core.bean.TreeBean { *; }

-keep public class com.queryio.core.bean.User { *; }

-keep public class com.queryio.core.bean.Volume { *; }

-keep public class com.queryio.core.monitor.charts.ChartData { *; }

-keep public class com.queryio.core.monitor.beans.AttributeData { *; }

-keep public class com.queryio.core.monitor.beans.LiveAttribute { *; }

-keep public class com.queryio.core.monitor.beans.MonitorData { *; }

-keep public class com.queryio.core.monitor.beans.Parameter { *; }

-keep public class com.queryio.core.monitor.beans.SummaryTable { *; }

-keep public class com.queryio.core.monitor.beans.SummaryAttribute { *; }

-keep public class com.queryio.core.monitor.charts.Series { public *; }

-keep public class com.queryio.core.monitor.dstruct.Rule { public *; }

-keep public class com.queryio.core.monitor.dstruct.RuleExpression { public *; }

-keep public class com.queryio.core.monitor.alerts.evaluator.AlertEvaluationManager { public *; }

-keep public class com.queryio.core.monitor.dstruct.Alert { public *; }

-keep public class com.queryio.scheduler.service.SchedulerBean { public *; }

-keep public class com.queryio.scheduler.service.QuerySchedulerBean { public *; }

-keep public class com.queryio.scheduler.service.TriggerDetailBean { public *; }

-keep public class com.queryio.core.conf.RemoteManager { public *; }

-keep public class com.queryio.core.conf.MigrationManager { public *; }

-keep public class com.queryio.core.monitor.managers.ChartManager { public *; }

-keep public class com.queryio.core.monitor.managers.SummaryManager { public *; }

-keep public class com.queryio.core.applications.ApplicationManager { public *; }

-keep public class com.queryio.core.monitor.managers.StatusManager { public *; }

-keep public class com.queryio.core.monitor.controllers.ControllerManager { public *; }

-keep public class com.queryio.core.reports.ReportManager { public *; }

-keep public class com.queryio.core.notification.NotificationSettingsManager { public *; }

-keep public class com.queryio.core.monitor.managers.RuleManager { public *; }

-keep public class com.queryio.core.monitor.managers.AlertManager { public *; }

-keep public class com.queryio.scheduler.service.ScheduleManager { public *; }

-keep public class com.queryio.core.snapshots.SnapshotManager { public *; }

-keep public class com.queryio.queryioservices.QueryIOServicesManager { public *; }

-keep public class com.queryio.core.customtags.BigQueryManager { public *; }

-keep public class com.queryio.core.billing.BillingManager { public *; }

-keep public class com.queryio.core.permissions.PermissionsManager { public *; }

-keep public class com.queryio.config.db.DBConfigManager { public *; }

-keep public abstract class * 


###QueryIO AGENT###

-keep public class * {
    public static void main(java.lang.String[]);
}

-keep public class com.queryio.core.services.queryioServiceImpl { public *; }

-keep public class  com.queryio.common.service.remote.queryioService { public *; }

-keep public class  com.queryio.agent.web.LogViewer { public *; }

-keep public class  com.queryio.web.listener.AgentStartupListener { public *; }

###hdfs-over-ftp.jar

-keep public class  com.queryio.ftpserver.userinfo.UserInfoUpdater { public *; }

-keep public class  com.queryio.db.reinit.ReinitializeDBServer { public *; }

-keep public class  com.queryio.servlets.ReinitializeHadoopConf { public *; }

-keep public class  com.queryio.servlets.StatusProvider { public *; }

-keep public class  com.queryio.ftpserver.listeners.StartupListener { public *; }

###os3server.jar

-keep public class com.os3.server.listeners.StartupListener { public *; }

-keep public class com.os3.server.servlets.OS3ActionServlet { public *; }

-keep public class com.os3.server.userinfo.UserInfoUpdater { public *; }

-keep public class com.queryio.servlets.StatusProvider { public *; }

###QueryIOCommon.jar

-keep public class com.queryio.common.database.DatabaseFunctions { public *; }

-keep public class com.queryio.common.database.CoreDBManager { public *; }

-keep public class com.queryio.common.util.SecurityHandler { public *; }

###hadoop-custom-compiled.jar

-keep public class org.apache.** { *; }

###LIBRARY JARS###

#QueryIO UI JARS
-libraryjars <queryio.lib.dir>/xercesImpl.jar
-libraryjars <queryio.lib.dir>/c3p0-0.9.1.1.jar
-libraryjars <queryio.lib.dir>/com.ibm.icu_4.4.2.v20110823.jar
-libraryjars <queryio.lib.dir>/com.lowagie.text_2.1.7.v201004222200.jar
-libraryjars <queryio.lib.dir>/commons-codec-1.6.jar
-libraryjars <queryio.lib.dir>/commons-fileupload-1.2.1.jar
-libraryjars <queryio.lib.dir>/commons-httpclient-3.1.jar
-libraryjars <queryio.lib.dir>/commons-io-2.1.jar
-libraryjars <queryio.lib.dir>/commons-lang-2.4.jar
-libraryjars <queryio.lib.dir>/derby.jar
-libraryjars <queryio.lib.dir>/dwr_3.0.RC2.jar
-libraryjars <queryio.lib.dir>/flute.jar
-libraryjars <queryio.lib.dir>/hadoop-yarn-api-2.0.2-alpha.jar
-libraryjars <queryio.lib.dir>/hadoop-yarn-common-2.0.2-alpha.jar
-libraryjars <queryio.lib.dir>/impression-1.0.1-alfa.jar
-libraryjars <queryio.lib.dir>/iText-2.1.6.jar
-libraryjars <queryio.lib.dir>/javax.wsdl_1.5.1.v201012040544.jar
-libraryjars <queryio.lib.dir>/javax.xml.stream_1.0.1.v201004272200.jar
-libraryjars <queryio.lib.dir>/JobStruct.jar
-libraryjars <queryio.lib.dir>/js.jar
-libraryjars <queryio.lib.dir>/json_simple-1.1.jar
-libraryjars <queryio.lib.dir>/log4j-1.2.15.jar
-libraryjars <queryio.lib.dir>/mail-1.4.1.jar
-libraryjars <queryio.lib.dir>/oncrpc-1.0.2.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.bridge_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.css_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.dom_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.dom.svg_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.ext.awt_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.parser_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.pdf_1.6.0.v201105071520.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.svggen_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.transcoder_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.util_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.util.gui_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.batik.xml_1.6.0.v201011041432.jar
-libraryjars <queryio.lib.dir>/org.apache.commons.codec_1.3.0.v201101211617.jar
-libraryjars <queryio.lib.dir>/org.apache.commons.logging_1.0.4.v201101211617.jar
-libraryjars <queryio.lib.dir>/org.apache.xerces_2.9.0.v201101211617.jar
-libraryjars <queryio.lib.dir>/org.apache.xml.resolver_1.2.0.v201005080400.jar
-libraryjars <queryio.lib.dir>/org.apache.xml.serializer_2.7.1.v201005080400.jar
-libraryjars <queryio.lib.dir>/org.eclipse.birt.runtime_4.2.1.v20120918-1113.jar
-libraryjars <queryio.lib.dir>/org.eclipse.core.contenttype_3.4.200.v20120523-2004.jar
-libraryjars <queryio.lib.dir>/org.eclipse.core.expressions_3.4.401.v20120627-124442.jar
-libraryjars <queryio.lib.dir>/org.eclipse.core.filesystem_1.3.200.v20120522-2012.jar
-libraryjars <queryio.lib.dir>/org.eclipse.core.jobs_3.5.300.v20120622-204750.jar
-libraryjars <queryio.lib.dir>/org.eclipse.core.resources_3.8.1.v20120802-154922.jar
-libraryjars <queryio.lib.dir>/org.eclipse.core.runtime_3.8.0.v20120521-2346.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity_1.2.6.v201208210832.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.apache.derby_1.0.102.v201107221459.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.apache.derby.dbdefinition_1.0.2.v201107221459.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.console.profile_1.0.10.v201109250955.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.db.generic_1.0.1.v201107221459.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.dbdefinition.genericJDBC_1.0.1.v201107221459.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.oda_3.3.3.v201110130935.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.oda.consumer_3.2.5.v201109151100.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.oda.design_3.3.5.v201204241156.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.oda.flatfile_3.1.3.v201209041005.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.oda.profile_3.2.8.v201209080429.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.connectivity.sqm.core_1.2.5.v201205240353.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.hsqldb_1.0.0.v201107221502.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.hsqldb.dbdefinition_1.0.0.v201107221502.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.ibm.db2.luw_1.0.2.v201107221502.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.ibm.db2.luw.dbdefinition_1.0.4.v201107221502.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.ibm.informix_1.0.1.v201107221502.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.ibm.informix.dbdefinition_1.0.4.v201107221502.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.msft.sqlserver_1.0.1.v201107221504.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.msft.sqlserver.dbdefinition_1.0.1.v201201240505.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.mysql_1.0.3.v201205252211.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.mysql.dbdefinition_1.0.4.v201109022331.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.oda.ws_1.2.4.v201203221631.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.oda.xml_1.2.3.v201112061438.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.oracle_1.0.0.v201107221506.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.oracle.dbdefinition_1.0.103.v201206010214.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.postgresql_1.1.1.v201205252207.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.enablement.postgresql.dbdefinition_1.0.2.v201110070445.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.modelbase.dbdefinition_1.0.2.v201107221519.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.modelbase.derby_1.0.0.v201107221519.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.modelbase.sql_1.0.6.v201208230744.jar
-libraryjars <queryio.lib.dir>/org.eclipse.datatools.modelbase.sql.query_1.1.3.v201208230742.jar
-libraryjars <queryio.lib.dir>/org.eclipse.emf_2.6.0.v20120917-0436.jar
-libraryjars <queryio.lib.dir>/org.eclipse.emf.common_2.8.0.v20120911-0500.jar
-libraryjars <queryio.lib.dir>/org.eclipse.emf.ecore_2.8.1.v20120911-0500.jar
-libraryjars <queryio.lib.dir>/org.eclipse.emf.ecore.change_2.8.0.v20120911-0500.jar
-libraryjars <queryio.lib.dir>/org.eclipse.emf.ecore.xmi_2.8.0.v20120911-0500.jar
-libraryjars <queryio.lib.dir>/org.eclipse.equinox.app_1.3.100.v20120522-1841.jar
-libraryjars <queryio.lib.dir>/org.eclipse.equinox.common_3.6.100.v20120522-1841.jar
-libraryjars <queryio.lib.dir>/org.eclipse.equinox.preferences_3.5.0.v20120522-1841.jar
-libraryjars <queryio.lib.dir>/org.eclipse.equinox.registry_3.5.200.v20120522-1841.jar
-libraryjars <queryio.lib.dir>/org.eclipse.osgi_3.8.1.v20120830-144521.jar
-libraryjars <queryio.lib.dir>/org.eclipse.osgi.services_3.3.100.v20120522-1822.jar
-libraryjars <queryio.lib.dir>/org.eclipse.update.configurator_3.3.200.v20120523-1752.jar
-libraryjars <queryio.lib.dir>/org.w3c.css.sac_1.3.0.v200805290154.jar
-libraryjars <queryio.lib.dir>/org.w3c.dom.smil_1.0.0.v200806040011.jar
-libraryjars <queryio.lib.dir>/org.w3c.dom.svg_1.1.0.v201011041433.jar
-libraryjars <queryio.lib.dir>/poi-3.5-beta5-20090219.jar
-libraryjars <queryio.lib.dir>/quartz-2.1.5.jar
-libraryjars <queryio.lib.dir>/smslib-3.3.3.jar
-libraryjars <queryio.lib.dir>/spring-2.5.4.jar
-libraryjars <queryio.lib.dir>/spring-modules-validation_0.9.jar
-libraryjars <queryio.lib.dir>/spring-security-core-2.0.2.jar
-libraryjars <queryio.lib.dir>/spring-security-taglibs-2.0.2.jar
-libraryjars <queryio.lib.dir>/spring-web_2.5.4.jar
-libraryjars <queryio.lib.dir>/spring-webmvc-2.5.4.jar
-libraryjars <queryio.lib.dir>/Tidy.jar
-libraryjars <queryio.lib.dir>/xercesImpl.jar
-libraryjars <hadoop.lib.dir>/guava-11.0.2.jar

#AGENT JARS
-libraryjars <queryioagent.lib.dir>/jregex-1.2_01.jar
-libraryjars <queryioagent.lib.dir>/jsch-0.1.44.jar
-libraryjars <queryio.lib.dir>/spring-2.5.4.jar
-libraryjars <queryio.lib.dir>/spring-modules-validation_0.9.jar
-libraryjars <queryio.lib.dir>/spring-security-core-2.0.2.jar
-libraryjars <queryio.lib.dir>/spring-security-taglibs-2.0.2.jar
-libraryjars <queryio.lib.dir>/spring-web_2.5.4.jar
-libraryjars <queryio.lib.dir>/spring-webmvc-2.5.4.jar

#DB JARS
-libraryjars <jdbc.lib.dir>/hsqldb-2_2_8.jar

#JAVA CLASSES

#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/alt-rt.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/charsets.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/dt.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/alt-string.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/jce.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/jsse.jar
#-libraryjars /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/ui.jar

#SERVLET JARS
-libraryjars <servlet.dependent.dir>/jsp-api.jar
-libraryjars <servlet.dependent.dir>/servlet-api.jar

#FTP Jars

-libraryjars <ftp.lib.dir>/ftplet-api-1.0.4.jar
-libraryjars <ftp.lib.dir>/ftpserver-core-1.0.4.jar
-libraryjars <ftp.lib.dir>/group_mapping_service.jar
-libraryjars <ftp.lib.dir>/jcl-over-slf4j-1.5.2.jar
-libraryjars <ftp.lib.dir>/jregex-1.2_01.jar
-libraryjars <ftp.lib.dir>/log4j-1.2.14.jar
-libraryjars <ftp.lib.dir>/log4j-1.2.15.jar
-libraryjars <ftp.lib.dir>/mina-core-2.0.0-RC1.jar
-libraryjars <ftp.lib.dir>/slf4j-api-1.5.6.jar
-libraryjars <ftp.lib.dir>/slf4j-log4j12-1.5.6.jar
-libraryjars <hadoop.lib.dir>/commons-net-3.1.jar

#Server Jars
-libraryjars <server.supported.dir>/commons-dbcp-1.2.2.jar
-libraryjars <server.supported.dir>/commons-pool-1.4.jar
-libraryjars <server.supported.dir>/tika-app-1.2-modified.jar
-libraryjars <server.supported.dir>/hadoop-custom-compiled.jar

#Jetty Jars
-libraryjars <jetty.supported.dir>/jetty-6.1.26.jar
-libraryjars <jetty.supported.dir>/jetty-util-6.1.26.jar