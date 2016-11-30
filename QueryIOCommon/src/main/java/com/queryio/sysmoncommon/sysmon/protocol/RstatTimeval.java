/*
 * @(#)  RstatTimeval.java
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
package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.IOException;

import org.acplt.oncrpc.OncRpcException;
import org.acplt.oncrpc.XdrAble;
import org.acplt.oncrpc.XdrDecodingStream;
import org.acplt.oncrpc.XdrEncodingStream;

/**
 * @author Exceed Consultancy Services
 * 
 */
public class RstatTimeval implements XdrAble {
	public int tv_sec;
	public int tv_usec;

	public RstatTimeval(final XdrDecodingStream xdr) throws OncRpcException, IOException {
		this.xdrDecode(xdr);
	}

	public void xdrEncode(final XdrEncodingStream xdr) throws OncRpcException, IOException {
		xdr.xdrEncodeInt(this.tv_sec);
		xdr.xdrEncodeInt(this.tv_usec);
	}

	public void xdrDecode(final XdrDecodingStream xdr) throws OncRpcException, IOException {
		this.tv_sec = xdr.xdrDecodeInt();
		this.tv_usec = xdr.xdrDecodeInt();
	}
}
