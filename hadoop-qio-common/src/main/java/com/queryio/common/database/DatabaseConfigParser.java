/*
 * @(#)  DatabaseConfigParser.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.CryptManager;

/**
 * This class parses the database config xml file
 * 
 * @author Exceed Consultancy Services
 */
public class DatabaseConfigParser extends DefaultHandler implements ErrorHandler {

	protected static final Logger LOGGER = Logger.getLogger(EnvironmentalConstants.class);
	CustomTagDBConfig dbConfig = null;
	DBTypeProperties dbTypeProperties = null;
	String name = null;
	String type = null;
	/*
	 * variables representing the tags in the XML file. They are not defined to
	 * be static & final as their memory should be cleared after parsing the XML
	 * file. There names are in CAPITAL letters as they act similar to constants
	 */
	private boolean isCustomTagDB;

	private transient String tagDriver;
	private transient String tagDbtype;
	private transient String tagUrl;
	private transient String tagUsername;
	private transient String tagPassword;
	private transient String tagDriverJar;

	private transient String tagPoolname;
	private transient String tagMaxconn;
	private transient String tagMaxidleconn;
	private transient String tagMaxwaittime;

	private transient String currentVal = null;
	private boolean isDBType = false;

	/**
	 * This method parses the xml and sets the Database properties in the
	 * EnvironmentatlConstants
	 * 
	 * @throws Exception
	 */
	public void loadDatabaseConfiguration(final String xmlFileName) throws Exception {
		this.allocateTags();

		/* parse the XML file */
		final XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(this);
		parser.parse(new InputSource(new BufferedInputStream(new FileInputStream(new File(xmlFileName)))));
	}

