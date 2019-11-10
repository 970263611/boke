package com.boke.baidu.ueditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.boke.baidu.ueditor.define.ActionMap;
import com.dahua.boke.util.StaticAddressUtil;

/**
 * 配置管理器
 * @author hancong03@baidu.com
 *
 */
public final class ConfigManager {

	private final String rootPath;
	@SuppressWarnings("unused")
	private final String originalPath;
	@SuppressWarnings("unused")
	private final String contextPath;
	private static final String configFileName = "";
	private String parentPath = null;
	private JSONObject jsonConfig = null;
	// 涂鸦上传filename定义
	private final static String SCRAWL_FILE_NAME = "scrawl";
	// 远程图片抓取filename定义
	private final static String REMOTE_FILE_NAME = "remote";
	
	/*
	 * 通过一个给定的路径构建一个配置管理器， 该管理器要求地址路径所在目录下必须存在config.properties文件
	 */
	private ConfigManager ( String rootPath, String contextPath, String uri ) throws FileNotFoundException, IOException {
		
		rootPath = rootPath.replace( "\\", "/" );
		
		this.rootPath = rootPath;
		this.contextPath = contextPath;
		
		if ( contextPath.length() > 0 ) {
			this.originalPath = this.rootPath + uri.substring( contextPath.length() );
		} else {
			this.originalPath = this.rootPath + uri;
		}
		
		this.initEnv();
		
	}
	
	/**
	 * 配置管理器构造工厂
	 * @param rootPath 服务器根路径
	 * @param contextPath 服务器所在项目路径
	 * @param uri 当前访问的uri
	 * @return 配置管理器实例或者null
	 */
	public static ConfigManager getInstance ( String rootPath, String contextPath, String uri ) {
		
		try {
			return new ConfigManager(rootPath, contextPath, uri);
		} catch ( Exception e ) {
			return null;
		}
		
	}
	
	// 验证配置文件加载是否正确
	public boolean valid () {
		return this.jsonConfig != null;
	}
	
	public JSONObject getAllConfig () {
		
		return this.jsonConfig;
		
	}
	
	public Map<String, Object> getConfig ( int type ) {
		
		Map<String, Object> conf = new HashMap<String, Object>();
		String savePath = null;
		
		switch ( type ) {
		
			case ActionMap.UPLOAD_FILE:
				conf.put( "isBase64", "false" );
				conf.put( "maxSize", this.jsonConfig.getLong( "fileMaxSize" ) );
				conf.put( "allowFiles", this.getArray( "fileAllowFiles" ) );
				conf.put( "fieldName", this.jsonConfig.getString( "fileFieldName" ) );
				savePath = this.jsonConfig.getString( "filePathFormat" );
				break;
				
			case ActionMap.UPLOAD_IMAGE:
				conf.put( "isBase64", "false" );
				conf.put( "maxSize", this.jsonConfig.getLong( "imageMaxSize" ) );
				conf.put( "allowFiles", this.getArray( "imageAllowFiles" ) );
				conf.put( "fieldName", this.jsonConfig.getString( "imageFieldName" ) );
				savePath = this.jsonConfig.getString( "imagePathFormat" );
				break;
				
			case ActionMap.UPLOAD_VIDEO:
				conf.put( "maxSize", this.jsonConfig.getLong( "videoMaxSize" ) );
				conf.put( "allowFiles", this.getArray( "videoAllowFiles" ) );
				conf.put( "fieldName", this.jsonConfig.getString( "videoFieldName" ) );
				savePath = this.jsonConfig.getString( "videoPathFormat" );
				break;
				
			case ActionMap.UPLOAD_SCRAWL:
				conf.put( "filename", ConfigManager.SCRAWL_FILE_NAME );
				conf.put( "maxSize", this.jsonConfig.getLong( "scrawlMaxSize" ) );
				conf.put( "fieldName", this.jsonConfig.getString( "scrawlFieldName" ) );
				conf.put( "isBase64", "true" );
				savePath = this.jsonConfig.getString( "scrawlPathFormat" );
				break;
				
			case ActionMap.CATCH_IMAGE:
				conf.put( "filename", ConfigManager.REMOTE_FILE_NAME );
				conf.put( "filter", this.getArray( "catcherLocalDomain" ) );
				conf.put( "maxSize", this.jsonConfig.getLong( "catcherMaxSize" ) );
				conf.put( "allowFiles", this.getArray( "catcherAllowFiles" ) );
				conf.put( "fieldName", this.jsonConfig.getString( "catcherFieldName" ) + "[]" );
				savePath = this.jsonConfig.getString( "catcherPathFormat" );
				break;
				
			case ActionMap.LIST_IMAGE:
				conf.put( "allowFiles", this.getArray( "imageManagerAllowFiles" ) );
				conf.put( "dir", this.jsonConfig.getString( "imageManagerListPath" ) );
				conf.put( "count", this.jsonConfig.getInt( "imageManagerListSize" ) );
				break;
				
			case ActionMap.LIST_FILE:
				conf.put( "allowFiles", this.getArray( "fileManagerAllowFiles" ) );
				conf.put( "dir", this.jsonConfig.getString( "fileManagerListPath" ) );
				conf.put( "count", this.jsonConfig.getInt( "fileManagerListSize" ) );
				break;
				
		}
		
		conf.put( "savePath", savePath );
		conf.put( "rootPath", this.rootPath );
		
		return conf;
		
	}
	
