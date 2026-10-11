import com.silasquercloud.megamanzeronative.TouchState;
public class TouchStateTest {
 public static void main(String[] args){TouchState s=new TouchState();s.hold(4,TouchState.direction(1,0));s.hold(9,257);check(s.mask()==273);s.hold(12,1);s.release(9);check(s.mask()==17);s.release(12);check(s.mask()==16);s.hold(8,256);s.hold(9,257);s.release(8);check(s.mask()==273);s.clear();check(s.mask()==0);check(TouchState.direction(.1f,.1f)==0);check(TouchState.direction(-1,-1)==96);check(TouchState.direction(1,.1f)==16);s.hold(90,2);s.hold(99,2);s.release(90);check(s.mask()==2);s.release(99);check(s.mask()==0);System.out.println("PASS independent pointer masks, R+A simultaneous hold/release, analog+macro+A, cancellation, deadzone, diagonals, duplicate holds");}
 static void check(boolean ok){if(!ok)throw new AssertionError();}
}
