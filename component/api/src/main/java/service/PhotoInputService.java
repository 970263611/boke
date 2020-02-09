package service;

import entity.*;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PhotoInputService {

	JSONObject fileUp(String fileName, String userId, int id, String text, User user) throws IOException;
}
