import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Driver extends Configured implements Tool {
    private static String[] inputPath;
    private static String outputPathMovies = "toptenmovies.txt";
    private static String outputPathUsers = "toptenusers.txt";
    private static HashMap<Integer, Double> moviesHashmap = null;
    private static HashMap<Integer, Integer> usersHashmap = null;
    private static HashMap<String, Double> movieNamesHashmap = null;

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Driver(), args);
        System.exit(exitCode);
    }

    public int run(String[] args) throws Exception {
        if(args.length != 2) {
            System.err.printf("Usage: %s needs 2 arguments: TrainingRatings.txt & movie_titles.txt", new Object[]{this.getClass().getSimpleName()});
            return -1;
        } else {
            inputPath = args;
            boolean movieJob = this.job("output/movie", "Top 10 Movies", DoubleWritable.class, MovieMapper.class, MovieReducer.class);
            boolean userJob = this.job("output/user", "Top 10 Users", IntWritable.class, UserMapper.class, UserReducer.class);
            if(movieJob) {
                this.nameMapperJob();
                this.outputMovies();
            }

            if(userJob) {
                this.outputUsers();
            }

            return movieJob && userJob?0:1;
        }
    }

    private boolean job(String outputPath, String jobName, Class outputValueClass, Class mapperClass, Class reducerClass) throws Exception {
        try {
            FileUtils.deleteDirectory(new File(outputPath));
        } catch (Exception var7) {
            System.out.println("job: " + var7);
        }

        Job job = new Job();
        job.setJarByClass(Driver.class);
        job.setJobName(jobName);
        FileInputFormat.addInputPath(job, new Path(inputPath[0]));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(outputValueClass);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setMapperClass(mapperClass);
        job.setReducerClass(reducerClass);
        job.waitForCompletion(true);
        return job.isSuccessful();
    }

    private boolean nameMapperJob() throws Exception {
        Job job = new Job();
        job.setJarByClass(Driver.class);
        job.setJobName("Map names to movie IDs");
        FileInputFormat.addInputPath(job, new Path(inputPath[1]));
        FileOutputFormat.setOutputPath(job, new Path("output/whatever"));
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setMapperClass(NameMapper.class);
        job.setNumReduceTasks(0);
        job.waitForCompletion(true);

        try {
            FileUtils.deleteDirectory(new File("output/whatever"));
        } catch (Exception var3) {
            System.out.println("nameMapperJob: " + var3);
        }

        return job.isSuccessful();
    }

    private void outputMovies() {
        if(moviesHashmap != null) {
            try {
                TreeMap e = new TreeMap(new CustomComparator(movieNamesHashmap));
                e.putAll(movieNamesHashmap);
                DecimalFormat df = new DecimalFormat("#.00");
                File file = new File(outputPathMovies);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("Top 10 Movies by Rating\n\n");
                Iterator var5 = e.keySet().iterator();

                while(var5.hasNext()) {
                    Object key = var5.next();
                    fileWriter.write(key + " ==> " + df.format(((Double)movieNamesHashmap.get(key)).doubleValue()) + "\n");
                }

                fileWriter.close();
            } catch (Exception var7) {
                System.out.println("outputMovies: " + var7);
            }
        }

    }

    private void outputUsers() {
        if(usersHashmap != null) {
            try {
                TreeMap e = new TreeMap(new CustomComparator(usersHashmap));
                e.putAll(usersHashmap);
                File file = new File(outputPathUsers);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("Top 10 Users by Number of Reviews\n\n");
                Iterator var4 = e.keySet().iterator();

                while(var4.hasNext()) {
                    Object key = var4.next();
                    fileWriter.write("UserID: " + key + ", Number of reviews: " + usersHashmap.get(key) + "\n");
                }

                fileWriter.close();
            } catch (Exception var6) {
                System.out.println("outputUsers: " + var6);
            }
        }

    }

    public static void setMoviesHashmap(HashMap map) {
        moviesHashmap = map;
    }

    public static void setUsersHashmap(HashMap map) {
        usersHashmap = map;
    }

    public static void setMovieNamesHashmap(HashMap map) {
        movieNamesHashmap = map;
    }

    public static HashMap getMoviesHashmap() {
        return moviesHashmap;
    }

    public static HashMap getMovieNamesHashmap() {
        return movieNamesHashmap;
    }
}