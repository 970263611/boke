package provider.impl;

import aspect.DwqAnnotation;
import com.dahuaboke.rpc.annotation.RpcService;
import entity.User;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import provider.dao.UserDao;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import service.PhotoInputService;
import util.SerializeUtil;
import util.StaticAddressUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RpcService
@DwqAnnotation
@DependsOn("staticAddressUtil")
public class PhotoInputServiceImpl implements PhotoInputService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private JedisPool jedisPool;

	@SuppressWarnings("unchecked")
	public JSONObject fileUp(String fileName, String userId, int id, String text, User user) throws IOException {
		HashMap<Object, Object> map = new HashMap<>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间
		userDao.fileUp(id,fileName,text,date);
		map.put("data", "success");
		Jedis jedis = jedisPool.getResource();
		byte[] childs = jedis.get(("follow_"+user.getId()).getBytes());
		List<Integer> childIds = (List<Integer>) SerializeUtil.unserialize(childs);
		if(childIds!=null) {
			for(Integer newId:childIds) {
				byte[] noticeByte = jedis.hget("notice".getBytes(), (newId+"").getBytes());
				List<String> notices = new ArrayList<>();
				if(SerializeUtil.unserialize(noticeByte)!=null) {
					notices.addAll((List<String>) SerializeUtil.unserialize(noticeByte));
				}
				notices.add("您关注的"+user.getNickname()+"刚刚发布了一篇留言为"+text+"的图片");
				jedis.hset("notice".getBytes(), (newId+"").getBytes(), SerializeUtil.serialize(notices));
			}
		}
		jedis.close();
		return JSONObject.fromObject(map);
	}

}
