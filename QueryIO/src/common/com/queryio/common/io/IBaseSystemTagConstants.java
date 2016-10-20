/*
 * @(#)  IBaseSystemTagConstants.java
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.io;

/**
 * This interface used to define the tag names to be used in the System XML
 * file.
 * 
 * @author Exceed Consultancy Services.
 * @version 1.0
 */
public interface IBaseSystemTagConstants
{
	// System Related Constants
	String SYSTEM = "System"; //$NON-NLS-1$
	String GENERAL = "General"; //$NON-NLS-1$
	String WIDTH = "Width"; //$NON-NLS-1$
	String HEIGHT = "Height"; //$NON-NLS-1$
	String HIGHLIGHTONMOUSEMOVE = "HighlightOnMouseMove"; //$NON-NLS-1$
	String STATUSBAR = "Statusbar"; //$NON-NLS-1$

	String OUTPUT_WINDOW = "OutputWindow"; //$NON-NLS-1$
	String LEFT_PANE_WIDTH = "LeftPaneWidth"; //$NON-NLS-1$
	String X_POSITION = "X"; //$NON-NLS-1$
	String Y_POSITION = "Y"; //$NON-NLS-1$
	String RESULT_SAVE_TYPE = "Result_Save_Type"; //$NON-NLS-1$
	String NUM_MRU = "Num_MRU"; //$NON-NLS-1$
	String NUM_RESULT_MRU = "Num_Result_MRU"; //$NON-NLS-1$

	String BROWSERS = "Browsers"; //$NON-NLS-1$
	String BROWSER = "Browser"; //$NON-NLS-1$
	String IE_BROWSER = "IEBrowser"; //$NON-NLS-1$
	String APPLICATION_FONT = "ApplicationFont"; //$NON-NLS-1$
	String USING_DEFAULT = "UsingDefault"; //$NON-NLS-1$
	String JDKS = "JDKS"; //$NON-NLS-1$
	String JDK = "JDK"; //$NON-NLS-1$
	String EDITOR = "Editor"; //$NON-NLS-1$
	String PATH = "Path"; //$NON-NLS-1$
	String DEVICE_ID = "DeviceId"; //$NON-NLS-1$
	String DEVICE_ORIENTATION = "DeviceOrientation"; //$NON-NLS-1$
	String MDS_HOME = "MDSHome"; //$NON-NLS-1$
	String MODEL = "Model"; //$NON-NLS-1$
	String DEFAULT = "Default"; //$NON-NLS-1$
	String DOCUMENT_ROOT = "DocumentRoot"; //$NON-NLS-1$
	String NAME = "Name"; //$NON-NLS-1$
	String VALUE = "Value"; //$NON-NLS-1$
	String VENDOR = "Vendor"; //$NON-NLS-1$
	String VERSION = "Version"; //$NON-NLS-1$

	String RHINO_CLASSPATH = "RhinoClasspath"; //$NON-NLS-1$
	String RHINO_CLASSPATH_ENTRY = "RhinoClasspathEntry"; //$NON-NLS-1$
	String ENVIRONMENT = "Environment"; //$NON-NLS-1$
	String CLASSPATH = "CLASSPATH"; //$NON-NLS-1$
	String CLASS_PATH = "CLASS-PATH"; //$NON-NLS-1$
	String LIBRARYPATH = "LIBRARYPATH"; //$NON-NLS-1$
	String LIBRARY_PATH = "LIBRARY-PATH"; //$NON-NLS-1$
	String JVMARGUMENTS = "JVMArguments"; //$NON-NLS-1$

