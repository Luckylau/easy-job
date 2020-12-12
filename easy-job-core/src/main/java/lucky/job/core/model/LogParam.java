package lucky.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:34
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogParam implements Serializable {

    private long logDateTim;
    private long logId;
    private int fromLineNum;
}
