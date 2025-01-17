package com.nutrons.farsight.farsight;

/**
 *
 * @author Asher Gottlieb
 *
 */
public class AngleCalculator {
    private static final int CAMERA_X_OFFSET = 0;
    private static final int CAMERA_FOV = 0;
    private static final int CAMERA_PIXEL_WIDTH = 0;
    private static final int TARGET_WIDTH = 0;
    /**
     * @param cameraXAngle horizontal angle to the target from the camera
     * @return the horizontal angle to the target from the robot's center
     */
    // TODO calculate height from arm angle instead of using constant
    public static double getHorizontalAngle(double pixelWidth, double centerX){
        double measuredDistance = getCameraDist(pixelWidth);
        double distance = getRobotDist(measuredDistance, getHorizontalCameraAngle(centerX));
        return Math.toDegrees(Math.acos((Math.pow(CAMERA_X_OFFSET, 2) - Math.pow(measuredDistance, 2)
                + Math.pow(distance, 2)) / (2*distance*Math.abs(CAMERA_X_OFFSET))))-90.0;
    }
    /**
     * @param x horizontal position of target, in pixels, on the image
     * @return horizontal position of target in degrees
     */
    public static double getHorizontalCameraAngle(double x){
        double slope = CAMERA_FOV/CAMERA_PIXEL_WIDTH;
        double intercept = CAMERA_FOV/2;
        return x*slope+intercept;
    }
    /**
     * @param y vertical position of target, in pixels, on the image
     * @return vertical position of target in degrees
     */
	/*public static double getVerticalCameraAngle(double y){
    	double slope = -RobotMap.CAMERA_VERTICAL_FOV/RobotMap.CAMERA_PIXEL_HEIGHT;
        double intercept = RobotMap.CAMERA_VERTICAL_FOV/2;
        return y*slope+intercept;
    }*/

    //Will do this in CameraActivity
    /*public static boolean isTargetSeen(double x) {
        return Math.abs(getHorizontalCameraAngle(x)) != RobotMap.GRIP_IGNORE_VALUE;
    }*/


    /**
     * @param yAngle angle from the horizontal plane to the target
     * @return the distance to the target
     */
    /*public static double getCameraDist(double yAngle){
    	return (RobotMap.TARGET_HEIGHT-RobotMap.CAMERA_Z_OFFSET)/Math.tan(Math.toRadians(yAngle));
    }*/
    public static double getCameraDist(double targetPixelWidth){
        double targetAngularWidth = targetPixelWidth*CAMERA_FOV/CAMERA_PIXEL_WIDTH;
        return (TARGET_WIDTH/2.0)/Math.tan(Math.toRadians(targetAngularWidth/2.0));
    }
    /**
     * @param cameraDistance the distance from the camera to the target
     * @param cameraXAngle horizontal angle to the target from the camera
     * @return the Robot's distance to the target
     */
    @SuppressWarnings("unused")
    private static double getRobotDist(double cameraDistance, double cameraXAngle){
        double angle = 90 + ((CAMERA_X_OFFSET > 0) ? cameraXAngle : -cameraXAngle);
        if(CAMERA_X_OFFSET == 0.0){
            return cameraDistance;
        }
        return Math.sqrt(Math.pow(CAMERA_X_OFFSET,2)+Math.pow(cameraDistance,2)-(2*Math.abs(CAMERA_X_OFFSET)*cameraDistance*Math.cos(Math.toRadians(angle))));
    }

    public static void main(String[] args){
        double cameraDistance  = getCameraDist(320);
        System.out.println(cameraDistance);
    }

}