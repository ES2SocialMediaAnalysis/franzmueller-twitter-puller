package ES2SocialMediaAnalysis.franzmueller.twitter_puller;

import ES2SocialMediaAnalysis.franzmueller.twitter_puller.RichTwitterObjects.RichUser;
import ES2SocialMediaAnalysis.franzmueller.twitter_puller.util.RateLimitStatusListenerImpl;
import org.apache.logging.log4j.LogManager;
import twitter4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        int brakepoint = 1;
    }
}

