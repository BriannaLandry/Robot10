/**
 * Handles the Gear pickup.
 */
package Team4450.Robot10;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Team4450.Lib.LCD;
import Team4450.Lib.Util;
import Team4450.Lib.ValveDA;
import Team4450.Lib.JoyStick.JoyStickButtonIDs;

public class GearPickup
{
	private Robot		robot;
	private Teleop		teleop;
	private CANTalon	motor = new CANTalon(7);
	private boolean		pickupDown = true, pickupOut = true;
	private ValveDA		pickupLiftValve = new ValveDA(6);
	private ValveDA		pickupDeployValve = new ValveDA(1, 0);
	private Thread		autoPickupThread;
	
	public GearPickup(Robot robot, Teleop teleop)
	{
		Util.consoleLog();
		
		this.robot = robot;
		this.teleop = teleop;
		
		stopMotor();
		
		pickupIn();
		
		raisePickup();
	}
	
	public void dispose()
	{
		Util.consoleLog();
		
		//if (motor != null) motor.free();
		if (pickupLiftValve != null) pickupLiftValve.dispose();
		if (pickupDeployValve != null) pickupDeployValve.dispose();
	}
	
	public void startMotorIn()
	{
		Util.consoleLog();

		SmartDashboard.putBoolean("GearPickupMotor", true);

		motor.set(.50);
	}
	
	public void startMotorOut()
	{
		Util.consoleLog();

		SmartDashboard.putBoolean("GearPickupMotor", true);
		
		motor.set(-.50);
	}

	public void stopMotor()
	{
		Util.consoleLog();

		//if (teleop != null) teleop.utilityStick.FindButton(JoyStickButtonIDs.TOP_LEFT).latchedState = false;

		SmartDashboard.putBoolean("GearPickupMotor", false);
		
		motor.set(0);
	}

	public boolean isRunning()
	{
		if (motor.get() != 0)
			return true;
		else
			return false;
	}
	
	public void lowerPickup()
	{
		Util.consoleLog();

		if (isPickupDown()) return;
		
		pickupDown = true;
		
		pickupLiftValve.SetB();
		
		SmartDashboard.putBoolean("GearPickupDown", pickupDown);
	}
	
	public void raisePickup()
	{
		Util.consoleLog();

		if (!isPickupDown()) return;
		
		pickupDown = false;
		
		pickupLiftValve.SetA();
		
		SmartDashboard.putBoolean("GearPickupDown", pickupDown);
	}
	
	public boolean isPickupDown()
	{
		return pickupDown;
	}
	
	public void pickupIn()
	{
		Util.consoleLog();

		if (!isPickupOut()) return;
		
		pickupOut = false;
		
		pickupDeployValve.SetA();
		
		SmartDashboard.putBoolean("GearPickupDown", pickupOut);
	}
	
	public void pickupOut()
	{
		Util.consoleLog();

		if (isPickupOut()) return;
		
		pickupOut = true;
		
		pickupDeployValve.SetB();
		
		SmartDashboard.putBoolean("GearPickupDown", pickupOut);
	}
	
	public boolean isPickupOut()
	{
		return pickupOut;
	}

	/**
	 * Start auto gear pickup thread.
	 */
	public void StartAutoPickup()
	{
		Util.consoleLog();
		
		if (autoPickupThread != null) return;

		autoPickupThread = new AutoPickup();
		autoPickupThread.start();
	}
	
	/**
	 * Stops auto pickup thread.
	 */
	public void StopAutoPickup()
	{
		Util.consoleLog();

		if (autoPickupThread != null) autoPickupThread.interrupt();
		
		autoPickupThread = null;
	}

	//----------------------------------------
	// Automatic ball pickup thread.
	
	private class AutoPickup extends Thread
	{
		AutoPickup()
		{
			Util.consoleLog();
			
			this.setName("AutoGearPickup");
	    }
		
	    public void run()
	    {
	    	Util.consoleLog();
	    	
	    	try
	    	{
	    		lowerPickup();
	    		sleep(250);
	    		pickupOut();
	    		sleep(250);
	    		startMotorIn();
	    		sleep(250);
	    		
    	    	while (!isInterrupted() && motor.getOutputCurrent() < 5.0)
    	    	{
    	            // We sleep since JS updates come from DS every 20ms or so. We wait 50ms so this thread
    	            // does not run at the same time as the teleop thread.
    	    		LCD.printLine(7, "gearmotor current=%f", motor.getOutputCurrent());
    	            sleep(50);
    	    	}
	    	}
	    	catch (InterruptedException e) {}
	    	catch (Throwable e) {e.printStackTrace(Util.logPrintStream);}

	    	stopMotor();
	    	pickupIn();
			raisePickup();
			
			autoPickupThread = null;
	    }
	}	// end of AutoPickup thread class.

}