	String GARBAGE = "GarbageCollector";//$NON-NLS-1$
	String RUN_GARBAGE = "RunGarbageCollector";//$NON-NLS-1$
	String GARBAGE_THRESHOLD = "GarbageThresholdinMB";//$NON-NLS-1$
	String GARBAGE_THRESHOLD_POLL = "GarbageThresholdPollTimeinMinutes";//$NON-NLS-1$
	// String APPSERVERS = "AppServers";
	// String APPSERVER = "AppServer";
	String APPSERVER_PROPERTY = "AppServerProperty"; //$NON-NLS-1$
	String APPSERVER_STARTUP_FILE = "StartupFilePath"; //$NON-NLS-1$
	String APPSERVER_CONFIG_FILE = "ConfigFilePath"; //$NON-NLS-1$
	String APPSERVER_INSTANCE_NAME = "InstanceName"; //$NON-NLS-1$
	String APPSERVER_LOCATION = "ServerLocation"; //$NON-NLS-1$

	String MRU = "MRU"; //$NON-NLS-1$
	String FILE = "File"; //$NON-NLS-1$
	String MRU_RESULT = "MRUResult"; //$NON-NLS-1$

	String LAST_OPENED_PROJECT_FOLDER = "LastOpenedProjectFolder"; //$NON-NLS-1$

	String AUTO_BUILDING = "AutoBuilding"; //$NON-NLS-1$

	String COMPILATION_PORT = "CompilationPort"; //$NON-NLS-1$

	String PRODUCT_INFO = "ProductInfo"; //$NON-NLS-1$

	String PAGE_SETUP = "PageSetup"; //$NON-NLS-1$
	String PAPER_SIZE = "PaperSize"; //$NON-NLS-1$
	String ORIENTATION = "Orientation"; //$NON-NLS-1$
	String PAGE_DIMENSIONS = "PageDimensions"; //$NON-NLS-1$
	String PAGE_MARGINS = "PageMargins"; //$NON-NLS-1$
	String PAGE_WIDTH = "PageWidth"; //$NON-NLS-1$
	String PAGE_HEIGHT = "PageHeight"; //$NON-NLS-1$
	String LEFT_MARGIN = "LeftMargin"; //$NON-NLS-1$
	String RIGHT_MARGIN = "RightMargin"; //$NON-NLS-1$
	String TOP_MARGIN = "TopMargin"; //$NON-NLS-1$
	String BOTTOM_MARGIN = "BottomMargin"; //$NON-NLS-1$
	String PAGE_BORDER = "PageBorder"; //$NON-NLS-1$
	String BORDER_SIZE = "BorderSize"; //$NON-NLS-1$
	String BORDER_COLOR = "BorderColor"; //$NON-NLS-1$
	String PAGE_NUMBER = "PageNumber"; //$NON-NLS-1$
	String ZONE = "Zone"; //$NON-NLS-1$
	String FORMAT = "Format"; //$NON-NLS-1$
	String FONT = "Font"; //$NON-NLS-1$
	String FONT_NAME = "FontName"; //$NON-NLS-1$
	String FONT_STYLE = "FontStyle"; //$NON-NLS-1$
	String FONT_SIZE = "FontSize"; //$NON-NLS-1$
	String FONT_EFFECTS = "FontEffects"; //$NON-NLS-1$
	String FONT_FGCOLOR = "FontForeColor"; //$NON-NLS-1$
	String FONT_BGCOLOR = "FontBackColor"; //$NON-NLS-1$
	String FONT_ALT_FGCOLOR = "FontAltForeColor"; //$NON-NLS-1$
	String FONT_ALT_BGCOLOR = "FontAltBackColor"; //$NON-NLS-1$

	String DEFAULT_PROPERTIES = "DefaultProperties"; //$NON-NLS-1$
	String ZONE_NAME = "ZoneName"; //$NON-NLS-1$
	String ZONE_PROPERTIES = "ZoneProperties"; //$NON-NLS-1$
	String VERTICAL_ALIGNMENT = "VerticalAlignment"; //$NON-NLS-1$
	String HORIZONTAL_ALIGNMENT = "HorizontalAlignment"; //$NON-NLS-1$

	String IS_ASCENDING = "IsAscending"; //$NON-NLS-1$

