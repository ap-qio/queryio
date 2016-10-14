package com.queryio.ftpserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;

import com.queryio.ftpserver.requestprocessor.CreateInputStreamRequest;
import com.queryio.ftpserver.requestprocessor.CreateOutputStreamRequest;
import com.queryio.ftpserver.requestprocessor.DeleteRequest;
import com.queryio.ftpserver.requestprocessor.GetFileStatusRequest;
import com.queryio.ftpserver.requestprocessor.GetGroupRequest;
import com.queryio.ftpserver.requestprocessor.GetModificationTimeRequest;
import com.queryio.ftpserver.requestprocessor.GetOwnerRequest;
import com.queryio.ftpserver.requestprocessor.GetPermissionsRequest;
import com.queryio.ftpserver.requestprocessor.GetSizeRequest;
import com.queryio.ftpserver.requestprocessor.IsDirRequest;
import com.queryio.ftpserver.requestprocessor.IsFileRequest;
import com.queryio.ftpserver.requestprocessor.ListFilesRequest;
import com.queryio.ftpserver.requestprocessor.MKDIRRequest;
import com.queryio.ftpserver.requestprocessor.MoveRequest;

public class HdfsFtpFile implements FtpFile {
	private final Logger log = Logger.getLogger(HdfsFtpFile.class);
	private final Path path;
	private final HdfsUser user;

	public HdfsFtpFile(String path, HdfsUser user) {
		this.path = new Path(path);
		this.user = user;
	}

	public String getAbsolutePath() {
		return this.path.toString();
	}

	public String getName() {
		String full = getAbsolutePath();
		int pos = full.lastIndexOf("/");
		if (full.length() == 1) {
			return "/";
		}
		return full.substring(pos + 1);
	}

	public boolean isHidden() {
		return false;
	}

	public boolean isDirectory() {
		final IsDirRequest request = new IsDirRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (Boolean) response : false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	private FsPermission getPermissions() throws IOException {
		final GetPermissionsRequest request = new GetPermissionsRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (FsPermission) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean isFile() {
		final IsFileRequest request = new IsFileRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (Boolean) response : false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public boolean doesExist() {
		final GetFileStatusRequest request = new GetFileStatusRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? true : false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public boolean isReadable() {
		try {
			FsPermission permissions = getPermissions();
			if (this.user.getName().equals(getOwnerName())) {
				if (permissions.toString().substring(0, 1).equals("r")) {
					System.out.println("PERMISSIONS: " + this.path + " - "
							+ " read allowed for user");
					return true;
				}

			}
			else if (permissions.toString().substring(6, 7)
					.equals("r")) {
				System.out.println("PERMISSIONS: " + this.path + " - "
						+ " read allowed for others");
				return true;
			}

			System.out.println("PERMISSIONS: " + this.path + " - "
					+ " read denied");
			return false;
		} catch (IOException e) {
			log.fatal(e.getMessage(), e);
			}
		return false;
	}

	private HdfsFtpFile getParent() {
		String pathS = this.path.toString();
		String parentS = "/";
		int pos = pathS.lastIndexOf("/");
		if (pos > 0) {
			parentS = pathS.substring(0, pos);
		}
		return new HdfsFtpFile(parentS, this.user);
	}

	public boolean isWritable() {
		try {
			FsPermission permissions = getPermissions();
			if (this.user.getName().equals(getOwnerName())) {
				if (permissions.toString().substring(1, 2).equals("w")) {
					System.out.println("PERMISSIONS: " + this.path + " - "
							+ " write allowed for user");
					return true;
				}

			}
			else if (permissions.toString().substring(7, 8)
					.equals("w")) {
				System.out.println("PERMISSIONS: " + this.path + " - "
						+ " write allowed for others");
				return true;
			}

			System.out.println("PERMISSIONS: " + this.path + " - "
					+ " write denied");
			return false;
		} catch (IOException e) {
		}
		return getParent().isWritable();
	}

	public boolean isRemovable() {
		return isWritable();
	}

	public String getOwnerName() {
		final GetOwnerRequest request = new GetOwnerRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (String) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public String getGroupName() {
		final GetGroupRequest request = new GetGroupRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (String) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public int getLinkCount() {
		return isDirectory() ? 3 : 1;
	}

	public long getLastModified() {
		final GetModificationTimeRequest request = new GetModificationTimeRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (Long) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return -1L;
	}

	public boolean setLastModified(long l) {
		return false;
	}

	public long getSize() {
		final GetSizeRequest request = new GetSizeRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (Long) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return -1L;
	}

	public boolean mkdir() {
		if (!isWritable()) {
			return false;
		}

		final MKDIRRequest request = new MKDIRRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (Boolean) response : false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public boolean delete() {
		final DeleteRequest request = new DeleteRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (Boolean) response : false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public boolean move(FtpFile fileObject) {
		final MoveRequest request = new MoveRequest(this.user, path, fileObject);
		try {
			final Object response = request.process();
			return response != null ? (Boolean) response : false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<FtpFile> listFiles() {
		if (!isReadable()) {
			System.out.println("No read permission : " + this.path);
			return null;
		}

		final ListFilesRequest request = new ListFilesRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (List<FtpFile>) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public OutputStream createOutputStream(long l) throws IOException {
		if (!isWritable()) {
			throw new IOException("No write permission : " + this.path);
		}

		final CreateOutputStreamRequest request = new CreateOutputStreamRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (OutputStream) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public InputStream createInputStream(long l) throws IOException {
		if (!isReadable())
			throw new IOException("No read permission : " + this.path);

		final CreateInputStreamRequest request = new CreateInputStreamRequest(this.user, path);
		try {
			final Object response = request.process();
			return response != null ? (InputStream) response : null;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
