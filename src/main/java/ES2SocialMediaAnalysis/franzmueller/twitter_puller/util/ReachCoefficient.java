package ES2SocialMediaAnalysis.franzmueller.twitter_puller.util;

import twitter4j.*;

import java.util.List;

/**
 * Class to compute ReachCoefficient as defined by Stüber (2017).
 *
 * @author franzmueller
 */
public class ReachCoefficient {

    public static double getReachCoefficient(int followerCount, List<Status> tweets, List<Status> mentions) {
        if (tweets.size() == 0) return 0;
        long favCount = 0, retweetCount = 0, replyCount;
        for (Status s : tweets) {
            favCount += s.getFavoriteCount();
            retweetCount += s.getRetweetCount();
        }
        replyCount = mentions.size() - tweets.size();

        return (favCount + retweetCount + replyCount) / (double) (3 * tweets.size() * followerCount);
    }

    public static double getReachCoefficient(String userName, Twitter twitter) throws TwitterException {
        List<Status> tweets = twitter.search(new Query("from=" + userName)).getTweets();
        List<Status> mentions = twitter.search(new Query("q=" + userName)).getTweets();
        int followerCount = twitter.showUser(userName).getFollowersCount();
        return getReachCoefficient(followerCount, tweets, mentions);
    }

    public static double getReachCoefficient(User user, Twitter twitter) throws TwitterException {
        List<Status> tweets = twitter.search(new Query("from=" + user.getName())).getTweets();
        List<Status> mentions = twitter.search(new Query("q=" + user.getName())).getTweets();
        int followerCount = user.getFollowersCount();
        return getReachCoefficient(followerCount, tweets, mentions);
    }
}
