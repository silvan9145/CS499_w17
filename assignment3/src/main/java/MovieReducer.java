import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class MovieReducer extends Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable> {

    private HashMap<Integer, Double> topMovies = new HashMap();

    @Override
    protected void reduce(IntWritable i, Iterable<DoubleWritable> values, Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable>.Context context) throws IOException, InterruptedException {
        int total = 0;
        double score = 0.0D;

        for (Iterator var7 = values.iterator(); var7.hasNext(); ++total) {
            DoubleWritable val = (DoubleWritable) var7.next();
            score += val.get();
        }

        score /= (double) total;
        this.topMovies.put(Integer.valueOf(i.get()), Double.valueOf(score));
        this.checkMap();
        context.write(i, new DoubleWritable(score));
    }

    private void checkMap() {
        if (this.topMovies.size() > 10) {
            double min = 1.7976931348623157E308D;
            int i = -1;
            Iterator var4 = this.topMovies.keySet().iterator();

            while (var4.hasNext()) {
                int key = ((Integer) var4.next()).intValue();
                double val = ((Double) this.topMovies.get(Integer.valueOf(key))).doubleValue();
                if (val < min) {
                    min = val;
                    i = key;
                }
            }

            if (i != -1) {
                this.topMovies.remove(Integer.valueOf(i));
            }
        }

    }

    @Override
    protected void cleanup(Reducer<IntWritable, DoubleWritable, IntWritable, DoubleWritable>.Context context) {
        Driver.setMoviesHashmap(this.topMovies);
    }
}