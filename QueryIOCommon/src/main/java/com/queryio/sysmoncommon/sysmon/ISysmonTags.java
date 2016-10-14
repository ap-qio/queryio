/*
 * @(#)  ISysmonTags.java Aug 26, 2005
 *
 * Copyright (C) 2002 - 2005 Exceed Consultancy Services. All Rights Reserved.
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
package com.queryio.sysmoncommon.sysmon;

public interface ISysmonTags
{
	int VALID = 0x01;
	int INVALID = 0x02;
	int COLLECT_DATA = 0x03;
	int DATA_COLLECTED = 0x04;
	int GET_NETWORK_INFO = 0x05;
	int NETWORK_INFO_COLLECTED = 0x06;
	int EXCEPTION_MESSAGE = 0x07;
	int REQUIRES_PROCESS_INFO = 0x08;
	int PROCESS_INFO_NOT_REQUIRED = 0x09;
	int PROCESS_INFO_COLLECTED = 0x10;
	int GET_PROCESS_INFO = 0x11;
	int GET_PROCESS_COUNT = 0x12;
	int SHUT_DOWN = 0x13;
	int CONTINUE = 0x14;
}