	String IS_SELECTED = "IsSelected"; //$NON-NLS-1$
	String SORT_ON_COL_INDEX = "SortOnColIndex"; //$NON-NLS-1$
	String BITMAP = "BitMap"; //$NON-NLS-1$

	String JDBC_CONNECTION_SETTINGS = "ConnectionSetting"; //$NON-NLS-1$
	String JDBC_CONNECTION = "Connection"; //$NON-NLS-1$
	String JDBC_DATABASE_ID = "DatabaseID"; //$NON-NLS-1$
	String JDBC_DATABASE_NAME = "DatabaseName"; //$NON-NLS-1$
	String JDBC_DRIVER_NAME = "DriverName"; //$NON-NLS-1$
	String JDBC_CONNECTION_URL = "ConnectionURL"; //$NON-NLS-1$
	String JDBC_USERNAME = "Username"; //$NON-NLS-1$
	String JDBC_PASSWORD = "Password"; //$NON-NLS-1$
	String JDBC_TIMEOUT = "TimeOut"; //$NON-NLS-1$
	String JDBC_DRIVER_PATH = "DriverPath"; //$NON-NLS-1$

	// AppProducts
	String ANALYTICS = "Anlaytics"; //$NON-NLS-1$
	String UNIT_TESTER = "UnitTester"; //$NON-NLS-1$
	String LOAD_TESTER = "LoadTester"; //$NON-NLS-1$
	String JAVA_PROFILER = "JavaProfiler"; //$NON-NLS-1$
	String FUNCTIONAL_TESTER = "FunctionalTester"; //$NON-NLS-1$
	String WIN_FUNCTIONAL_TESTER = "WindowsFunctionalTester"; //$NON-NLS-1$
	String CODE_ANALYZER = "CodeAnalyzer"; //$NON-NLS-1$
	String CODE_COVERAGE = "CodeCoverage"; //$NON-NLS-1$

	// UNIT TESTER
	String EXECUTION_PORT = "ExecutionPort"; //$NON-NLS-1$
	String CC_PORT_FOR_JAVA = "JavaCCPort"; //$NON-NLS-1$
	String CC_PORT_FOR_JSP = "ServerCCPort"; //$NON-NLS-1$
	String PORT_FOR_REPOSITORY = "RepositoryPort"; //$NON-NLS-1$
	String PORT_FOR_TEST_CLASS_GENERATION = "TestClassPort"; //$NON-NLS-1$

	String SHOW_RENDERING_VALIDATION = "ShowRenderingValidation"; //$NON-NLS-1$
	String APPSERVER_SETTINGS = "AppServer"; //$NON-NLS-1$
	String PROXY_SETTINGS = "ProxySettings"; //$NON-NLS-1$
	String APPSERVER_PROXYSET = "AppServerProxySet"; //$NON-NLS-1$
	String APPSERVER_PROXYPORT = "AppServerProxyPort"; //$NON-NLS-1$
	String PROXY_HOST = "ProxyHost"; //$NON-NLS-1$
	String PROXY_PORT = "ProxyPort"; //$NON-NLS-1$
	String HTTPS_PROXY_HOST = "HttpsProxyHost"; //$NON-NLS-1$
	String HTTPS_PROXY_PORT = "HttpsProxyPort"; //$NON-NLS-1$
	String USE_SAME_PROXY = "UseSameProxy"; //$NON-NLS-1$
	String BYPASS_PROXY_FOR_LOCAL_ADDRESS = "BypassProxyForLocalAddress"; //$NON-NLS-1$
	String ADDRESSES_WITHOUT_PROXY = "AddressWithoutProxy"; //$NON-NLS-1$
	String ENABLE_PROXY = "EnableProxy"; //$NON-NLS-1$
	String SSL_CERTIFICATE_PATH = "SSLCertificatePath"; //$NON-NLS-1$
	String PROXY_FILTER = "ProxyFilter"; //$NON-NLS-1$

