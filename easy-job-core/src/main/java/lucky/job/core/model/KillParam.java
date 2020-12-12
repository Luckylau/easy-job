package lucky.job.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:32
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KillParam implements Serializable {
    private int jobId;
}
