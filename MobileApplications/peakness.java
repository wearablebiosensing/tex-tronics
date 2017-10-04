/**
  * The Peakness can be used to determine peak locations in short time-series data.
  * 
  * The class must be initialized with:
  *		- a starting average (can be first data point),
  * 	- a Threshold.
  *
  * The method for determining the instance of a peak is peakDect and returns a boolean.
  *
  * This works using a Rolling average and as such must be feed the newest data point.
 */
public class Peakness {
        
    /** 
    * the Peakness class has
    * three fields
    *
    * @param avg
    * @param threshold
    * @param oldSmoothedValue
    * @param oldSmoothedDiff
	*/ 
    public double avg;
    public double threshold;
    private double oldSmoothedValue;
    private double oldSmoothedDiff;
        
    /**
    * the Peakness class has
    * one constructor
    *
    * @param avg
    * @param threshold
    * @param oldSmoothedValue
    * @param oldSmoothedDiff
    */
    public Peakness(double startAvg, double startThreshold) {
        avg = startAvg;
        threshold = startThreshold;
        oldSmoothedValue = avg;
        oldSmoothedDiff = avg;
    }

	/**
	* the Peakness class method approxMovingAverage will append
	* new sample to old average
    *
    *@param avg
    *@param new_sample
    *@return new average
    */
    private double approxMovingAverage(double avg, double new_sample) {

		avg -= avg / N;
		avg += new_sample / N;

		return avg;
	}
        
    /**
    * the Peakness class method discDiff computes the discrete differential
    * This is essentially just the difference between to old value and the new value
    *
    *@param y0
    *@param y1
    *@param t0
    *@param t1
    *@return new difference
    */
    private double discDiff(double y0, double y1, double t0, double t1) {
		return (y1 - y0)/(t1 - t0);
	}
	
	/**
	*the Peakness class method peakDect determines if latest value is a peak
	*based on averaged old values and new value.
	*
	*@param newValue
	*@param threshold
	*@return the boolean for a peak
	*/    
    public boolean peakDect(double newValue, double threshold) {
        % Run the signal through a moving-average filter to smooth it out.
		newSmoothedValue = approxMovingAverage(oldSmoothedValue, newValue);

		% Find the discrete differential of this filtered signal.
		newDiff = (oldSmoothedValue, newSmoothedValue, 0, 1);

		% Run the discrete differential through another moving average filter. 
		newSmoothedDiff = (oldSmoothedDiff, newDiff);

		% At each zero-crossings in the smoothed differential signal, 
		if((newSmoothedDiff*oldSmoothedDiff) < 0) {
			% compare the current unfiltered point to current filtered point 
			% If this point is greater than some predetermined threshold, n is your big spike.	
			if(newValue - newSmoothedDiff > threshold) {
				oldSmoothedValue = newSmoothedValue;
				oldSmoothedDiff = newSmoothedDiff;
				return true;
			}
		}
		oldSmoothedValue = newSmoothedValue;
		oldSmoothedDiff = newSmoothedDiff;
		return false;
    }
        
}