	private void initEnv () throws FileNotFoundException, IOException {
		String path = System.getProperty("user.dir");
		File file = new File( path + "");
		
		if ( !file.isAbsolute() ) {
			file = new File( file.getAbsolutePath() );
		}
		
		this.parentPath = file.getParent();
		
		String configContent = this.readFile( this.getConfigPath() );
		
		try{
			JSONObject jsonConfig = new JSONObject( configContent );
			this.jsonConfig = jsonConfig;
		} catch ( Exception e ) {
			this.jsonConfig = null;
		}
		
	}
	
	private String getConfigPath () {
		return this.parentPath + File.separator + ConfigManager.configFileName;
	}

	private String[] getArray ( String key ) {
		
		JSONArray jsonArray = this.jsonConfig.getJSONArray( key );
		String[] result = new String[ jsonArray.length() ];
		
		for ( int i = 0, len = jsonArray.length(); i < len; i++ ) {
			result[i] = jsonArray.getString( i );
		}
		
		return result;
		
	}
	
	private String readFile ( String path ) throws IOException {
		
		/*StringBuilder builder = new StringBuilder();
		
		try {
			
			InputStreamReader reader = new InputStreamReader( new FileInputStream(StaticAddressUtil.baiduImgJson), "UTF-8" );
			BufferedReader bfReader = new BufferedReader( reader );
			
			String tmpContent = null;
			
			while ( ( tmpContent = bfReader.readLine() ) != null ) {
				builder.append( tmpContent );
			}
			
			bfReader.close();
			
		} catch ( UnsupportedEncodingException e ) {
			// 忽略
		}*/
		
		return this.filter( json );
		
	}
	
