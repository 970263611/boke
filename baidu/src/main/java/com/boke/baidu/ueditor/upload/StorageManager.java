package com.boke.baidu.ueditor.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.boke.baidu.ueditor.define.AppInfo;
import com.boke.baidu.ueditor.define.BaseState;
import com.boke.baidu.ueditor.define.State;
import com.dahua.boke.util.StaticAddressUtil;

public class StorageManager {
	public static final int BUFFER_SIZE = 8192;

	public StorageManager() {
	}

	public static State saveBinaryFile(byte[] data, String path) {
		File file = new File(path);

		State state = valid(file);

		if (!state.isSuccess()) {
			return state;
		}

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(data);
			bos.flush();
			bos.close();
		} catch (IOException ioe) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}

		state = new BaseState(true, file.getAbsolutePath());
		state.putInfo("size", data.length);
		state.putInfo("title", file.getName());
		return state;
	}

	public static State saveFileByInputStream(InputStream is, String path, long maxSize) {
		State state = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
            byte[] buffer = new byte[1024];
            int num = is.read(buffer);
            while (num != -1) {
                baos.write(buffer, 0, num);
                num = is.read(buffer);
            }
            baos.flush();
        }catch(Exception e) {
        	
        } finally {
            if (is != null) {
            	try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
		
		state = saveTmpFile1(baos.toByteArray(), path);
		
		return state;
	}

	private static State saveTmpFile1(byte[] dataBuf, String path) {
		State state = null;
		File targetFile = new File(path);

		if (targetFile.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}
		String base64 = Base64.getEncoder().encodeToString(dataBuf);
		imgUp(path.split("name=")[1],base64);

		state = new BaseState(true);
		state.putInfo("size", targetFile.length());
		state.putInfo("title", targetFile.getName());
		return state;
	}
	
	public static void imgUp(String name,String img) {
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("name", name);
		paramMap.add("img", img);
		restTemplate.postForObject(StaticAddressUtil.yuming+"/fileUpBaiduImg",paramMap,String.class);
	}

	public static State saveFileByInputStream(InputStream is, String path) {
		State state = null;

		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[2048];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile),
					StorageManager.BUFFER_SIZE);

			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();

			state = saveTmpFile(tmpFile, path);

			if (!state.isSuccess()) {
				tmpFile.delete();
			}

			return state;
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static File getTmpFile() {
		File tmpDir = FileUtils.getTempDirectory();
		String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
		return new File(tmpDir, tmpFileName);
	}

	private static State saveTmpFile(File tmpFile, String path) {
		State state = null;
		File targetFile = new File(path);

		if (targetFile.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("name", path.split("name="));
        paramMap.add("img", "");
        restTemplate.postForObject(StaticAddressUtil.yuming+"/fileUpBaiduImg",paramMap,String.class);
		// FileUtils.moveFile(tmpFile, targetFile);

		state = new BaseState(true);
		state.putInfo("size", targetFile.length());
		state.putInfo("title", targetFile.getName());

		return state;
	}

	private static State valid(File file) {
		File parentPath = file.getParentFile();

		if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
			return new BaseState(false, AppInfo.FAILED_CREATE_FILE);
		}

		if (!parentPath.canWrite()) {
			return new BaseState(false, AppInfo.PERMISSION_DENIED);
		}

		return new BaseState(true);
	}
	
}
