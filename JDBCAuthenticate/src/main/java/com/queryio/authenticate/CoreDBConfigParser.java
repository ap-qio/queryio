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
	
	private transient String tagDriver;
	private transient String tagUrl;
	private transient String tagUsername;
	private transient String tagPassword;
	private transient String tagDriverJar;
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
			if (this.tagDriver.equals(qualifiedName))
			{
				dbBean.setDriverName(this.currentVal);
			}
			else if (this.tagUrl.equals(qualifiedName))
			{			
				dbBean.setUrl(this.currentVal);
			}
			else if (this.tagUsername.equals(qualifiedName))
			{
				dbBean.setUserName(this.currentVal);
			}
			else if (this.tagPassword.equals(qualifiedName))
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
			else if (this.tagDriverJar.equals(qualifiedName))
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
		this.tagDriver = "driver";
		this.tagUrl = "url";
		this.tagUsername = "userName";
		this.tagPassword = "password";
		this.tagDriverJar = "driverJar";
	}
}