	public void startElement(final String namespaceURI, final String localName, final String qName,
			final Attributes atts) {
		if (localName.equals(QueryIOConstants.DBCONFIG_TYPE_ROOT))
			isDBType = !isDBType;

		if (!isDBType) {
			if (atts.getValue(QueryIOConstants.DB_NAME_TYPE) != null) {
				if (qName.equals(QueryIOConstants.DEFAULT_MONITOR_DB)) {
					isCustomTagDB = false;
					EnvironmentalConstants.setQueryIODatabaseType(atts.getValue(QueryIOConstants.DB_TYPE));
				} else {
					isCustomTagDB = true;
					dbConfig = new CustomTagDBConfig();
					name = qName;
					dbConfig.setCustomTagDBType(atts.getValue(QueryIOConstants.DB_TYPE));
				}
			}
		} else {
			if (atts.getValue(QueryIOConstants.DB_NAME_TYPE) != null) {
				if (qName.equals(QueryIOConstants.DATABASE_TYPE)) {
					dbTypeProperties = new DBTypeProperties();
					dbTypeProperties.setDbType(atts.getValue(QueryIOConstants.DB_NAME_TYPE));
					type = qName;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(final char[] text, final int start, final int length) {

		this.currentVal = new String(text, start, length).trim(); // Always
																	// ACITVE
																	// Connection

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void endElement(final String namespaceURI, final String localName, final String qualifiedName) {

		if (!isDBType) {
			// Handling for DBConfiguration
			if (isCustomTagDB) {
				if (this.tagDriver.equals(qualifiedName)) {
					dbConfig.setCustomTagDriverClass(this.currentVal);
				} else if (this.tagUrl.equals(qualifiedName)) {
					dbConfig.setCustomTagUrl(this.currentVal);
				} else if (this.tagUsername.equals(qualifiedName)) {
					dbConfig.setCustomTagUserName(this.currentVal);
				} else if (this.tagPassword.equals(qualifiedName)) {

					try {
						String encryptedPassword = this.currentVal;
						CryptManager cm = CryptManager.createInstance();
						String password = cm.decryptData(encryptedPassword);
						dbConfig.setCustomTagPassword(password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (this.tagPoolname.equals(qualifiedName)) {
					dbConfig.setCustomTagPoolName(this.currentVal);
				} else if (this.tagMaxconn.equals(qualifiedName)) {
					dbConfig.setCustomTagMaxConn(Integer.parseInt(this.currentVal));
				} else if (this.tagMaxidleconn.equals(qualifiedName)) {
					dbConfig.setCustomTagMaxIdleConn(Integer.parseInt(this.currentVal));
				} else if (this.tagMaxwaittime.equals(qualifiedName)) {
					dbConfig.setCustomTagMaxWaitTime(Integer.parseInt(this.currentVal));
				} else if (this.tagDriverJar.equals(qualifiedName)) {
					dbConfig.setCustomTagDriverJarPath(
							EnvironmentalConstants.getJdbcDriverPath() + File.separator + this.currentVal);
				} else if (this.name != null && this.name.equals(qualifiedName)) {
					CustomTagDBConfigManager.addCustomTagDBConfig(dbConfig);
					dbConfig = null;
					name = null;
				}
			} else {
				if (this.tagDriver.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseDriverClass(this.currentVal);
				} else if (this.tagUrl.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseURL(this.currentVal);
				} else if (this.tagUsername.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseUserName(this.currentVal);
				} else if (this.tagDbtype.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseType(this.currentVal);
				} else if (this.tagPassword.equals(qualifiedName)) {

					try {
						String encryptedPassword = this.currentVal;
						CryptManager cm = CryptManager.createInstance();
						String password = cm.decryptData(encryptedPassword);
						EnvironmentalConstants.setQueryIODatabasePassword(password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// EnvironmentalConstants.setQueryIODatabasePassword(this.currentVal);
				} else if (this.tagPoolname.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabasePoolName(this.currentVal);
				} else if (this.tagMaxconn.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseMaxConn(Integer.parseInt(this.currentVal));
				} else if (this.tagMaxidleconn.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseMaxIdleConn(Integer.parseInt(this.currentVal));
				} else if (this.tagMaxwaittime.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseMaxWaitTime(Integer.parseInt(this.currentVal));
				} else if (this.tagDriverJar.equals(qualifiedName)) {
					EnvironmentalConstants.setQueryIODatabaseDriverPath(
							EnvironmentalConstants.getJdbcDriverPath() + File.separator + this.currentVal);
				}
			}
		} else {
			// Handling for DBTypeConfiguration
			if (MetadataConstants.DB_CONFIG_DEFAULT_SCHEMA_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.setDefaultSchema(this.currentVal);
			else if (MetadataConstants.DB_CONFIG_STRING_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.STRING_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_TIMESTAMP_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.TIMESTAMP_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_LONG_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.LONG_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_SHORT_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.SHORT_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_INTEGER_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.INTEGER_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_REAL_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.REAL_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_DOUBLE_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.DOUBLE_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_DECIMAL_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.DECIMAL_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_BOOLEAN_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.BOOLEAN_WRAPPER_CLASS, this.currentVal);
			else if (MetadataConstants.DB_CONFIG_BLOB_ENTRY_TAG.equals(qualifiedName))
				dbTypeProperties.addDataType(MetadataConstants.BLOB_WRAPPER_CLASS, this.currentVal);
			else if (this.type != null && this.type.equals(qualifiedName)) {
				CustomTagDBConfigManager.addDBTypeProperties(dbTypeProperties.getDbType(), dbTypeProperties);
				dbTypeProperties = null;
			}
		}
	}

	/**
	 * allocateTags
	 * 
	 */
	private void allocateTags() {
		this.tagDriver = "driver";
		this.tagUrl = "url";
		this.tagUsername = "userName";
		this.tagPassword = "password";
		this.tagDriverJar = "driverJar";
		this.tagPoolname = "poolname";
		this.tagMaxconn = "maxconnections";
		this.tagMaxidleconn = "maxidleconnections";
		this.tagMaxwaittime = "waittimeinmillis";
		this.tagDbtype = "type";
	}

	public static void main(String[] args) throws Exception {
		new DatabaseConfigParser().loadDatabaseConfiguration("/QueryIO/tomcat/webapps/queryio/WEB-INF/dbconfig.xml");
	}
}
