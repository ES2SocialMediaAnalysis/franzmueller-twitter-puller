package ES2SocialMediaAnalysis.franzmueller.twitter_puller;

import ES2SocialMediaAnalysis.franzmueller.twitter_puller.util.ReachCoefficient;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Main {
    public static void main(String[] args) throws TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        System.out.println("franzmueller46 has " + ReachCoefficient.getReachCoefficient("franzmueller46", twitter) + " RC");
        System.out.println("janboehm has " + ReachCoefficient.getReachCoefficient("janboehm", twitter) + " RC");
        System.out.println("nike has " + ReachCoefficient.getReachCoefficient("nike", twitter) + " RC");
    }
}

