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


    public RichUser(String userName, Twitter twitter) throws TwitterException {
        user = twitter.users().showUser(userName);
        userID = user.getId();
        followerCount = user.getFollowersCount();
        this.twitter = twitter;
    }

    public RichUser(long userID, Twitter twitter) throws TwitterException {
        user = twitter.users().showUser(userID);
        followerCount = user.getFollowersCount();
        this.userID = userID;
        this.twitter = twitter;
    }

    public RichUser(User user, Twitter twitter) {
        this.user = user;
        this.twitter = twitter;
        followerCount = user.getFollowersCount();
    }

    public User getUser() throws TwitterException {
        return twitter.showUser(user.getId()); //creates a copy
    }

    public double getReachCoefficient() throws TwitterException {
        if (reachCoefficient == 0) {
            return reachCoefficient = ReachCoefficient.getReachCoefficient(user, twitter);
        }
        return reachCoefficient;
    }

    public long[] getFollowers() throws TwitterException {
        if (followerIDs == null) {
            long cursor = -1;
            IDs ids;
            do {
                ids = twitter.getFollowersIDs(userID, cursor);
                long[] followerIDs1 = ids.getIDs();
                followerIDs = ArrayUtils.addAll(followerIDs, followerIDs1);
            } while ((cursor = ids.getNextCursor()) != 0);
        }
        return followerIDs;
    }

    public long getFollowerCount() {
        return followerCount;
    }
}
