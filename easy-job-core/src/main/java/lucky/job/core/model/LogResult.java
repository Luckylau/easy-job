package lucky.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:35
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogResult implements Serializable {
    private int fromLineNum;
    private int toLineNum;
    private String logContent;
    private boolean isEnd;
}
