package llevc.ballistics;

public interface PlayerInterface {
    void ballistics$resetThrowCharge();
    void ballistics$startThrowCharge(int time);
    int ballistics$getThrowChargeTime();
    boolean ballistics$getUseThrowCharge();
}
