package ES2SocialMediaAnalysis.franzmueller.twitter_puller;

import ES2SocialMediaAnalysis.franzmueller.twitter_puller.RichTwitterObjects.RichUser;
import ES2SocialMediaAnalysis.franzmueller.twitter_puller.util.RateLimitStatusListenerImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import twitter4j.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * @author franzmueller
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //Init
        Twitter twitter = TwitterFactory.getSingleton();
        HashMap<Long, RichUser> userManager = new HashMap<>();

        twitter.addRateLimitStatusListener(new RateLimitStatusListenerImpl());

        //Get Tweets
        List<Status> tweets = new ArrayList<>();
        Query query = new Query("to:DHLPaket");
        query.count(5);
        QueryResult queryResult = null;
        //TODO do {
            queryResult = twitter.search(query);
            tweets.addAll(queryResult.getTweets());
            LogManager.getLogger().debug("Found " + queryResult.getTweets().size() + " more tweets!");
        //TODO } while ((query = queryResult.nextQuery()) != null);

        LogManager.getLogger().debug("Found " + tweets.size() + " total tweets!");

        //Add all users who tweeted at target
        long userID;
        for (Status tweet : tweets) {
            userID = tweet.getUser().getId();
            if (!userManager.containsKey(userID))
                userManager.put(userID, new RichUser(tweet.getUser(), twitter));
        }

        LogManager.getLogger().debug("Identified " + userManager.size() + " unique users!");

        //TODO: this takes forever due to rate limit

        //Analyze all users who tweeted at target
        LogManager.getLogger().debug("Calculating RC and following now and writing to files");
        RichUser user;
        Iterator<Map.Entry<Long, RichUser>> iterator = userManager.entrySet().iterator();
        BufferedWriter writerRC = new BufferedWriter(new FileWriter("rc.csv"));
        CSVPrinter csvPrinterRC = new CSVPrinter(writerRC, CSVFormat.DEFAULT);
        BufferedWriter writerF = new BufferedWriter(new FileWriter("following.csv"));
        CSVPrinter csvPrinterF = new CSVPrinter(writerF, CSVFormat.DEFAULT);
        while (iterator.hasNext()) {
            //get user
            user = iterator.next().getValue();

            //print rc
            csvPrinterRC.print(user.getUser().getId());
            csvPrinterRC.print(user.getReachCoefficient());
            csvPrinterRC.println();
            LogManager.getLogger().debug("Wrote user " + user.getUser().getScreenName() + " to rc file");

            //print following
            csvPrinterF.print(user.getUser().getId());
            long[] followers = user.getFollowers();
            for (long follower : followers) csvPrinterF.print(follower);
            csvPrinterF.println();
            LogManager.getLogger().debug("Wrote user " + user.getUser().getScreenName() + " to following file");
        }

        csvPrinterF.flush();
        writerF.flush();
        writerF.close();
        csvPrinterRC.flush();
        writerRC.flush();
        writerRC.close();
    }
}