	String REPOSITORY = "Repository"; //$NON-NLS-1$
	String REPORTS = "Reports"; //$NON-NLS-1$
	String SHOW_STACK_TRACE = "ShowStackTrace"; //$NON-NLS-1$
	String TESTCASEALLCOMBINATIONS = "TestCasesWithAllCombinations"; //$NON-NLS-1$
	String OBJECTALLCOMBINATIONS = "ObjectsWithAllCombinations"; //$NON-NLS-1$
	String AUTOUPDATE = "AutoUpdate"; //$NON-NLS-1$
	String TESTCLASSSETTINGS = "TestClassSettings"; //$NON-NLS-1$
	String METHODEXECUTION = "MethodExecution"; //$NON-NLS-1$
	String TESTCASEWITHNULLVALUES = "TestCaseWithNullValues"; //$NON-NLS-1$
	String EXECUTIONTIMEOUT = "ExecutionTimeOut"; //$NON-NLS-1$
	String MEMORYLEAKAGE = "MemoryLeakage"; //$NON-NLS-1$
	String IMPROVECODECOVERAGE = "ImproveCodeCoverage"; //$NON-NLS-1$
	String TESTCASECUTOFF = "TestCaseCutOff"; //$NON-NLS-1$
	String CODECOVERAGECUTOFF = "CodeCoverageCutOff"; //$NON-NLS-1$
	String GENERATESTUBS = "GenerateStubs";
	String SHOWOBJCREATIONDLG = "ShowObjectCreationDialog";
	// CODE ANALYZER
	String RULES_TREE_VIEW = "RulesTreeView"; //$NON-NLS-1$
	String IS_RULES_TREE_CATEGORY_WISE = "IsTreeCategoryWise"; //$NON-NLS-1$

	String SYNTAX_ERROR_SETTINGS = "SyntaxErrorSettings"; //$NON-NLS-1$
	String SHOWERROR = "SHOWERROR"; //$NON-NLS-1$
	String ACTIONONSYNTAXERROR = "ACTIONONSYNTAXERROR"; //$NON-NLS-1$
	String ASSERT = "Assert"; //$NON-NLS-1$
	String DOES_RECOGNIZE_ASSERT = "DoesRecognizeAssert"; //$NON-NLS-1$
	String AUTOFIXFORMATTING = "AutoFixFormatting"; //$NON-NLS-1$
	String INSERT_NEWLINE = "InsertNewLine"; //$NON-NLS-1$
	String COMPACT_ASSIGNMENT = "CompactAssignment"; //$NON-NLS-1$
	String INDENT_TAB = "IndentTab"; //$NON-NLS-1$
	String INDENT_SPACES = "IndentSpaces"; //$NON-NLS-1$

	// FUNCTIONAL TESTER
	String USE_SERVER = "UseServer"; //$NON-NLS-1$
	String PORT = "Port"; //$NON-NLS-1$
	String VALIDATE_URL = "ValidateURL"; //$NON-NLS-1$

	String EVENT_ID = "Event_Id"; //$NON-NLS-1$
	String EVENT_API = "Event_Api"; //$NON-NLS-1$
	String EVENT_ENABLED = "Event_Enabled"; //$NON-NLS-1$
	String EVENT = "Event"; //$NON-NLS-1$

	String WINDOW_EVENT = "WindowEvent"; //$NON-NLS-1$
	String JAVA_EVENT = "JavaEvent"; //$NON-NLS-1$

	String WINDOW_RECORD = "WindowRecord"; //$NON-NLS-1$

	String JAVA_RECORD = "JavaRecord"; //$NON-NLS-1$

	String RECORD = "Record"; //$NON-NLS-1$
	String ELEMENTID = "ElementId"; //$NON-NLS-1$
	String EVENTIDS = "EventIds"; //$NON-NLS-1$

