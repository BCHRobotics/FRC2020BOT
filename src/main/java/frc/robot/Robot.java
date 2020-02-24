/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.AutoCommands;
import frc.robot.auto.Autonomous;
import frc.robot.auto.PID;
import frc.robot.subsystems.Drivetrain;

/**
 * This is FRC team 2386 code
 */
public class Robot extends TimedRobot {

  private static final boolean newBot = true; 

  public static AHRS ahrs = new AHRS(Port.kUSB);

  public static Drivetrain mDrivetrain = new Drivetrain(newBot);
  public static OI mOi = new OI();
  //public static BallHandler mBallHandler = new BallHandler();
  //public static Shooter mShooter = new Shooter();
  //public static Retriever mRetriever = new Retriever();
  //public static Climber mClimber = new Climber();
  //public static ColorWheel mColorWheel = new ColorWheel();

  public static Teleop mTeleop = new Teleop(mOi, mDrivetrain);
  public static AutoCommands mAutoCommands = new AutoCommands(ahrs, mDrivetrain, newBot);
  public static Autonomous mAutonomous = new Autonomous(mDrivetrain, mAutoCommands, ahrs);


  @Override
  public void robotInit() {

    SmartDashboard.putNumber("DrivePSet", 0);
    SmartDashboard.putNumber("DriveISet", 0);
    SmartDashboard.putNumber("DriveDSet", 0); 

    SmartDashboard.putNumber("GyroPSet", 0);
    SmartDashboard.putNumber("GyroISet", 0);
    SmartDashboard.putNumber("GyroDSet", 0);

    //Resets all devices
    //mClimber.resetEncoder();
    //mColorWheel.resetEncoderExtend();
    //mColorWheel.resetEncoderSpinner();
    mDrivetrain.resetEncoders();
    //mShooter.resetEncoderTurret();
    //mShooter.resetEncoderWheel();
    ahrs.reset();
  }

  @Override
  public void disabledPeriodic() {

    //Resets all devices
    //mClimber.resetEncoder();
    //mColorWheel.resetEncoderExtend();
    //mColorWheel.resetEncoderSpinner();
    mDrivetrain.resetEncoders();
    //mShooter.resetEncoderTurret();
    //mShooter.resetEncoderWheel();
    ahrs.reset();

    SmartDashboard.putBoolean("ENDLOOP", false);
  }


  @Override
  public void robotPeriodic() {
    mAutoCommands.setPID();
    mAutoCommands.printPID();

    SmartDashboard.putNumber("gyro", ahrs.getAngle());

    if(SmartDashboard.getBoolean("resetEncoders", false)) mDrivetrain.resetEncoders();
    if(SmartDashboard.getBoolean("resetNavX", false)) ahrs.reset();

    //mBallHandler.periodic();
    mDrivetrain.periodic();
    //mShooter.periodic();
    //mRetriever.periodic();
    //mClimber.periodic();
    //mColorWheel.periodic();
  }


  @Override
  public void autonomousInit() {
    mAutonomous.init();
  }


  @Override
  public void autonomousPeriodic() {
    mAutonomous.periodic();
  }


  @Override
  public void teleopPeriodic() {
    mTeleop.drivestick();
    mTeleop.funstick();
    mTeleop.prostick();
  }

  //PID testPID = new PID(0.018,0,0);

  @Override
  public void testPeriodic() {
    
    mAutoCommands.gyroTurn(90, 2, 250, 2000);

    //mDrivetrain.arcade(0, 0.25 * testPID.run(90, ahrs.getAngle()));

  }
}