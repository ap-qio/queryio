/*
 * @(#)  Statstime.java
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
public class Statstime implements XdrAble {
	public int[] cp_time;
	public int[] dk_xfer;
	public int v_pgpgin;
	public int v_pgpgout;
	public int v_pswpin;
	public int v_pswpout;
	public int v_intr;
	public int if_ipackets;
	public int if_ierrors;
	public int if_oerrors;
	public int if_collisions;
	public int v_swtch;
	public int[] avenrun;
	public RstatTimeval boottime;
	public RstatTimeval curtime;
	public int if_opackets;

	public Statstime() {
		// Default constructor
	}

	public Statstime(final XdrDecodingStream xdr) throws OncRpcException, IOException {
		this.xdrDecode(xdr);
	}

	public void xdrEncode(final XdrEncodingStream xdr) throws OncRpcException, IOException {
		xdr.xdrEncodeIntFixedVector(this.cp_time, RstatConstants.CPUSTATES);
		xdr.xdrEncodeIntFixedVector(this.dk_xfer, RstatConstants.DK_NDRIVE);
		xdr.xdrEncodeInt(this.v_pgpgin);
		xdr.xdrEncodeInt(this.v_pgpgout);
		xdr.xdrEncodeInt(this.v_pswpin);
		xdr.xdrEncodeInt(this.v_pswpout);
		xdr.xdrEncodeInt(this.v_intr);
		xdr.xdrEncodeInt(this.if_ipackets);
		xdr.xdrEncodeInt(this.if_ierrors);
		xdr.xdrEncodeInt(this.if_oerrors);
		xdr.xdrEncodeInt(this.if_collisions);
		xdr.xdrEncodeInt(this.v_swtch);
		xdr.xdrEncodeIntFixedVector(this.avenrun, 3);
		this.boottime.xdrEncode(xdr);
		this.curtime.xdrEncode(xdr);
		xdr.xdrEncodeInt(this.if_opackets);
	}

	public void xdrDecode(final XdrDecodingStream xdr) throws OncRpcException, IOException {
		this.cp_time = xdr.xdrDecodeIntFixedVector(RstatConstants.CPUSTATES);
		this.dk_xfer = xdr.xdrDecodeIntFixedVector(RstatConstants.DK_NDRIVE);
		this.v_pgpgin = xdr.xdrDecodeInt();
		this.v_pgpgout = xdr.xdrDecodeInt();
		this.v_pswpin = xdr.xdrDecodeInt();
		this.v_pswpout = xdr.xdrDecodeInt();
		this.v_intr = xdr.xdrDecodeInt();
		this.if_ipackets = xdr.xdrDecodeInt();
		this.if_ierrors = xdr.xdrDecodeInt();
		this.if_oerrors = xdr.xdrDecodeInt();
		this.if_collisions = xdr.xdrDecodeInt();
		this.v_swtch = xdr.xdrDecodeInt();
		this.avenrun = xdr.xdrDecodeIntFixedVector(3);
		this.boottime = new RstatTimeval(xdr);
		this.curtime = new RstatTimeval(xdr);
		this.if_opackets = xdr.xdrDecodeInt();
	}
}