	String IGNORE = "Ignore"; //$NON-NLS-1$
	String ATTRIBUTE = "Attribute"; //$NON-NLS-1$
	String DYNAMIC_ATTRIBUTES = "DynamicAttributes"; //$NON-NLS-1$

	// JAVA PROFILER
	String AGENT_SETTINGS = "AgentSettings";//$NON-NLS-1$
	String SAMPLING_RATE = "SamplingRate"; //$NON-NLS-1$
	String ALLOCATION_TRACE_DEPTH = "Allocation_Trace_Depth"; //$NON-NLS-1$
	String METHOD_CALL_DEPTH = "Method_Call_Depth"; //$NON-NLS-1$
	String INSTRUMENTATION_TYPE = "InstrumentationType"; //$NON-NLS-1$
	String CONNECTION_TIMED_OUT = "ConnectionTimeOutPeriod"; //$NON-NLS-1$
	String SHOW_FULL_SIGNTURE = "ShowFullSignature"; //$NON-NLS-1$
	String RECORD_URLS = "RecordURLS"; //$NON-NLS-1$
	String REFRESH_RATE = "ViewRefreshRate"; //$NON-NLS-1$
	String FILTERS = "Filters"; //$NON-NLS-1$
	String FILTER = "Filter"; //$NON-NLS-1$
	String ID = "Id"; //$NON-NLS-1$
	String SELECTED = "Selected"; //$NON-NLS-1$
	String EXCLUDE = "Exclude"; //$NON-NLS-1$
	String INCLUDE = "Include"; //$NON-NLS-1$
	String VIEW_FILTER = "View_Filter"; //$NON-NLS-1$
	String JP_COMPONENTS = "JPComponents"; //$NON-NLS-1$
	String COMPONENT_CATEOGRY = "ComponentCateogry"; //$NON-NLS-1$
	String COMPONENT_CLASS = "ComponentClass"; //$NON-NLS-1$

	// LOAD TESTER
	String CHECK_LOCATION_HEADER="CheckLocationHeader"; //$NON-NLS-1$
	String SAVE_HEADERS_N_PARAMETERS = "SaveHdrsNParams"; //$NON-NLS-1$
	String SAVE_SUB_TASKS = "SaveSubTasks"; //$NON-NLS-1$
	String SAVE_FAILED_RESPONSES = "SaveFailedResponse"; //$NON-NLS-1$

	String CODE = "CODE"; //$NON-NLS-1$
	String PHRASE = "PHRASE"; //$NON-NLS-1$
	String ISSYSTEMDEFINED = "IsSytemDefined"; //$NON-NLS-1$
	String ISENABLED = "IsEnabled"; //$NON-NLS-1$
	String IS_TREAT_AS_SUCCESS = "IsTreatAsSuccess"; //$NON-NLS-1$
	String HTTPRESPONSECODESETTINGS = "HTTPResponseCodeSettings"; //$NON-NLS-1$
	String SSLPROVIDER = "SSLProvider"; //$NON-NLS-1$
	String TESTOPTIONS = "TestOptions"; //$NON-NLS-1$
	String REFRESH_INTERVAL = "RefreshDataInterval"; //$NON-NLS-1$

