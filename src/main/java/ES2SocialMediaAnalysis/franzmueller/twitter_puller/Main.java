package ES2SocialMediaAnalysis.franzmueller.twitter_puller;

import ES2SocialMediaAnalysis.franzmueller.twitter_puller.RichTwitterObjects.RichUser;
import ES2SocialMediaAnalysis.franzmueller.twitter_puller.util.RateLimitHandler;
import twitter4j.*;

import java.util.*;

/**
 * @author franzmueller
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //Init
        Twitter twitter = TwitterFactory.getSingleton();
        HashMap<Long, RichUser> userManager = new HashMap<>();

        //Get Tweets
        List<Status> tweets = new ArrayList<>();
        Query query = new Query("to:janboehm");
        query.count(100);
        QueryResult queryResult = null;
        boolean runGood;
        do {
            runGood = false;
            try {
                if (queryResult != null) RateLimitHandler.handleRateLimit(queryResult.getRateLimitStatus());
                queryResult = twitter.search(query);
                tweets.addAll(queryResult.getTweets());
                runGood = true;
            } catch (TwitterException e) {
                if (e.getMessage().contains("rate limit")) {
                    System.out.println("Rate limit exceeded while retrieving followers. Will sleep for 120s.");
                    Thread.sleep(120000);
                } else
                    throw new Exception(e.getMessage());
            }

        } while (runGood && ((query = queryResult.nextQuery()) != null));

        //Add all users who tweeted at target
        long userID;
        for (Status tweet : tweets) {
            userID = tweet.getUser().getId();
            if (!userManager.containsKey(userID))
                userManager.put(userID, new RichUser(userID, twitter));
        }

        //Analyze all users who tweeted at target
        RichUser user;
        Iterator<Map.Entry<Long, RichUser>> iterator = userManager.entrySet().iterator();
        while (iterator.hasNext()) {
            user = iterator.next().getValue();
            user.getReachCoefficient();
            System.out.println("User " + user.getUser().getScreenName() + " has " + user.getFollowerCount() + " followers " +
                    "and a RC of " + user.getReachCoefficient());
        }

        int breakpoint = 1;
    }
}

