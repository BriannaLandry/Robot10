/**
 * Handles the Ball Pickup.
 */
package Team4450.Robot10;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Team4450.Lib.*;

public class BallPickup
{
	private Robot		robot;
	private Talon		motor = new Talon(0);
	
	public BallPickup(Robot robot)
	{
		Util.consoleLog();
		
		this.robot = robot;
		
		stop();
	}
	
	public void dispose()
	{
		Util.consoleLog();
		
		if (motor != null) motor.free();
	}
	
	public void start()
	{
		Util.consoleLog();
		
		SmartDashboard.putBoolean("BallPickupMotor", true);

		motor.set(.50);
	}

	public void stop()
	{
		Util.consoleLog();

		SmartDashboard.putBoolean("BallPickupMotor", false);

		motor.set(0);
	}

	public boolean isRunning()
	{
		if (motor.get() != 0)
			return true;
		else
			return false;
	}
}
