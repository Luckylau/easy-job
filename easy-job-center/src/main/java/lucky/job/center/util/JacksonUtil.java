package lucky.job.center.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: luckylau
 * @Date: 2020/12/10 19:52
 * @Description:
 */
@Slf4j
public class JacksonUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    public static String writeValueAsString(Object obj) {
        try {
            return getInstance().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("writeValueAsString error ", e);
        }
        return null;
    }

    /**
     * string --> bean、Map、List(array)
     *
     * @param jsonStr
     * @param clazz
     * @return obj
     * @throws Exception
     */
    public static <T> T readValue(String jsonStr, Class<T> clazz) {
        try {
            return getInstance().readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.error("readValue", e);
        }
        return null;
    }
}
