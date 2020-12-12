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
}