	private String json = "/* 前后端通信相关的配置,注释只允许使用多行方式 */" + 
			"{" + 
			"    /* 上传图片配置项 */" + 
			"    \"imageActionName\": \"uploadimage\", /* 执行上传图片的action名称 */" + 
			"    \"imageFieldName\": \"upfile\", /* 提交的图片表单名称 */" + 
			"    \"imageMaxSize\": 2048000, /* 上传大小限制，单位B */" + 
			"    \"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 上传图片格式显示 */" + 
			"    \"imageCompressEnable\": true, /* 是否压缩图片,默认是true */" + 
			"    \"imageCompressBorder\": 1600, /* 图片压缩最长边限制 */" + 
			"    \"imageInsertAlign\": \"none\", /* 插入的图片浮动方式 */" + 
			"    \"imageUrlPrefix\": \""+StaticAddressUtil.baiduImgUrl+"\", /* 图片访问路径前缀 */" + 
			"    \"imagePathFormat\": \""+StaticAddressUtil.yuming+"/fileUpBaiduImg?name={time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */" + 
			"                                /* {filename} 会替换成原文件名,配置这项需要注意中文乱码问题 */" + 
			"                                /* {rand:6} 会替换成随机数,后面的数字是随机数的位数 */" + 
			"                                /* {time} 会替换成时间戳 */" + 
			"                                /* {yyyy} 会替换成四位年份 */" + 
			"                                /* {yy} 会替换成两位年份 */" + 
			"                                /* {mm} 会替换成两位月份 */" + 
			"                                /* {dd} 会替换成两位日期 */" + 
			"                                /* {hh} 会替换成两位小时 */" + 
			"                                /* {ii} 会替换成两位分钟 */" + 
			"                                /* {ss} 会替换成两位秒 */" + 
			"                                /* 非法字符 \\ : * ? \" < > | */" + 
			"                                /* 具请体看线上文档: fex.baidu.com/ueditor/#use-format_upload_filename */" + 
			"" + 
			"    /* 涂鸦图片上传配置项 */" + 
			"    \"scrawlActionName\": \"uploadscrawl\", /* 执行上传涂鸦的action名称 */" + 
			"    \"scrawlFieldName\": \"upfile\", /* 提交的图片表单名称 */" + 
			"    \"scrawlPathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */" + 
			"    \"scrawlMaxSize\": 2048000, /* 上传大小限制，单位B */" + 
			"    \"scrawlUrlPrefix\": \"\", /* 图片访问路径前缀 */" + 
			"    \"scrawlInsertAlign\": \"none\"," + 
			"" + 
			"    /* 截图工具上传 */" + 
			"    \"snapscreenActionName\": \"uploadimage\", /* 执行上传截图的action名称 */" + 
			"    \"snapscreenPathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */" + 
			"    \"snapscreenUrlPrefix\": \"\", /* 图片访问路径前缀 */" + 
			"    \"snapscreenInsertAlign\": \"none\", /* 插入的图片浮动方式 */" + 
			"" + 
			"    /* 抓取远程图片配置 */" + 
			"    \"catcherLocalDomain\": [\"127.0.0.1\", \"localhost\", \"img.baidu.com\"]," + 
			"    \"catcherActionName\": \"catchimage\", /* 执行抓取远程图片的action名称 */" + 
			"    \"catcherFieldName\": \"source\", /* 提交的图片列表表单名称 */" + 
			"    \"catcherPathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */" + 
			"    \"catcherUrlPrefix\": \"\", /* 图片访问路径前缀 */" + 
			"    \"catcherMaxSize\": 2048000, /* 上传大小限制，单位B */" + 
			"    \"catcherAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 抓取图片格式显示 */" + 
			"" + 
			"    /* 上传视频配置 */" + 
			"    \"videoActionName\": \"uploadvideo\", /* 执行上传视频的action名称 */" + 
			"    \"videoFieldName\": \"upfile\", /* 提交的视频表单名称 */" + 
			"    \"videoPathFormat\": \"/ueditor/jsp/upload/video/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */" + 
			"    \"videoUrlPrefix\": \"\", /* 视频访问路径前缀 */" + 
			"    \"videoMaxSize\": 102400000, /* 上传大小限制，单位B，默认100MB */" + 
			"    \"videoAllowFiles\": [" + 
			"        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\"," + 
			"        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"], /* 上传视频格式显示 */" + 
			"" + 
			"    /* 上传文件配置 */" + 
			"    \"fileActionName\": \"uploadfile\", /* controller里,执行上传视频的action名称 */" + 
			"    \"fileFieldName\": \"upfile\", /* 提交的文件表单名称 */" + 
			"    \"filePathFormat\": \"/ueditor/jsp/upload/file/{yyyy}{mm}{dd}/{time}{rand:6}\", /* 上传保存路径,可以自定义保存路径和文件名格式 */" + 
			"    \"fileUrlPrefix\": \"\", /* 文件访问路径前缀 */" + 
			"    \"fileMaxSize\": 51200000, /* 上传大小限制，单位B，默认50MB */" + 
			"    \"fileAllowFiles\": [" + 
			"        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"," + 
			"        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\"," + 
			"        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"," + 
			"        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\"," + 
			"        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"" + 
			"    ], /* 上传文件格式显示 */" + 
			"" + 
			"    /* 列出指定目录下的图片 */" + 
			"    \"imageManagerActionName\": \"listimage\", /* 执行图片管理的action名称 */" + 
			"    \"imageManagerListPath\": \"/ueditor/jsp/upload/image/\", /* 指定要列出图片的目录 */" + 
			"    \"imageManagerListSize\": 20, /* 每次列出文件数量 */" + 
			"    \"imageManagerUrlPrefix\": \"\", /* 图片访问路径前缀 */" + 
			"    \"imageManagerInsertAlign\": \"none\", /* 插入的图片浮动方式 */" + 
			"    \"imageManagerAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], /* 列出的文件类型 */" + 
			"" + 
			"    /* 列出指定目录下的文件 */" + 
			"    \"fileManagerActionName\": \"listfile\", /* 执行文件管理的action名称 */" + 
			"    \"fileManagerListPath\": \"/ueditor/jsp/upload/file/\", /* 指定要列出文件的目录 */" + 
			"    \"fileManagerUrlPrefix\": \"\", /* 文件访问路径前缀 */" + 
			"    \"fileManagerListSize\": 20, /* 每次列出文件数量 */" + 
			"    \"fileManagerAllowFiles\": [" + 
			"        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"," + 
			"        \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\"," + 
			"        \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\"," + 
			"        \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\"," + 
			"        \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"" + 
			"    ] /* 列出的文件类型 */" + 
			"" + 
			"}";
	
	// 过滤输入字符串, 剔除多行注释以及替换掉反斜杠
	private String filter ( String input ) {
		
		return input.replaceAll( "/\\*[\\s\\S]*?\\*/", "" );
		
	}
	
}
