package com.home.neil.connectfour.learninggamethread.clientthreads;

import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;

public interface ClientLearningThreadMBean {
		public void setAbort ();
		public boolean getAbort();
		public void setThrottle (long pThrottleValue);
		public long getThrottle ();
		public void togglePause();
		public boolean getPause();
		public String getThreadTimeStatistics ();
		public String getCurrentMoveEvaluated() throws IOException, ConfigurationException;
		public long getThreadMemoryFootprint();
}
