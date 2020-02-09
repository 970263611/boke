package util;


import entity.Article;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class RedisUtil {

    /**
     * 在redis中批量装配文章信息
     */
    public static void hsetArticle(List<Article> list, Jedis jedis) {
        for (Article ac : list) {
            jedis.lpush("article", ac.getCreate_time() + "*" + ac.getId());
            if (notEnpty(ac.getId() + "")) {
                jedis.hset("article_" + ac.getId() + "", "id", ac.getId() + "");
                jedis.hset("article_" + ac.getId() + "", "title", ac.getTitle() + "");
                jedis.hset("article_" + ac.getId() + "", "content", ac.getContent() + "");
                jedis.hset("article_" + ac.getId() + "", "lead", ac.getLead() + "");
                jedis.hset("article_" + ac.getId() + "", "create_user", ac.getCreate_user() + "");
                jedis.hset("article_" + ac.getId() + "", "create_time", ac.getCreate_time() + "");
                jedis.hset("article_" + ac.getId() + "", "type", ac.getType() + "");
                jedis.hset("article_" + ac.getId() + "", "isdelete", ac.getIsdelete() + "");
                jedis.hset("article_" + ac.getId() + "", "top", ac.getTop() + "");
                jedis.hset("article_" + ac.getId() + "", "see", ac.getSee() + "");
            }
        }
    }

    /**
     * 在redis中批量装配置顶文章信息
     */
    public static void hsetTopArticle(List<Article> list, Jedis jedis) {
        for (Article ac : list) {
            jedis.lpush("top", ac.getCreate_time() + "*" + ac.getId());
            if (notEnpty(ac.getId() + "")) {
                jedis.hset("article_" + ac.getId() + "", "id", ac.getId() + "");
                jedis.hset("article_" + ac.getId() + "", "title", ac.getTitle() + "");
                jedis.hset("article_" + ac.getId() + "", "content", ac.getContent() + "");
                jedis.hset("article_" + ac.getId() + "", "lead", ac.getLead() + "");
                jedis.hset("article_" + ac.getId() + "", "create_user", ac.getCreate_user() + "");
                jedis.hset("article_" + ac.getId() + "", "create_time", ac.getCreate_time() + "");
                jedis.hset("article_" + ac.getId() + "", "type", ac.getType() + "");
                jedis.hset("article_" + ac.getId() + "", "isdelete", ac.getIsdelete() + "");
                jedis.hset("article_" + ac.getId() + "", "top", ac.getTop() + "");
                jedis.hset("article_" + ac.getId() + "", "see", ac.getSee() + "");
            }
        }
    }

    /**
     * 在redis中批量拆解文章信息
     */
    public static List<Article> hgetArticle(List<String> list, Jedis jedis) {
        List<Article> articles = new ArrayList<Article>();
        for (String a : list) {
            Article article = new Article();
            article.setId(Integer.parseInt(jedis.hget("article_" + a.split("\\*")[1], "id")));
            article.setTitle(jedis.hget("article_" + a.split("\\*")[1], "title"));
            article.setContent(jedis.hget("article_" + a.split("\\*")[1], "content"));
            article.setLead(jedis.hget("article_" + a.split("\\*")[1], "lead"));
            article.setCreate_user(jedis.hget("article_" + a.split("\\*")[1], "create_user"));
            article.setCreate_time(jedis.hget("article_" + a.split("\\*")[1], "create_time"));
            article.setType(jedis.hget("article_" + a.split("\\*")[1], "type"));
            article.setIsdelete(jedis.hget("article_" + a.split("\\*")[1], "isdelete"));
            article.setTop(jedis.hget("article_" + a.split("\\*")[1], "top"));
            if (jedis.hget("article_" + a.split("\\*")[1], "see") == null) {
                article.setSee(0);
            } else {
                article.setSee(Integer.parseInt(jedis.hget("article_" + a.split("\\*")[1], "see")));
            }
            articles.add(article);
        }
        return articles;
    }

    /**
     * 在redis中单条拆解文章信息
     */
    public static Article hgetArticleSingle(String a, Jedis jedis) {
        Article article = new Article();
        article.setId(Integer.parseInt(jedis.hget("article_" + a, "id")));
        article.setTitle(jedis.hget("article_" + a, "title"));
        article.setContent(jedis.hget("article_" + a, "content"));
        article.setLead(jedis.hget("article_" + a, "lead"));
        article.setCreate_user(jedis.hget("article_" + a, "create_user"));
        article.setCreate_time(jedis.hget("article_" + a, "create_time"));
        article.setType(jedis.hget("article_" + a, "type"));
        article.setIsdelete(jedis.hget("article_" + a, "isdelete"));
        article.setTop(jedis.hget("article_" + a, "top"));
        if (jedis.hget("article_" + a, "see") == null) {
            article.setSee(0);
        } else {
            article.setSee(Integer.parseInt(jedis.hget("article_" + a, "see")));
        }
        return article;
    }

    /**
     * 在redis中单条装配文章信息
     */
    public static void hsetArticleSingle(Article ac, Jedis jedis) {
        jedis.lpush("article", ac.getCreate_time() + "*" + ac.getId());
        if (notEnpty(ac.getId() + "")) {
            jedis.hset("article_" + ac.getId() + "", "id", ac.getId() + "");
            jedis.hset("article_" + ac.getId() + "", "title", ac.getTitle() + "");
            jedis.hset("article_" + ac.getId() + "", "content", ac.getContent() + "");
            jedis.hset("article_" + ac.getId() + "", "lead", ac.getLead() + "");
            jedis.hset("article_" + ac.getId() + "", "create_user", ac.getCreate_user() + "");
            jedis.hset("article_" + ac.getId() + "", "create_time", ac.getCreate_time() + "");
            jedis.hset("article_" + ac.getId() + "", "type", ac.getType() + "");
            jedis.hset("article_" + ac.getId() + "", "isdelete", ac.getIsdelete() + "");
            jedis.hset("article_" + ac.getId() + "", "top", ac.getTop() + "");
            jedis.hset("article_" + ac.getId() + "", "see", ac.getSee() + "");
        }
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean notEnpty(String str) {
        if (str != null && !str.equals("")) {
            return true;
        } else {
            return false;
        }
    }

}
