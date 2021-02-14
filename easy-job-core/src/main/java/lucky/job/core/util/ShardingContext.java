package lucky.job.core.util;

/**
 * @author: luckylau
 * @Date: 2020/12/28 14:48
 * @Description:
 */
public class ShardingContext {

    private static InheritableThreadLocal<Sharding> contextHolder = new InheritableThreadLocal<Sharding>();

    public static Sharding getSharding() {
        return contextHolder.get();
    }

    public static void setSharding(Sharding sharding) {
        contextHolder.set(sharding);
    }

    public static class Sharding {

        private int index;  // sharding index
        private int total;  // sharding total

        public Sharding(int index, int total) {
            this.index = index;
            this.total = total;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
