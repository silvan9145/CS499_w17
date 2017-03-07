import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class UserReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

    private HashMap<Integer, Integer> topUsers = new HashMap();

    @Override
    protected void reduce(IntWritable i, Iterable<IntWritable> values, Reducer<IntWritable, IntWritable, IntWritable, IntWritable>.Context context) throws IOException, InterruptedException {
        int total = 0;

        IntWritable val;
        for(Iterator var5 = values.iterator(); var5.hasNext(); total += val.get()) {
            val = (IntWritable)var5.next();
        }

        this.topUsers.put(Integer.valueOf(i.get()), Integer.valueOf(total));
        this.checkMap();
        context.write(i, new IntWritable(total));
    }

    private void checkMap() {
        if(this.topUsers.size() > 10) {
            double min = 1.7976931348623157E308D;
            int i = -1;
            Iterator var4 = this.topUsers.keySet().iterator();

            while(var4.hasNext()) {
                int key = ((Integer)var4.next()).intValue();
                double val = (double)((Integer)this.topUsers.get(Integer.valueOf(key))).intValue();
                if(val < min) {
                    min = val;
                    i = key;
                }
            }

            if(i != -1) {
                this.topUsers.remove(Integer.valueOf(i));
            }
        }

    }

    @Override
    protected void cleanup(Reducer<IntWritable, IntWritable, IntWritable, IntWritable>.Context context) {
        Driver.setUsersHashmap(this.topUsers);
    }
}