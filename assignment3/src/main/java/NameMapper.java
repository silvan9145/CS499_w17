import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class NameMapper extends Mapper<LongWritable, Text, Text, Text> {
    private static HashMap<Integer, Double> map;
    private static HashMap<String, Double> namesMap;

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        map = Driver.getMoviesHashmap();
        namesMap = Driver.getMovieNamesHashmap();
        String line = value.toString();
        StringTokenizer st = new StringTokenizer(line, ",");
        int movieID = Integer.parseInt(st.nextToken());
        st.nextToken();
        String movieName = st.nextToken();
        if(map.get(Integer.valueOf(movieID)) != null) {
            if(namesMap == null) {
                namesMap = new HashMap();
            }

            namesMap.put(movieName, map.get(Integer.valueOf(movieID)));
        }

        Driver.setMovieNamesHashmap(namesMap);
    }
}