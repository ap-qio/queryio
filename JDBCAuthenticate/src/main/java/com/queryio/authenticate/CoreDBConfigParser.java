package com.queryio.authenticate;


import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;



/**
 * This class parses the database config xml file
 * 
 * @author Exceed Consultancy Services
 */
public class CoreDBConfigParser extends DefaultHandler implements ErrorHandler
{
	public static final String DBCONFIG_XMLFILENAME = "dbconfig.xml";

	/*
	 * variables representing the tags in the XML file. They are not defined to
	 * be static & final as their memory should be cleared after parsing the XML
	 * file. There names are in CAPITAL letters as they act similar to constants
	 */
	private DetailBean dbBean;
	
	private transient String TAG_DRIVER;
	private transient String TAG_URL;
	private transient String TAG_USERNAME;
	private transient String TAG_PASSWORD;
	private transient String TAG_DRIVER_JAR;
	private boolean isCustomTagDB;
	private transient String currentVal = null;

	/**
	 * This method parses the xml and sets the Database properties in the
	 * EnvironmentatlConstants
	 * 
	 * @throws Exception
	 */
	public void loadDatabaseConfiguration(final String xmlFileName) throws Exception
	{
		
		this.allocateTags();
		dbBean = new DetailBean();
		/* parse the XML file */
		final XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(this);
		parser.parse(new InputSource(new BufferedInputStream(new FileInputStream(xmlFileName))));
	}

	public void startElement(final String namespaceURI,final String localName,final String qName,final Attributes atts)
	{
		if (atts.getValue("name") != null)
		{
			if (qName.equals("SystemDB"))
				isCustomTagDB = false;
			else
			{
				isCustomTagDB = true;
			}
		}
	}
	
	public DetailBean getDBDetail(){
		return dbBean;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(final char[] text, final int start, final int length)
	{
		this.currentVal = new String(text, start, length).trim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void endElement(final String namespaceURI, final String localName, final String qualifiedName)
	{
		if(!isCustomTagDB){
			if (this.TAG_DRIVER.equals(qualifiedName))
			{
				dbBean.setDriverName(this.currentVal);
			}
			else if (this.TAG_URL.equals(qualifiedName))
			{			
				dbBean.setUrl(this.currentVal);
			}
			else if (this.TAG_USERNAME.equals(qualifiedName))
			{
				dbBean.setUserName(this.currentVal);
			}
			else if (this.TAG_PASSWORD.equals(qualifiedName))
			{
				try {
					String encryptedPassword = this.currentVal;
					CryptManager cm = CryptManager.createInstance();
					String password = cm.decryptData(encryptedPassword);
					dbBean.setPassword(password);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (this.TAG_DRIVER_JAR.equals(qualifiedName))
			{
				dbBean.setDriverJar(this.currentVal);
			}
		}
	}

	/**
	 * allocateTags
	 * 
	 */
	private void allocateTags()
	{
		this.TAG_DRIVER = "driver";
		this.TAG_URL = "url";
		this.TAG_USERNAME = "userName";
		this.TAG_PASSWORD = "password";
		this.TAG_DRIVER_JAR = "driverJar";
	}
}
