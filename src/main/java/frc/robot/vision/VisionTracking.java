/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.BallHandler;
import frc.robot.subsystems.Shooter;

/**
 * Add your docs here.
 */
public class VisionTracking {

    private double tv,tx,thor,ty;

    private double height = 73.25;
    private double a1 = 115;
    private double distance;

    private double range = 1.75;
    private double spin = 0.025;
    private double wheelRpmSet = 0;
    private double rpmRange = 100; 

    private int speed = 0;

    private double thorMulti = 158;
    private boolean goodToShoot = false;

    private int[][] thorMultiples = {
        {190, 180, 4050},
        {180, 170, 4025},
        {170, 160, 4000},
        {160, 150, 3925},
        {150, 140, 3825},
        {140, 130, 3825},
        {130, 120, 3825},
        {120, 110, 3900},
        {110, 100, 3900},
        {100, 90, 4025},
        {90, 80, 4050}
    };
    //thor = 185, 140.5inches at 4050 ref rpm

    private Shooter mShooter;
    private BallHandler mBallHandler;

    public VisionTracking(Shooter mShooter, BallHandler mBallHandler){
        this.mShooter = mShooter;
        this.mBallHandler = mBallHandler;
    }

    public int getOutputSpeed(){
        
        for(int i = 0; i < thorMultiples.length; i++){
            if(thor >= thorMultiples[i][1] && thor < thorMultiples[i][0]){
                speed = thorMultiples[i][2];
            }
        }
        if(thor < 80 || thor == 0){
            speed = 4050;
        }

        return speed;
    }

    public void justTurret(){

        periodic();

        // limelight LED turn on
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
        if(tv == 1){
            if(tx > range || tx < -range){
                mShooter.turretSpeed(tx * spin);
            }else{
                mShooter.turretSpeed(0);         
            }
        }else{
            mShooter.turretSpeed(0);
        }
    }
    
    public void turretFun(){

        periodic();

        // limelight LED turn on
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
        if(tv == 1){
            if(tx > range || tx < -range){
                mShooter.turretSpeed(tx * spin);
                mShooter.wheelSpeed(0.5);
                goodToShoot = false;
            }else{

                distance = height / (Math.tan(a1 + ty));

                wheelRpmSet = getOutputSpeed();

                //wheelRpmSet = 1/thor*thorMulti*1000;
                SmartDashboard.putNumber("wheel rpm set", wheelRpmSet);
                SmartDashboard.putNumber("distance", distance);

                mShooter.turretSpeed(0);
                //mShooter.wheelSpeed(1/thor * 32.5); //wheel spin rpm (1/60*210000)
                mShooter.setWheelSpeed(wheelRpmSet);

                if(wheelRpmSet > mShooter.getWheelRpm() - rpmRange && wheelRpmSet < mShooter.getWheelRpm() + wheelRpmSet){
                    
                    goodToShoot = true;
                    
                } else {
                    goodToShoot = false;
                    mBallHandler.delayedUnloadSet();
                }
            }
        }else{
            mShooter.turretSpeed(0);
            mShooter.wheelSpeed(0);
            goodToShoot = false; 
        }
    }  

    public void shoot(double unloadSpeed, double shootTime){

        shootTime = shootTime + System.currentTimeMillis();

        while(shootTime < System.currentTimeMillis()){

            turretFun();

            if(goodToShoot){
                //mBallHandler.delayedUnload(unloadSpeed);
                //mBallHandler.unlatch();
            }
            
        }
    }

    public void shoot(double unloadSpeed){

        turretFun();

        if(goodToShoot){
            //delayedUnload(unloadSpeed);
            //mBallHandler.unlatch();
        }

    }

    public void periodic(){

        tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
        tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        thor = NetworkTableInstance.getDefault().getTable("limelight").getEntry("thor").getDouble(0);
        ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);

        SmartDashboard.putNumber("tv",tv);
        SmartDashboard.putNumber("tx",tx);
        SmartDashboard.putNumber("thor",thor);
        SmartDashboard.putBoolean("goodtoshoot", goodToShoot);
        //thorMulti = SmartDashboard.getNumber("thorMulti", 0);

        distance = height / (Math.tan(a1 + ty));
        SmartDashboard.putNumber("distance", distance);
        SmartDashboard.putNumber("Vision: output speed", getOutputSpeed());
    }

}
