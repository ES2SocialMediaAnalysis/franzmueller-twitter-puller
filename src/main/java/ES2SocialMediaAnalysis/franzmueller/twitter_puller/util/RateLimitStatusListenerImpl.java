package ES2SocialMediaAnalysis.franzmueller.twitter_puller.util;

import org.apache.logging.log4j.LogManager;
import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;


public class RateLimitStatusListenerImpl implements RateLimitStatusListener {
    @Override
    public void onRateLimitStatus(RateLimitStatusEvent rateLimitStatusEvent) {
        if (rateLimitStatusEvent.getRateLimitStatus().getRemaining() < 2) {
            try {
                LogManager.getLogger("RateLimit").info("RateLimit almost exceeded, will sleep for "
                        + (rateLimitStatusEvent.getRateLimitStatus().getSecondsUntilReset() + 2) + " seconds");
                Thread.sleep((rateLimitStatusEvent.getRateLimitStatus().getSecondsUntilReset() * 1000) + 2000);
            } catch (InterruptedException e) {
                System.err.println("Got interrupted while waiting for RateLimit!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRateLimitReached(RateLimitStatusEvent rateLimitStatusEvent) {
        LogManager.getLogger("RateLimit").fatal("RateLimit exceeded, somehow I wasn't able to catch this in time");
    }
}
