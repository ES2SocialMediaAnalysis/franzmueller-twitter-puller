package ES2SocialMediaAnalysis.franzmueller.twitter_puller;

import ES2SocialMediaAnalysis.franzmueller.twitter_puller.RichTwitterObjects.RichUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * @author franzmueller
 */
public class Main {
    public static void main(String[] args) throws TwitterException, InterruptedException {
        Twitter twitter = TwitterFactory.getSingleton();

        RichUser franzmueller46 = new RichUser("franzmueller46", twitter);

        System.out.println("franzmueller46 has " + franzmueller46.getReachCoefficient() + " rc");

        long[] followers = franzmueller46.getFollowers();

        System.out.println("followers: ");
        for (long l : followers) System.out.println(l);
    }
}

