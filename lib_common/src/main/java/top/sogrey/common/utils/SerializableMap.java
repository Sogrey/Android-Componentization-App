package top.sogrey.common.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * 序列化的map
 * Created by Sogrey on 2018/6/13.
 */

public class SerializableMap implements Serializable {
    private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
