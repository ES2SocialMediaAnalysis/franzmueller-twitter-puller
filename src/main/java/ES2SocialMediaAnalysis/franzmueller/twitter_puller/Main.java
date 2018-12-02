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
        Query query = new Query("to:" + args[0]);
        query.count(100);
        QueryResult queryResult = null;
        do {
            queryResult = twitter.search(query);
            tweets.addAll(queryResult.getTweets());
            LogManager.getLogger("Main").debug("Found " + queryResult.getTweets().size() + " more tweets!");
        } while ((query = queryResult.nextQuery()) != null);

        LogManager.getLogger("Main").debug("Found " + tweets.size() + " total tweets!");

        //Add all users who tweeted at target
        long userID;
        for (Status tweet : tweets) {
            userID = tweet.getUser().getId();
            if (!userManager.containsKey(userID))
                userManager.put(userID, new RichUser(tweet.getUser(), twitter));
        }

        LogManager.getLogger("Main").debug("Identified " + userManager.size() + " unique users!");

        //TODO: this takes forever due to rate limit

        //Analyze all users who tweeted at target
        LogManager.getLogger("Main").debug("Calculating RC and following now and writing to files");
        RichUser user;
        Iterator<Map.Entry<Long, RichUser>> iterator = userManager.entrySet().iterator();
        BufferedWriter writerRC = new BufferedWriter(new FileWriter("rc.csv"));
        CSVPrinter csvPrinterRC = new CSVPrinter(writerRC, CSVFormat.DEFAULT);
        BufferedWriter writerE = new BufferedWriter(new FileWriter("edges.csv"));
        CSVPrinter csvPrinterE = new CSVPrinter(writerE, CSVFormat.DEFAULT);

        while (iterator.hasNext()) {
            //get user
            user = iterator.next().getValue();

        /*TODO this doesn't seem to work properly
            //print rc
            csvPrinterRC.print(user.getUser().getId());
            csvPrinterRC.print(user.getReachCoefficient());
            csvPrinterRC.println();
            LogManager.getLogger("Main").debug("Wrote user " + user.getUser().getScreenName() + " to rc file");
            csvPrinterRC.flush();
            writerRC.flush();
        */
            //print following
            long[] followers = user.getFollowers();
            if (followers != null && followers.length > 0) for (long follower : followers) {
                csvPrinterE.print(follower);
                csvPrinterE.print(user.getUser().getId());
                csvPrinterE.println();
            }
            csvPrinterE.flush();
            writerE.flush();
            LogManager.getLogger("Main").debug("Wrote user " + user.getUser().getScreenName() + " to edges file");
        }
        writerE.close();
        writerRC.close();
    }
}

