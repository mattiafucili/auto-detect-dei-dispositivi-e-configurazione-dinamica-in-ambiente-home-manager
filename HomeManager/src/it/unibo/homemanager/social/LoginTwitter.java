package it.unibo.homemanager.social;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class LoginTwitter 
{
	private LoginTwitter() {}
	
	
	protected static Twitter login(String ck, String cks, String at, String ats)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(ck)
		.setOAuthConsumerSecret(cks)
		.setOAuthAccessToken(at)
		.setOAuthAccessTokenSecret(ats);
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}
}
