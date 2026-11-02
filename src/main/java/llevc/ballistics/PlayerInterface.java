package llevc.ballistics;

public interface PlayerInterface {
    void ballistics$resetThrowCharge();
    void ballistics$startThrowCharge(int time);
    int ballistics$getThrowChargeTime();
    int ballistics$getMaxChargeTime();
    int ballistics$getMaxOvercharge();
    boolean ballistics$getUseThrowCharge();
}
