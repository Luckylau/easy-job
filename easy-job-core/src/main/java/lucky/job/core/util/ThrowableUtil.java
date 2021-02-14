package lucky.job.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author: luckylau
 * @Date: 2021/1/11 09:59
 * @Description:
 */
public class ThrowableUtil {

    /**
     * parse error to string
     *
     * @param e
     * @return
     */
    public static String toString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
