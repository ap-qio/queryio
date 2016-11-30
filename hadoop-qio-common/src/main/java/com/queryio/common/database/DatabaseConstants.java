/*
 * @(#)  DatabaseConstants.java
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

/**
 * 
 * @author Exceed Consultancy Services
 */
public interface DatabaseConstants {
	// identity_vals table
	String TABLE_IDENTITY_VALS = "IDENTITY_VALUES"; //$NON-NLS-1$

	// true/false constants
	int COL_VALUE_FALSE = 0;
	int COL_VALUE_TRUE = 1;

	int DB_DATASOURCE_INDEX = 1;
	int CSV_DATASOURCE_INDEX = 2;

	// RESPONSE TABLE
	int RESPONSE_STATUS_SUCCEEDED = 1;
	int RESPONSE_STATUS_NOT_PLAYED = 2;
	int RESPONSE_STATUS_TIMEDOUT = 3;
	int RESPONSE_STATUS_FAILED = 4;
	int RESPONSE_STATUS_EXPLICIT_STOP_REQUEST = 5;

	// URLS TABLE AND EVENTS TABLE
	int PARAMETER_RANDOM = 0;
	int PARAMETER_SEQUENTIAL = 1;

	// identity_vals table
	String COL_IDENTITY_VALS_KEY_VALUE = "KEY_VALUE"; //$NON-NLS-1$
	String COL_IDENTITY_VALS_CURRENT_VAL = "CURRENT_VAL"; //$NON-NLS-1$

	String TREND_TIMESTAMP_COLUMN_NAME = "TRENDTIMESTAMP"; //$NON-NLS-1$
	String TABLE_TIMESTAMPS = "TIMESTAMPS"; //$NON-NLS-1$

	String TABLE_DUMMY = "DUMMY_TABLE"; //$NON-NLS-1$
	String COL_DUMMY = "DUMMY_COLUMN"; //$NON-NLS-1$

	String RELATIONSHIP_STRINGS[] = { "One-One", "One-Many", "Many-Many" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	int RELATIONSHIP_ONE_TO_ONE = 0;
	int RELATIONSHIP_ONE_TO_MANY = 1;
	int RELATIONSHIP_MANY_TO_MANY = 2;

	// constants for database options
	String DB_HSQL = "HSQL";
	String DB_MYSQL = "MySQL";
	String DB_ORACLE = "Oracle";
	String DB_POSTGRESQL = "PostgreSQL";

	String EMPTY_STRING = "";
}