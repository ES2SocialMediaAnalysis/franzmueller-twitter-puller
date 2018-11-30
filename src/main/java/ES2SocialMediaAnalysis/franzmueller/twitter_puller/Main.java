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
        Query query = new Query("to:janboehm");
        query.count(100);
        QueryResult queryResult = null;
        do {
            queryResult = twitter.search(query);
            tweets.addAll(queryResult.getTweets());
            LogManager.getLogger().debug("Found " + queryResult.getTweets().size() + " more tweets!");
        } while ((query = queryResult.nextQuery()) != null);

        LogManager.getLogger().debug("Found " + tweets.size() + " total tweets!");

        //Add all users who tweeted at target
        long userID;
        for (Status tweet : tweets) {
            userID = tweet.getUser().getId();
            if (!userManager.containsKey(userID))
                userManager.put(userID, new RichUser(tweet.getUser(), twitter));
        }

        LogManager.getLogger().debug("Identified " + userManager.size() + " unique users!");

        /* TODO: this takes forever due to rate limit

        //Analyze all users who tweeted at target
        LogManager.getLogger().debug("Calculating RCs now");
        RichUser user;
        Iterator<Map.Entry<Long, RichUser>> iterator = userManager.entrySet().iterator();
        while (iterator.hasNext()) {
            user = iterator.next().getValue();
            user.getReachCoefficient();
            LogManager.getLogger().debug("User " + user.getUser().getScreenName() + " has " + user.getFollowerCount() + " followers " +
                    "and a RC of " + user.getReachCoefficient());
        }

        */

        //Write csv file
        RichUser user;
        Iterator<Map.Entry<Long, RichUser>> iterator = userManager.entrySet().iterator();
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.csv"));
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        while (iterator.hasNext()) {
            user = iterator.next().getValue();
            csvPrinter.printRecord(user.getUser().getId());
            long[] followers = user.getFollowers();
            for (long follower : followers) csvPrinter.printRecord(follower);
            csvPrinter.println();
            csvPrinter.flush();
            writer.flush();
        }
        csvPrinter.close();
        writer.close();


        int brakepoint = 1;
    }
}