	String CSV_REPORT_SEPARATOR = "CSVReportSeparator"; //$NON-NLS-1$
	String LOGIN_USERNAME = "LoginUser"; //$NON-NLS-1$
	String APP_HTTP_PROXY_PORT = "AppHttpProxyPort"; //$NON-NLS-1$
	String APP_HTTP_PROXY_HOST = "AppHttpProxyHost"; //$NON-NLS-1$
	String APP_HTTP_PROXY_TYPE = "AppHttpProxyType"; //$NON-NLS-1$
	String USE_CACHING = "UseCaching"; //$NON-NLS-1$
	String USE_COOKIES = "UseCookies"; //$NON-NLS-1$
	String COOKIE_POLICY = "CookiePolicy"; //$NON-NLS-1$
	String CONTENT_CHARSET = "ContentCharset"; //$NON-NLS-1$
	String IS_DEODE_POST_PARAMETER = "DecodePostParameter"; //$NON-NLS-1$
	String IS_DEODE_REQUEST_HEADER = "DecodeRequestHeader"; //$NON-NLS-1$
	String IS_DEODE_PARAMETER = "DecodeParameter"; //$NON-NLS-1$
	String IS_ENCODE_GET_PARAMETER = "EncodeGetParameter"; //$NON-NLS-1$
	String IS_LOG_REQUESTS_FIRED = "IsLogRequestsFired"; //$NON-NLS-1$
	String IS_INCLUDE_PATTERN = "IsIncludePattern"; //$NON-NLS-1$
	String IS_EXCLUDE_PATTERN = "IsExcludePattern"; //$NON-NLS-1$
	String EXCLUDE_PATTERN = "ExcludePattern"; //$NON-NLS-1$
	String INCLUDE_PATTERN = "IncludePattern"; //$NON-NLS-1$
	String REMOTE_SERVER_PORT = "RemoteServerPort"; //$NON-NLS-1$
	String DATA_CONSOLIDATION_COUNT = "DataConsolidationCount"; //$NON-NLS-1$
	String COMPARE_WITH_RECORDED_RESPONSE_CODE = "CompareWithRecordedResponseCode"; //$NON-NLS-1$
	String SUB_URL_EXTENSION_LIST = "SubURLExtensionList"; //$NON-NLS-1$
	String PASSTHROUGH_EXTENSION_LIST = "PassThroughExtensionList"; //$NON-NLS-1$
	String PASSTHROUGH_EXTENSION_DESCRIPTION = "PassthroughExtensionDescription"; //$NON-NLS-1$
	String PASSTHROUGH_EXTENSION = "PassthroughExtension"; //$NON-NLS-1$
	String PASSTHROUGH_SYSTEM_DEFINED = "IsPassthroughSystemDefined"; //$NON-NLS-1$
	String IS_PASSTHROUGH_CHECKED = "IsPassthroughChecked"; //$NON-NLS-1$
	String RENDER_LIST = "RenderingElementList"; //$NON-NLS-1$
	String RENDER_ELEMENT_NAME = "RenderingElementName"; //$NON-NLS-1$
	String RENDER_ELEMENT_TAG = "RenderingElementTag"; //$NON-NLS-1$
	String RENDER_ELEMENT_SELECTED = "isSelected"; //$NON-NLS-1$
	String SUPPRESS_HEADERS = "SuppressHeaders"; //$NON-NLS-1$
	String SUPPRESS_HEADER_RECORDING = "SuppressDuringRecording"; //$NON-NLS-1$
	String SUPPRESS_HEADER_REPLAYING = "SuppressDuringReplaying"; //$NON-NLS-1$
	String SUB_URL_EXTENSION = "SubURLExtension"; //$NON-NLS-1$
	String IS_SYSTEM_DEFINED = "IsSystemDefined"; //$NON-NLS-1$
	String SUB_URL_EXTENSION_DESCRIPTION = "SubURLExtensionDescription"; //$NON-NLS-1$
	String EXTENSION_DESCRIPTION = "Description"; //$NON-NLS-1$
	String IS_CHECKED = "IsChecked"; //$NON-NLS-1$
	String WEBELEMENTS = "WebElements"; //$NON-NLS-1$
	String WINDOWELEMENTS = "WindowElements"; //$NON-NLS-1$
	String JAVAELEMENTS = "JavaElements"; //$NON-NLS-1$
	String ELEMENT_OR_CLASSNAME = "ElementOrClassName"; //$NON-NLS-1$
	String ELEMENT_INPUT_TYPE = "ElementInputType";

//	String SYSMON_PORT = "SysmonPort";//$NON-NLS-1$
	String SYSMON_SETTINGS = "SysmonSettings";//$NON-NLS-1$

