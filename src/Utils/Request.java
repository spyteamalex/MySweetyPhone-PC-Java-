package Utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

public abstract class Request {
    protected JSONObject result;
    public String Start(String address, MultipartEntity entity){
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(address);
            post.setEntity(entity);
            String response = EntityUtils.toString(client.execute(post).getEntity(), "UTF-8");
            result = (JSONObject) JSONValue.parse(response);
            int i = ((Long) result.getOrDefault("code", 2)).intValue();
            switch (i){
                case 0:
                    On0();
                    break;
                case 1:
                    On1();
                    break;
                default:
                case 2:
                    On2();
                    break;
                case 3:
                    On3();
                    break;
                case 4:
                    On4();
                    break;
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected abstract void On0();
    protected abstract void On1();
    protected abstract void On2();
    protected abstract void On3();
    protected abstract void On4();
}
