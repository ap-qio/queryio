/*
 * @(#)  ChartPropertiesManager.java May 26, 2006
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

package com.queryio.common.charts.properties;

import com.queryio.common.util.IntHashMap;

/**
 * This is the controller class for reading and setting chart properties.
 * 
 * @author Exceed Consultancy Services
 */
public class ChartPropertiesManager
{
	private static IntHashMap products;
	public static final transient String PROPERTIES_FILE = "chartProperties.xml"; //$NON-NLS-1$
	public static final int TYPE_RUNTIME_CHARTS = -1;

	private ChartPropertiesManager()
	{

	}

	/**
	 * This method return the chart properties object for a specific node type
	 * (of chart) within a product.
	 * 
	 * @see com.queryio.sysmoncommon.IProductConstants
	 * @param productId
	 *            The product in which the chart is present
	 * @param nodeType
	 *            The node type of that particular chart
	 * @return an object of ChartProperties - the data structure representing
	 *         all properties of the chart
	 */
	public static ChartProperties getChartProperties(final int productId, final int nodeType)
	{
		// structure
		/*
		 * ChartPropertiesManager +-->IntHashMap [producttype, IntHashMap] +-->
		 * IntHashMap [NodeType, ChartProperties] +--> ChartProperties +-->
		 * IntHashMap [propertyType, Object]
		 */

		// for runtime generated charts
		if (nodeType == TYPE_RUNTIME_CHARTS)
		{
			return new ChartProperties();
		}

		if (products == null)
		{
			products = new IntHashMap();
		}
		IntHashMap productSpecificNodeTypes = (IntHashMap) products.get(productId);
		if (productSpecificNodeTypes == null)
		{
			productSpecificNodeTypes = new IntHashMap();
			products.put(productId, productSpecificNodeTypes);
		}
		ChartProperties chartProperties = (ChartProperties) productSpecificNodeTypes.get(nodeType);
		if (chartProperties == null)
		{
			chartProperties = new ChartProperties();
			productSpecificNodeTypes.put(nodeType, chartProperties);
		}
		return chartProperties;
	}

	/**
	 * This method saves the properties of the chart to a file
	 * 
	 * @param productId
	 *            The product in which the chart is present
	 * @param settingsFolder
	 *            The product's folder name within TestStudio Settings folder.
	 * @throws Exception
	 */
	// public static void writeToFile( final int productId , final String
	// settingsFolder ) throws Exception
	// {
	// final Object productSpecificNodeTypes = products.get(productId);
	// AppAsserts.assertNull(productSpecificNodeTypes);
	// new ChartPropertiesWriter(settingsFolder).saveChartProperties(productId,
	// (IntHashMap) productSpecificNodeTypes);
	// }
	public static IntHashMap getAllChartProperties(final int productId)
	{
		return (IntHashMap) products.get(productId);

	}
}
