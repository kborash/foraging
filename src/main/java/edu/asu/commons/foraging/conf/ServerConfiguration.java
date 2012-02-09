package edu.asu.commons.foraging.conf;

import java.text.NumberFormat;

import org.stringtemplate.v4.ST;

import edu.asu.commons.conf.ExperimentConfiguration;
import edu.asu.commons.foraging.model.ClientData;

/**
 * $Id$
 * 
 * Contains the know-how for parsing and programmatically accessing the
 * server's configuration file properties. The forager server's config file
 * specifies per-server settings, per-round settings are managed by
 * RoundConfiguration.
 *
 * FIXME: Recoverable exceptions that are handled shouldn't spit out their
 * stack trace without some additional info stating that they are mostly
 * harmless.
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @see 
 * @version $Revision$
 */
public class ServerConfiguration extends ExperimentConfiguration.Base<RoundConfiguration> {
    
    private static final long serialVersionUID = -1737412253553943902L;
    
    private static final double DEFAULT_SHOW_UP_PAYMENT = 5.0d;
    private static final double DEFAULT_QUIZ_CORRECT_ANSWER_REWARD = 0.50d;
    private static final String SAME_ROUND_AS_PREVIOUS_INSTRUCTIONS = "<h3>Round {self.roundNumber} Instructions</h3><hr><p>Round {self.roundNumber} is the same as the previous round.</p><p>The length of this round is {duration}.</p><p>If you have any questions please raise your hand.  <b>Do you have any questions so far?</b></p>";
    private static final String DEFAULT_LOG_FILE_DESTINATION = "foraging-server.log";
    private static final double DEFAULT_DOLLARS_PER_TOKEN = .02d;
    
    public ServerConfiguration() {
        super();
    }
    
    public ServerConfiguration(String configurationDirectory) {
        super(configurationDirectory);
    }
    
    @Override
    protected RoundConfiguration createRoundConfiguration(String roundConfigurationResource) {
        return new RoundConfiguration(roundConfigurationResource);
    }
    
    public String getLogFileDestination() {
        return getStringProperty("log", DEFAULT_LOG_FILE_DESTINATION);
    }

    public boolean shouldUpdateFacilitator() {
        return getBooleanProperty("update-facilitator", false);
    }
    
    public boolean isCensoredChat() {
        return getBooleanProperty("censored-chat-enabled", false);
    }
    
    public boolean shouldInitialize3D() {
        for (RoundConfiguration configuration : getAllParameters()) {
            if (configuration.is3dExperiment()) {
                return true;
            }
        }
        return false;
    }
    public boolean shouldInitialize2D() {
        for (RoundConfiguration configuration : getAllParameters()) {
            if (configuration.is2dExperiment()) {
                return true;
            }
        }
        return false;
    }
    
    public double getShowUpPayment() {
        return getDoubleProperty("show-up-payment", DEFAULT_SHOW_UP_PAYMENT);
    }

    public double getQuizCorrectAnswerReward() {
        return getDoubleProperty("quiz-correct-answer-reward", DEFAULT_QUIZ_CORRECT_ANSWER_REWARD);
    }
    
    public String getWelcomeInstructions() {
        return getStringProperty("welcome-instructions", "Please wait quietly and do not open or close any programs on this computer.");
    }
    
    public String getGeneralInstructions() {
        ST st = createStringTemplate(getStringProperty("general-instructions"));
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        st.add("showUpPayment", formatter.format(getShowUpPayment()));
        st.add("dollarsPerToken", formatter.format(getDollarsPerToken()));
        return st.render();
    }
    
    public String getSameAsPreviousRoundInstructions() {
        return getStringProperty("same-as-previous-round-instructions", SAME_ROUND_AS_PREVIOUS_INSTRUCTIONS);
    }
    
    public String getFieldOfVisionInstructions() {
        return getProperty("field-of-vision-instructions", 
                "Your view of the resource will be limited in this round.  The area visible to you will be shaded.");
    }
    
    public boolean shouldAskForSurveyId() {
        return getBooleanProperty("survey-id-enabled");
    }

    public double getDollarsPerToken() {
        return DEFAULT_DOLLARS_PER_TOKEN;
    }
    
    public String getFinalRoundFacilitatorInstructions() {
    	return getProperty("facilitator-payment-instructions", 
    			"<h3>The experiment has ended and participant payments are listed above.  We recommend that you copy and paste it into a text editor for your records.</h3>");
    }

    public String getSurveyInstructions() {
        return getProperty("survey-instructions");
    }

    public String getFacilitatorDebriefing() {
        return getProperty("facilitator-debriefing");
    }

    public String getClientDebriefing() {
        return getProperty("client-debriefing");
    }

    public double getTotalIncome(ClientData data) {
        return getTotalIncome(data, false);
    }

    public double getTotalIncome(ClientData data, boolean includeTrustGame) {
        double totalIncome = data.getTotalIncome() + getShowUpPayment() + getQuizEarnings(data);
        return (includeTrustGame) ? totalIncome + data.getTrustGameIncome() : totalIncome;
    }

    public double getQuizEarnings(ClientData data) {
        return data.getCorrectQuizAnswers() * getQuizCorrectAnswerReward();
    }

	public String getInitialVotingInstructions() {
		return getProperty("initial-voting-instructions");
	}
	
	public String getVotingResults() {
	    return getProperty("voting-results");
	}
	
	public int getServerSleepInterval() {
	    return getIntProperty("server-sleep-interval", 50);
	}

	public String getWaitingRoomInstructions() {
		return getProperty("waiting-room-instructions", "<h1>Please wait</h1><hr><p>Please wait while the rest of the participants complete the task.</p>");
	}

}
