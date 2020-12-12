package lucky.job.center.exception;

/**
 * @author: luckylau
 * @Date: 2020/12/10 19:51
 * @Description:
 */
public class EasyJobException extends RuntimeException {
    public EasyJobException() {
    }

    public EasyJobException(String message) {
        super(message);
    }
}
