package com.silasquercloud.megamanzeronative;
import java.util.HashMap;
/** Every finger owns its mask. Releasing a macro cannot release another finger. */
public final class TouchState {
    private final HashMap<Integer,Integer> masks=new HashMap<>();
    public void hold(int pointer,int mask){masks.put(pointer,mask&1023);}
    public void release(int pointer){masks.remove(pointer);}
    public void clear(){masks.clear();}
    public int mask(){int value=0;for(int m:masks.values())value|=m;return value;}
    public static int direction(float x,float y){if(x*x+y*y<.22f*.22f)return 0;int m=0;if(Math.abs(x)>=Math.abs(y)*.4142f)m|=x>0?16:32;if(Math.abs(y)>=Math.abs(x)*.4142f)m|=y>0?128:64;return m;}
}