	String PROXY_AUTHENTICATION_ENABLED = "ProxyAuthenticationEnabled"; //$NON-NLS-1$
	String PROXY_AUTHENTICATION_USERNAME = "ProxyAuthenticationUserName"; //$NON-NLS-1$
	String PROXY_AUTHENTICATION_PASSWORD = "ProxyAuthenticationPassword"; //$NON-NLS-1$
	String AUTOMATIC_BROWSER_PROXY_SETTING = "AutoSetBrowserProxySetting"; //$NON-NLS-1$

	String LOGGER = "Logger"; //$NON-NLS-1$
	String BUILD_CREATION_TIME = "BuildCreationTime";
	String BUILD_NUMBER = "BuildNumber";
	
	String BUILD_CREATION_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	String SSL_CUSTOM_CERTIFICATE = "SSLCustomCertificate";// $NON_NLS$
	// //$NON-NLS-1$
	String SSL_USE_CUSTOM_CERT = "SSLUseCustomCert";// $NON_NLS$ //$NON-NLS-1$
	String SSL_ACCEPT_CUSTOM_CERT = "SSLAcceptCustomCert";// $NON_NLS$
	// //$NON-NLS-1$
	String SSL_CERTIFICATE = "SSLCertificate";// $NON_NLS$ //$NON-NLS-1$
	String SSL_CERT_COMPANYNAME = "SSLCertCompanyName";// $NON_NLS$
	// //$NON-NLS-1$
	String SSL_CERT_PATH = "SSLCertPath";// $NON_NLS$ //$NON-NLS-1$

	// System Monitor
	/*
	 * String SYSTEM_MONITOR = "SystemMonitor"; //$NON-NLS-1$ String CACHE =
	 * "Cache"; //$NON-NLS-1$ String TIMETOLIVE = "TimeToLive"; //$NON-NLS-1$
	 * String ACCESSTIMEOUT = "AccessTimeout"; //$NON-NLS-1$ String MAXSIZE=
	 * "MaxSize"; //$NON-NLS-1$ String CLEANERTIMERINTERVAL =
	 * "CleanerTimerInterval"; //$NON-NLS-1$
	 */

	String UPDATE_TYPE = "UpdateType"; //$NON-NLS-1$
	String UPDATE_DAYS = "UpdateDays"; //$NON-NLS-1$
	String NOTIFY_IN_DAYS = "NotifyInDays"; //$NON-NLS-1$
	String LAST_NOTIFY_TIME = "LastNotifyTime"; //$NON-NLS-1$

	// String CVS_CONNECTION = "CVSConnection"; //$NON-NLS-1$
	// String USECVSCONNECTION = "UseCVSConnection"; //$NON-NLS-1$
	String SVN_CONNECTION = "SVNConnection"; //$NON-NLS-1$
	String REPOSITORY_PATH = "RepositoryPath"; //$NON-NLS-1$
	String SVN_PROTOCOL = "SvnProtocol"; //$NON-NLS-1$
	String USER_NAME = "UserName"; //$NON-NLS-1$
	String PASSWORD = "Password"; //$NON-NLS-1$
	String HOST = "Host"; //$NON-NLS-1$
	String SHOW_SVN_DLG = "ShowSVNInfo"; //$NON-NLS-1$
	// String ROOT = "Root"; //$NON-NLS-1$
	// String USER = "Username"; //$NON-NLS-1$
	// String PASSWORD = "Password"; //$NON-NLS-1$
	// String CONNECTION = "Connection"; //$NON-NLS-1$

	String SERVICE = "Service";
	String SERVER = "Server";
	String PROTOCOL = "Protocol";
	
	String REPORT_IMAGE_PATH = "ReportImagePath";
	
	String MIME_TYPE_INFO = "MimeTypeInfo"; //$NON-NLS-1$
	String MIME_TYPE = "MimeType"; //$NON-NLS-1$
	String MIME_TYPE_FILE_EXTENSION = "FileExtension"; //$NON-NLS-1$
}