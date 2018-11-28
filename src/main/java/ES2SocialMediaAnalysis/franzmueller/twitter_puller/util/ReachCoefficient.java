package ES2SocialMediaAnalysis.franzmueller.twitter_puller.util;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class ReachCoefficient {

    public static double getReachCoefficient(String userName, Twitter twitter) throws TwitterException {
        List<Status> tweets = twitter.search(new Query("from=" + userName)).getTweets();
        if (tweets.size() == 0) return 0;
        long favCount = 0, retweetCount = 0, replyCount;
        for (Status s : tweets) {
            favCount += s.getFavoriteCount();
            retweetCount += s.getRetweetCount();
        }
        replyCount = twitter.search(new Query("q=" + userName)).getCount() - tweets.size();

        return (favCount + retweetCount + replyCount) / (double) (3 * tweets.size() * twitter.showUser(userName).getFollowersCount());
    }
}
