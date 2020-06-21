package provider.impl;

import aspect.DwqAnnotation;
import com.alibaba.fastjson.JSONObject;
import com.dahuaboke.rpc.annotation.RpcService;
import entity.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import provider.dao.UserDao;
import provider.dao.WeChatDao;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import service.UserService;
import util.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RpcService
@DwqAnnotation
@DependsOn("staticAddressUtil")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private WeChatDao weChatDao;

    private static final String ipAddress = StaticAddressUtil.rocketMQTelnet;
    //产生者组
    private static final String producterName = "dwq"; //自己起的名字不需要配置
    //topic名称
    private static final String topicName = "dahuaboke";//topic 需要配置

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 向数据库新增用户信息service实现类
     *
     * @return
     */
    public void saveUser(User user) {
        userDao.saveUser(user);
    }

    /**
     * 用户登录方法，返回用户的昵称
     */
    @Override
    public String Userlogin(User user) {
        return userDao.selectNickname(user);
    }

    /**
     * 查询分页
     */
    //判定文章分页调用为第几次调用
    int dy = 1;

    @Override
    public List<Article> Go_page(int page) {
        //开启redis连接
        Jedis jedis = jedisPool.getResource();
        //限定一页装载的文章数量
        int pageSize = 6;
        //设定截止到当前页一共需要的文章条数
        int articleSize = page * pageSize;
        //本页第一条文章的位置
        int firstArticle = (page - 1) * pageSize;
        /**
         * redis排序方式
         */
        SortingParams sortingParameters = new SortingParams();
        sortingParameters.desc();
        sortingParameters.alpha();
        //新建装在置顶文章标题的list
        List<String> timeStr = new ArrayList<>();
        //置顶文章的数量
        int size = jedis.llen("top").intValue();
        //最后返回的list，所有要返回的数据装在这里面
        List<Article> acList = new ArrayList<>();
        //非置顶文章的数量
        int asize = jedis.llen("article").intValue();
        //当redis中置顶文章数量为0的时候
        if (size != 0) {
            //当redis中置顶文章数量大于当前页（包含前面的页）所需要的总文章条数
            if (size > articleSize) {
                //将文章标题从redis中取出
                timeStr = jedis.sort("top", sortingParameters).subList(firstArticle, pageSize);
                //将文章从redis中取出
                acList.addAll(RedisUtil.hgetArticle(timeStr, jedis));
                //返回文章信息
                return acList;
                //当redis中置顶文章不大于当前页（包含前面的页）所需要的总文章条数
            } else {
                //当置顶文章数量取余本页所需要文章数量为0的时候
                if (size % pageSize == 0) {
                    //判断当前页是否有置顶文章，通过置顶文章总数除于当前一页所需要文章数量如果等于当前的页码（则本页有置顶文章）
                    if (size / pageSize == page) {
                        //到此可以保证前面的都是置顶文章，排序redis中的置顶文章取出当前页所需要第一条，就是置顶文章里面对应的那条，取到置顶文章条数结束，因为到此可以保证置顶文章数量不够本页的条数
                        timeStr = jedis.sort("top", sortingParameters).subList(firstArticle, size);
                        //将取出的文章放到返回的list里面
                        acList.addAll(RedisUtil.hgetArticle(timeStr, jedis));
                    }
                    //当置顶文章数量取余本页所需要文章数量不为0的时候
                } else {
                    //判断当前页是否有置顶文章，通过置顶文章总数除于当前一页所需要文章数量加上1如果等于当前的页码（则本页有置顶文章）
                    if (size / pageSize + 1 == page) {
                        //到此可以保证前面的都是置顶文章，排序redis中的置顶文章取出当前页所需要第一条，就是置顶文章里面对应的那条，取到置顶文章条数结束，因为到此可以保证置顶文章数量不够本页的条数
                        timeStr = jedis.sort("top", sortingParameters).subList(firstArticle, size);
                        //将取出的文章放到返回的list里面
                        acList.addAll(RedisUtil.hgetArticle(timeStr, jedis));
                    }
                }
                //当非置顶文章数量不为0的时候
                if (asize != 0) {
                    //定义非置顶文章标题的list
                    List<String> notops = new ArrayList<>();
                    //当非置顶文章的数量大于等于当前页（包含前面的页）所需要的总文章条数减去置顶文章的数量时
                    if (asize >= (articleSize - size)) {
                        //当置顶文章数量和非置顶文章数量的和除于本页所需要文章数量（一共的页数，本应加1，等式后面也要加1，抵消了）减去置顶文章数量除于本页所需要文章数量（即置顶文章的页数）大于0的时候（说明文章不全是置顶文章，存在非置顶文章）
                        if ((size + asize) / pageSize - (size / pageSize) > 0) {
                            //当当前页码减1乘上本页所需要文章数量(截止到前一页所需要的文章总数)减去置顶文章数小于0的时候（说明本页有置顶文章，但是置顶文章有不够本页时）
                            if ((page - 1) * pageSize - size < 0) {
                                //redis中取出非置顶文章的标题
                                notops = jedis.sort("article", sortingParameters).subList(0, articleSize - size);
                            } else {
                                //说明本页没有置顶文章，全是非置顶文章，在redis中取出所需要的非置顶文章标题
                                //(page-1)*本页所需要文章数量-size本页减1乘上本页所需要文章数量减去置顶文章数为非置顶文章填补给置顶文章的个数，从这个取开始取（截止到本页所需要的减去置顶文章）个（填补给置顶文章凑齐本页个数，保证个数正确）
                                notops = jedis.sort("article", sortingParameters).subList((page - 1) * pageSize - size, articleSize - size);
                            }
                            //当置顶文章数量和非置顶文章数量的和除于每页所需要文章数量（一共的页数，本应加1，等式后面也要加1，抵消了）减去置顶文章数量除于每页所需要文章数量*（即置顶文章的页数）不大于0的时候（说明不全是置顶文章，存在非置顶文章，所以从0开始取，取本页减去置顶文章个数剩余的个数）
                        } else {
                            notops = jedis.sort("article", sortingParameters).subList(0, pageSize - size % pageSize);
                        }
                        //当非置顶文章的总数量不够填补本页了
                    } else {
                        //从redis中取出所有非置顶文章标题
                        notops = jedis.sort("article", sortingParameters).subList(firstArticle - size, asize);
                    }
                    //根据标题从redis中取出应该返回的非置顶文章添加到返回的list中
                    acList.addAll(RedisUtil.hgetArticle(notops, jedis));
                    //当非置顶文章个数为0的时候
                } else {
                    //从数据库中取出所有的非置顶文章
                    List<Article> untops = userDao.select_article_untop();
                    //将所有的非置顶文章缓存到redis中
                    RedisUtil.hsetArticle(untops, jedis);
                    //递归调用并返回
                    return Go_page(page);
                }
            }
            //当置顶文章个数为0的时候
        } else {
            //从redis中查询非置顶文章的个数
            int newasize = jedis.llen("article").intValue();
            //判断数据库置顶文章个数是否为0，当为0的情况时
            if (newasize != 0) {
                //获取所有非置顶的文章标题
                List<String> articles = jedis.sort("article", sortingParameters);
                //新建一个list用于接收截取后的标题
                List<String> titles = new ArrayList<>();
                //当redis中非置顶文章个数大于等于本页所需要文章数量的时候（足够填充本页）
                if (newasize >= articleSize) {
                    //截取操作
                    for (int a = firstArticle; a < articleSize; a++) {
                        titles.add(articles.get(a));
                    }
                    //从redis取出非置顶文章的前本页所需要文章数量条
                    acList = RedisUtil.hgetArticle(titles, jedis);
                    //当redis中非置顶文章个数不足够填充本页时
                } else {
                    //截取操作
                    for (int a = firstArticle; a < newasize; a++) {
                        titles.add(articles.get(a));
                    }
                    //取出redis中所有的非置顶文章
                    acList = RedisUtil.hgetArticle(titles, jedis);
                }
                //判断数据库置顶文章个数是否为0，当不为0的情况时
            } else {
                if (dy == 2) {
                    //从数据库中取出所有的非置顶文章
                    List<Article> untops = userDao.select_article_untop();
                    //将所有的非置顶文章缓存到redis中
                    RedisUtil.hsetArticle(untops, jedis);
                    //递归调用并返回
                    return Go_page(page);
                }
                List<Article> topac = userDao.select_article_top();
                //查询出数据库所有的置顶文章
                acList.addAll(topac);
                //将置顶文章缓存到redis中
                RedisUtil.hsetTopArticle(acList, jedis);
                //调用次数加一
                dy = dy + 1;
                //递归调用并返回
                return Go_page(page);
            }
        }
        //关闭redis连接，释放资源
        jedis.close();
        for (Article a : acList) {
            a.setCreate_time(a.getCreate_time().replaceFirst("20-", ""));
            a.setCreate_time(a.getCreate_time().replaceFirst("19-", ""));
            a.setCreate_time(a.getCreate_time().replaceFirst("18-", ""));
            a.setCreate_time(a.getCreate_time().replaceFirst("17-", ""));
        }
        //返回文章信息给前端
        return acList;
    }

    /**
     * 保存文章方法事物层实现
     */
    @Override
    @SuppressWarnings("unchecked")
    public String save_article(Article ac, User user) {
        Jedis jedis = jedisPool.getResource();
        int size = userDao.saveArticle(ac);
        if (size == 1) {
            RedisUtil.hsetArticleSingle(ac, jedis);
            byte[] childs = jedis.get(("follow_" + user.getId()).getBytes());
            List<Integer> childIds = (List<Integer>) SerializeUtil.unserialize(childs);
            if (childIds != null) {
                for (Integer id : childIds) {
                    byte[] noticeByte = jedis.hget("notice".getBytes(), (id + "").getBytes());
                    List<String> notices = new ArrayList<>();
                    if (SerializeUtil.unserialize(noticeByte) != null) {
                        notices.addAll((List<String>) SerializeUtil.unserialize(noticeByte));
                    }
                    notices.add("您关注的" + ac.getCreate_user() + "刚刚发布了一篇名字为" + ac.getTitle() + "的文章");
                    jedis.hset("notice".getBytes(), (id + "").getBytes(), SerializeUtil.serialize(notices));
                }
            }
            jedis.close();
            return "success";
        } else {
            return "error";
        }
    }

    /**
     * 根据id查询单挑文章记录，用于进入文章详情页面
     */
    @Override
    public Article toSingle(String id) {
        Jedis jedis = jedisPool.getResource();
        Article ac = null;
        String value = jedis.hget("article_" + id, "id");
        if (RedisUtil.notEnpty(value)) {
            ac = RedisUtil.hgetArticleSingle(id, jedis);
        } else {
            ac = userDao.toSingle(id);
            RedisUtil.hsetArticleSingle(ac, jedis);
        }
        jedis.close();
        return ac;
    }

    /**
     * 根据关联id查询文章的所有评价
     */
    @Override
    public List<Comment> select_message(String id) {
        return userDao.select_message(id);
    }

    /**
     * 提交评价
     */
    @Override
    public String comment_insert(Comment comment) {
        int size = userDao.comment_insert(comment);
        if (size == 1) {
            return "success";
        }
        return null;
    }

    /**
     * 提交留言信息
     *
     * @param wordMessage
     * @return
     */
    @Override
    public String words_mess(WordMessage wordMessage) {
        int size = userDao.words_mess(wordMessage);
        if (size == 1) {
            return "success";
        }
        return null;
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @Override
    public String userAdd(User user) {
        // 先查询新注册用户昵称是否重名
        String id_two = userDao.user_seNickname(user);
        if (id_two != null) {
            return "NickNamealReguster";
        }
        // 先查询新注册用户用户名是否重名
        String id = userDao.user_seName(user);
        if (id != null) {
            return "alReguster";
        }
        // 将拥护注册信息添加到数据库
        int size = userDao.userAdd(user);
        if (size == 1) {
            return "1";
        }
        return null;
    }

    /**
     * 根据用户昵称获取起所发的文章
     *
     * @param nickname
     * @return
     */
    @Override
    public List<Article> select_article_mine(String nickname) {
        return (List<Article>) userDao.select_article_mine(nickname);
    }

    /**
     * 保存用户的铭言和格言
     */
    @Override
    public String add_myworld_test(int id, String test1, String test2) {
        int a = 0;
        String test_1 = userDao.select_myworld_test1(id);
        String test_2 = userDao.select_myworld_test2(id);
        if (test_1 != null || test_2 != null) {
            a = userDao.update_myworld_test(id, test1, test2);
        } else {
            a = userDao.add_myworld_test(id, test1, test2);
        }
        if (a == 1) {
            return "success";
        }
        return "error";
    }

    /**
     * 查询文章条数，便于前端分页
     */
    @Override
    public int pageNum() {
        return userDao.pageNum();
    }

    /**
     * 缓存所有文章的方法，要先执行一下在进入系统，需要管理员手动执行
     */
    @Override
    public String admin_redis() {
        List<Article> list = userDao.admin_redis();
        Jedis jedis = jedisPool.getResource();
        List<Article> topAc = new ArrayList<>();
        List<Article> untopAc = new ArrayList<>();
        for (Article ac : list) {
            if (ac.getTop().equals("1")) {
                topAc.add(ac);
            } else {
                untopAc.add(ac);
            }
        }
        RedisUtil.hsetTopArticle(topAc, jedis);
        RedisUtil.hsetArticle(untopAc, jedis);
        List<Follow> follows = userDao.getFollows();
        HashSet<Integer> set = new HashSet<>();
        for (Follow follow : follows) {
            set.add(follow.getParentId());
        }
        for (Integer parentId : set) {
            List<Integer> childList = new ArrayList<>();
            for (Follow follow : follows) {
                if (parentId == follow.getParentId()) {
                    childList.add(follow.getChildId());
                }
            }
            jedis.set(("follow_" + parentId).getBytes(), SerializeUtil.serialize(childList));
        }
        jedis.close();
        logger.info("------所有文章缓存成功------");
        return "redis_success";
    }

    @Override
    public String admin_clear() {
        Jedis jedis = jedisPool.getResource();
        jedis.flushAll();
        jedis.close();
        logger.info("------清空缓存成功------");
        return "redis_clear";
    }

    /**
     * 根据用户名和密码获取user对象
     *
     * @return
     */
    @Override
    public User getUser(User user) {
        User newUser = userDao.finduser(user);
        return newUser;
    }

    /**
     * 根据用户昵称获取起所有的文章
     *
     * @param nickname
     * @return
     */
    @Override
    public List<Article> select_article_user_all(String nickname) {
        return userDao.select_article_user_all(nickname);
    }

    /**
     * 查询最新照片信息
     *
     * @return
     */
    @Override
    public List<MyPhoto> select_all_four() {
        return userDao.select_all_four();
    }

    /**
     * 根据用户id查询用户所有信息
     */

    @Override
    public User select_user(Integer userId) {
        return userDao.select_user(userId);
    }

    /**
     * 查询用户所有上传的照片
     *
     * @param nickname
     * @return
     */
    @Override
    public List<MyPhoto> select_photo_user_all(String nickname) {
        return userDao.select_photo_user_all(nickname);
    }

    /**
     * 关注方法
     */
    @SuppressWarnings("unchecked")
    @Override
    public String follow(String articleId, User user) {
        Integer parentId = userDao.getUserIdByArticleId(articleId);

        Jedis jedis = jedisPool.getResource();
        byte[] bytes = jedis.get(("follow_" + parentId).getBytes());
        List<Integer> childIds = (List<Integer>) SerializeUtil.unserialize(bytes);//取出关注用户的子集
        if (user == null) {
            jedis.close();
            return "nouser";
        }
        if (childIds != null && childIds.contains(user.getId())) {//如果子集包含当前登录用户
            System.out.println("此用户已经关注了");
            jedis.close();
            return "successed";
        } else {
            Follow follow = new Follow();
            follow.setParentId(parentId);
            ;
            follow.setChildId(user.getId());
            follow.setCreateTime(new Date());
            RocketMQUtil.producer(ipAddress, producterName, topicName, "follow", JSONObject.toJSONString(follow));
            if (childIds == null) {
                childIds = new ArrayList<>();
            }
            childIds.add(user.getId());
            jedis.set(("follow_" + parentId).getBytes(), SerializeUtil.serialize(childIds));
            byte[] noticeByte = jedis.hget("notice".getBytes(), (parentId + "").getBytes());
            List<String> notices = new ArrayList<>();
            if (SerializeUtil.unserialize(noticeByte) != null) {
                notices.addAll((List<String>) SerializeUtil.unserialize(noticeByte));
            }
            notices.add(user.getNickname() + "刚刚关注了你");
            jedis.hset("notice".getBytes(), (parentId + "").getBytes(), SerializeUtil.serialize(notices));
        }
        jedis.close();
        return "success";
    }

    /**
     * 文章置顶的方法
     */
    @Override
    public String top(String articleId, String time) {
        Jedis jedis = jedisPool.getResource();
        List<String> list = jedis.lrange("article", 0, -1);
        for (String s : list) {
            if (s.contains(time + "*" + articleId)) {
                jedis.lrem("article", 1, s);
                jedis.lpush("top", s);
            }
        }
        jedis.hset("article_" + articleId, "top", "1");
        jedis.close();
        RocketMQUtil.producer(ipAddress, producterName, topicName, "top", articleId);
        return "success";
    }

    /**
     * 文章取消置顶的方法
     */
    @Override
    public String untop(String articleId, String time) {
        Jedis jedis = jedisPool.getResource();
        List<String> list = jedis.lrange("top", 0, -1);
        for (String s : list) {
            if (s.contains(time + "*" + articleId)) {
                jedis.lrem("top", 1, s);
                jedis.lpush("article", s);
            }
        }
        jedis.hset("article_" + articleId + "", "top", "0");
        jedis.close();
        RocketMQUtil.producer(ipAddress, producterName, topicName, "untop", articleId);
        return "success";
    }

    /**
     * 文章删除的方法
     */
    @Override
    public String isdel(String articleId, String time) {
        Jedis jedis = jedisPool.getResource();
        jedis.del("article_" + articleId);
        List<String> list = jedis.lrange("article", 0, -1);
        for (String s : list) {
            if (s.contains(time + "*" + articleId)) {
                jedis.lrem("article", 1, s);
            }
        }
        jedis.close();
        RocketMQUtil.producer(ipAddress, producterName, topicName, "isdel", articleId);
        return "success";
    }

    /**
     * 消费者消费方法
     *
     * @param msgs
     */
    public void custom(List<MessageExt> msgs) {
		/*for (MessageExt msg : msgs) {
			String topic = msg.getTags();
			String body = new String(msg.getBody());
			Follow follow = JSON.parseObject(new String(msg.getBody()), Follow.class);
        }*/
    }

    /**
     * select2加载数据
     */
    @Override
    public List<Map> getArticleTypes() {
        return userDao.getArticleTypes();
    }

    /**
     * 获取用户的名言和留言
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public HashMap getmyandly(String uId) {
        int userId = Integer.parseInt(uId);
        String test1 = userDao.select_myworld_test1(userId);
        String test2 = userDao.select_myworld_test2(userId);
        HashMap map = new HashMap<>();
        map.put("test1", test1);
        map.put("test2", test2);
        return map;
    }

    /**
     * 查询用户的关注关系
     */
    @Override
    public String checkFolllw(String articleId, User user) {
        Integer parentId = userDao.getUserIdByArticleId(articleId);
        Jedis jedis = jedisPool.getResource();
        byte[] bytes = jedis.get(("follow_" + parentId).getBytes());
        @SuppressWarnings("unchecked")
        List<Integer> childIds = (List<Integer>) SerializeUtil.unserialize(bytes);//取出关注用户的子集
        if (user == null) {
            jedis.close();
            return "nouser";
        }
        if (childIds != null && childIds.contains(user.getId())) {//如果子集包含当前登录用户
            System.out.println("此用户已经关注了");
            jedis.close();
            return "successed";
        } else {
            jedis.close();
            return "show";
        }
    }

    /**
     * 像redis中存储访问量
     *
     * @param id
     */
    @Override
    public void setSee(String id) {
        Jedis jedis = jedisPool.getResource();
        int see;
        Article article = RedisUtil.hgetArticleSingle(id, jedis);
        if (jedis.hget("article_" + id, "see") != null) {
            see = article.getSee();
            see = see + 1;
        } else {
            see = userDao.toSingle(id).getSee();
            see = 1;
        }
        jedis.hset("article_" + article.getId() + "", "see", see + "");
        jedis.close();
    }

    /**
     * 用户添加标签存储方法
     */
    @Override
    public String saveTags(String tag, User user) {
        Tag tagEntity = new Tag();
        tagEntity.setTag(tag);
        if (user != null) {
            tagEntity.setUserId(user.getId());
            tagEntity.setNickname(user.getNickname());
        }
        RocketMQUtil.producer(ipAddress, producterName, topicName, "tagSave", JSONObject.toJSONString(tagEntity));
        return "success";
    }

    /**
     * 获取消息
     *
     * @param id
     * @return
     */
    @Override
    public List<String> getNotices(int id) {
        Jedis jedis = jedisPool.getResource();
        byte[] noticeByte = jedis.hget("notice".getBytes(), (id + "").getBytes());
        @SuppressWarnings("unchecked")
        List<String> notices = (List<String>) SerializeUtil.unserialize(noticeByte);
        jedis.close();
        return notices;
    }

    /**
     * 查看后删除消息
     */
    @Override
    public String delNotice(int id) {
        Jedis jedis = jedisPool.getResource();
        jedis.hdel("notice".getBytes(), (id + "").getBytes());
        jedis.close();
        return "success";
    }

    @Override
    public String shareArticle(String id, String nickname) {
        String imgName = getPass();
        String urlTop = StaticAddressUtil.yuming + "/";
        String loginUUID = "";
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (isNumeric(id)) {
            urlTop = urlTop + "single?id=" + id;
        } else if (id.contains("modify")) {
            urlTop = urlTop + "myworld?" + id;
        } else if (id.contains("*erweimadenglu*")) {
            loginUUID = id;
            Jedis jedis = jedisPool.getResource();
            jedis.set(loginUUID, loginUUID);
            jedis.expire(loginUUID, 60 * 3);
            jedis.close();
            urlTop = urlTop + "Sweep?loginUUID=" + loginUUID.split("\\*erweimadenglu\\*")[0];
        } else {
            urlTop = urlTop + id;
        }
        logger.debug("urlTop--------+++++++++++++++++-------------" + urlTop);
        map.put("url", urlTop);
        map.put("sharePeople", nickname);
        map.put("imgName", imgName);
        map.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
        map.put("createTime", new Date());
        String jsonString = JSONObject.toJSONString(map);
        QrCodeCreateUtil.CreateQrCodeByHua(urlTop, imgName);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        // 可以执行Runnable对象或者Callable对象代表的线程
        //pool.submit(new MyTask(urlTop,imgName));
        pool.execute(new RocketTask(jsonString));
        //结束线程池
        pool.shutdown();
        return imgName;
    }

    /**
     * 扫描登录方法实现
     */
    @Override
    public String sweep(String loginUUID, String code) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + StaticAddressUtil.AppID_CS + "&secret="
                        + StaticAddressUtil.AppSecret_CS + "&code=" + code + "&grant_type=authorization_code");
        // Create a custom response handler
        ResponseHandler<net.sf.json.JSONObject> responseHandler = new ResponseHandler<net.sf.json.JSONObject>() {

            public net.sf.json.JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    if (null != entity) {
                        String result = EntityUtils.toString(entity);
                        // 根据字符串生成JSON对象
                        net.sf.json.JSONObject resultObj = net.sf.json.JSONObject.fromObject(result);
                        return resultObj;
                    } else {
                        return null;
                    }
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        //设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        httpget.setConfig(requestConfig);
        System.out.println("请求信息成功-webocket自动登录");
        logger.info("请求信息成功-webocket自动登录");
        // 返回的json对象
        net.sf.json.JSONObject responseBody;
        String openid = "";
        try {
            responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("responseBody=============" + responseBody.toString());
            logger.info("基本信息=============" + responseBody.toString());
            // 暂时只用到openid
            /*
             * String access_token = (String) responseBody.get("access_token"); String
             * refresh_token = (String) responseBody.get("refresh_token");
             */
            openid = (String) responseBody.get("openid");
            logger.info("openid=============" + openid);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Jedis jedis = jedisPool.getResource();
        String uuid = jedis.get(loginUUID + "*erweimadenglu*");
        if (uuid != null && !"".equals(uuid)) {
            jedis.set(loginUUID + "*erweimadenglu*", openid);
            jedis.close();
            return "success";
        } else {
            jedis.close();
            return "timeout";
        }
    }

    class MyTask implements Runnable {
        private String jsonString;
        private String imgName;

        public MyTask(String jsonString, String imgName) {
            this.jsonString = jsonString;
            this.imgName = imgName;
        }

        @Override
        public void run() {
            QrCodeCreateUtil.CreateQrCodeByHua(jsonString, imgName);
        }
    }

    class RocketTask implements Runnable {
        private String jsonString;

        public RocketTask(String jsonString) {
            this.jsonString = jsonString;
        }

        @Override
        public void run() {
            RocketMQUtil.producer(ipAddress, producterName, topicName, "share", jsonString);
        }
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public String getPass() {
        Random r = new Random();
        String pass = Math.abs(r.nextInt()) + "";
        if (pass.length() >= 8) {
            pass = pass.substring(0, 7);
        }
        return pass;
    }

    //没写好，有时间优化
    /*
     * public class TaskCallable implements Callable<String>{ private String
     * message; public TaskCallable(String message) { this.message = message; }
     *
     * @Override public String call() throws Exception { synchronized (this) { try {
     * Thread.sleep(3000); } catch (InterruptedException e) {
     *
     * } Jedis jedis = jedisPool.getResource(); Long time =
     * jedis.ttl(message+"*erweimadenglu*"); String loginUUID =
     * jedis.get(message+"*erweimadenglu*"); jedis.close();
     * if(time == -1) {//当 key 存在但没有设置剩余生存时间时，返回 -1,一定有生命周期，否则异常 return state =
     * "error"; }else if(time == -2) {//当 key 不存在时，返回 -2，超过了生存周期，过期 return state =
     * "timeout"; }else if(time>0) {//有生命周期 if(loginUUID!=null &&
     * !"".equals(loginUUID)) { if(!(message+"*erweimadenglu*").equals(loginUUID))
     * {//用户扫描了，因为redis中值改变了 return state = "success"; }else { runSweep(message); }
     * }else { return state = "timeout";//获取不到，超时 } } } } }
     *
     * String state = "state"; public String checkSweep(String message) {
     * List<Future> list = new ArrayList<Future>(); ExecutorService es =
     * Executors.newFixedThreadPool(10); ArrayList<Future<String>> results = new
     * ArrayList<Future<String>>();// results.add(es.submit(new
     * TaskCallable(message)));//submit返回一个Future，代表了即将要返回的结果 for (Future f : list)
     * { // 从Future对象上获取任务的返回值，并输出到控制台 try { System.out.println(">>>" +
     * f.get().toString()); logger.info(f.get().toString()); } catch
     * (InterruptedException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } catch (ExecutionException e) { // TODO Auto-generated
     * catch block e.printStackTrace(); } } }
     *
     * class checkSweep implements Runnable{
     *
     * private String message;
     *
     * public checkSweep(String message) { super(); this.message = message; }
     *
     * @Override public void run() { synchronized (this) { try { Thread.sleep(3000);
     * } catch (InterruptedException e) {
     *
     * } Jedis jedis = jedisPool.getResource(); Long time =
     * jedis.ttl(message+"*erweimadenglu*"); String loginUUID =
     * jedis.get(message+"*erweimadenglu*"); jedis.close();
     * if(time == -1) {//当 key 存在但没有设置剩余生存时间时，返回 -1,一定有生命周期，否则异常 state = "error";
     * }else if(time == -2) {//当 key 不存在时，返回 -2，超过了生存周期，过期 state = "timeout"; }else
     * if(time>0) {//有生命周期 if(loginUUID!=null && !"".equals(loginUUID)) {
     * if(!(message+"*erweimadenglu*").equals(loginUUID)) {//用户扫描了，因为redis中值改变了
     * state = "success"; }else { runSweep(message); } }else { state =
     * "timeout";//获取不到，超时 } } } }
     *
     * }
     */

    @SuppressWarnings({"rawtypes", "unchecked"})
    public HashMap checkSweep(String message) {
        HashMap map = new HashMap();
        int a = 0;
        Jedis jedis = jedisPool.getResource();
        String state = runSweep(message, jedis, a);
        if ("success".equals(state)) {
            String openid = jedis.get(message + "*erweimadenglu*");
            logger.info("openid----------------------------------------" + openid);
            User user = weChatDao.getUserId(openid);
            byte[] bytes = SerializeUtil.serialize(user);
            String str = Base64.encodeBase64String(bytes);
            if (user != null) {
                map.put("user", str);
            } else {
                System.out.println("用户不存在出现异常--------------------------------------");
                logger.info("用户不存在出现异常----------------------------------------");
            }
        }
        jedis.close();
        map.put("state", state);
        return map;
    }

    public String runSweep(String message, Jedis jedis, int a) {
        Long time = jedis.ttl(message + "*erweimadenglu*");
        String loginUUID = jedis.get(message + "*erweimadenglu*");
        System.out.println(loginUUID + "-----------------------------------------" + time + "--------------------------------------------" + a);
        if (time == -1 && loginUUID != null && !"".equals(loginUUID) && !(message + "*erweimadenglu*").equals(loginUUID)) {//(可能版本太低，有生存周期也返回-1---这面加了一步判断...)
            return "success";
        } else if (time == -1) {//当 key 存在但没有设置剩余生存时间时，返回 -1,一定有生命周期，否则异常
            return "error";
        } else if (time == -2) {//当 key 不存在时，返回 -2，超过了生存周期，过期
            return "timeout";
        } else if (time > 0) {//有生命周期
            if (loginUUID != null && !"".equals(loginUUID)) {
                if (!(message + "*erweimadenglu*").equals(loginUUID)) {//用户扫描了，因为redis中值改变了
                    return "success";
                } else {
                    try {
                        Thread.sleep(2000);
                        a++;
                        return runSweep(message, jedis, a);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                return "timeout";//获取不到，超时
            }
        }
        return "error";
    }

    @Override
    public void delLoginUUID(String loginUUID) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(loginUUID + "*erweimadenglu*");
        jedis.close();
    }

    @Override
    public void saveIP(String ipAddress, String time, int i, String object) {
        userDao.saveIP(ipAddress, time, i, object);
    }

    @Override
    public String user_seNickname(User nickname_user) {
        return userDao.user_seNickname(nickname_user);
    }

    @Override
    public List<MyPhoto> select_all(Integer userId) {
        return userDao.select_all(userId);
    }

    @Override
    public Set<String> getRoles(String username) {
        return userDao.getRoles(username);
    }

    @Override
    public Set<String> getPermissions(String username) {
        return userDao.getPermissions(username);
    }

    @Override
    public User se_user(String username) {
        return userDao.se_user(username);
    }

    @Override
    public void beachSaveSee(List<Article> articles) {
        userDao.beachSaveSee(articles);
    }

    /**
     * 保存用户的留言
     */
    @Override
    @SuppressWarnings("unchecked")
    public String save_leaveMes(String articleId, String mes, User user) {
        String userName = "游客";
        int userId = 999999;
        if (user != null) {
            userName = user.getNickname();
            userId = user.getId();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        Integer parentId = userDao.getUserIdByArticleId(articleId);
        LeaveMes leaveMes = new LeaveMes();
        leaveMes.setFromuser(userId);
        leaveMes.setTouser(parentId);
        leaveMes.setMessage(mes);
        RocketMQUtil.producer(ipAddress, producterName, topicName, "leaveMes", JSONObject.toJSONString(leaveMes));
        Jedis jedis = jedisPool.getResource();
        byte[] noticeByte = jedis.hget("notice".getBytes(), (parentId + "").getBytes());
        List<String> notices = new ArrayList<>();
        if (SerializeUtil.unserialize(noticeByte) != null) {
            notices.addAll((List<String>) SerializeUtil.unserialize(noticeByte));
            notices.add("用户：" + userName + " 在" + df.format(new Date()) + "给您留言：" + mes);
            jedis.hset("notice".getBytes(), (parentId + "").getBytes(), SerializeUtil.serialize(notices));
        } else {
            notices.add("用户：" + userName + " 在" + df.format(new Date()) + "给您留言：" + mes);
            jedis.hset("notice".getBytes(), (parentId + "").getBytes(), SerializeUtil.serialize(notices));
        }
        jedis.close();
        return "success";
    }
}

