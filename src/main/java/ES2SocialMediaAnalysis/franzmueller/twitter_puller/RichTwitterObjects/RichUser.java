package ES2SocialMediaAnalysis.franzmueller.twitter_puller.RichTwitterObjects;

import ES2SocialMediaAnalysis.franzmueller.twitter_puller.util.ReachCoefficient;
import org.apache.commons.lang3.ArrayUtils;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class RichUser {
    protected final User user;
    protected final Twitter twitter;
    protected double reachCoefficient;
    protected long userID; //I don't know if the user object saves this field or asks for it every time.
    protected long followerCount;
    protected long[] followerIDs;


    public RichUser(String userName, Twitter twitter) throws TwitterException { //TODO dont throw
        user = twitter.users().showUser(userName);
        userID = user.getId();
        followerCount = user.getFollowersCount();
        this.twitter = twitter;
    }

    public RichUser(long userID, Twitter twitter) throws TwitterException { //TODO dont throw
        user = twitter.users().showUser(userID);
        followerCount = user.getFollowersCount();
        this.userID = userID;
        this.twitter = twitter;
    }

    public User getUser() throws TwitterException {
        return twitter.showUser(user.getId()); //creates a copy
    }

    public double getReachCoefficient() throws TwitterException { //TODO dont throw
        if (reachCoefficient == 0) {
            return reachCoefficient = ReachCoefficient.getReachCoefficient(user, twitter);
        }
        return reachCoefficient;
    }

    public long[] getFollowers() throws InterruptedException, TwitterException { //TODO dont throw
        if (followerIDs == null) {
            long cursor = -1;
            boolean runGood;
            IDs ids = null;
            do {
                runGood = false;
                try {
                    ids = twitter.getFollowersIDs(userID, cursor);
                    long[] followerIDs1 = ids.getIDs();
                    followerIDs = ArrayUtils.addAll(followerIDs, followerIDs1);
                    runGood = true;
                } catch (TwitterException e) {
                    if (e.getMessage().contains("rate limit")) {
                        System.out.println("Rate limit exceeded while retrieving followers. Will sleep for 120s.");
                        Thread.sleep(120000);
                    } else
                        throw new TwitterException(e.getMessage());
                }
            } while (runGood && ((cursor = ids.getNextCursor()) != 0)); //Takes advantage of lazy-evaluation.

        }
        return followerIDs;
    }

    public long getFollowerCount() {
        return followerCount;
    }
}
