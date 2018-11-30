package ES2SocialMediaAnalysis.franzmueller.twitter_puller.util;

import twitter4j.RateLimitStatus;

public class RateLimitHandler {

    public static void handleRateLimit(RateLimitStatus status) {
        if (status.getRemaining() == 0) {
            try {
                Thread.sleep(status.getSecondsUntilReset() * 1000);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for RateLimit reset!");
                e.printStackTrace();
            }
        }
    }
}
