package com.silasquercloud.megamanzeronative;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.util.SparseIntArray;
import android.app.AlertDialog;
import android.widget.*;
public final class TouchOverlay extends View {
    public static native void keys(int mask);
    private final TouchState state=new TouchState();
    private final SparseIntArray fingers=new SparseIntArray();
    private final Paint paint=new Paint();
    private final Bitmap[] art=new Bitmap[6];
    private final float[] defaults={.12f,.50f,.12f,.16f,.88f,.16f,.83f,.50f,.94f,.68f,.76f,.77f,.44f,.91f,.56f,.91f};
    private final float[] centers=new float[16];
    private final String[] names={"Analógico","L","R","B","A","Macro","SELECT","START"};
    private boolean editing=false;
    private float size=1,opacity=.7f;
    public TouchOverlay(Context c){super(c);setLayerType(View.LAYER_TYPE_HARDWARE,null);int[] ids={R.drawable.pad_walk,R.drawable.pad_dash,R.drawable.pad_guard,R.drawable.pad_attack,R.drawable.pad_jump,R.drawable.pad_elf};for(int i=0;i<6;i++)art[i]=BitmapFactory.decodeResource(getResources(),ids[i]);reload();setContentDescription("Controles multitouch de Mega Man Zero");}
    private SharedPreferences prefs(){return getContext().getSharedPreferences("launcher",0);}
    public void reload(){size=prefs().getInt("pad_size",100)/100f;opacity=prefs().getInt("pad_opacity",70)/100f;for(int i=0;i<16;i++)centers[i]=prefs().getFloat("pad_xy_"+i,defaults[i]);setVisibility(prefs().getBoolean("touch",true)?VISIBLE:GONE);releaseAll();invalidate();}
    private float radius(int i){float d=getResources().getDisplayMetrics().density;return Math.min(getHeight()*.19f,(i==0?57:i>=6?25:35)*d*size);}
    private float cx(int i){float r=radius(i);return Math.max(r,Math.min(getWidth()-r,centers[i*2]*getWidth()));}
    private float cy(int i){float r=radius(i);return Math.max(r,Math.min(getHeight()-r,centers[i*2+1]*getHeight()));}
    @Override protected void onDraw(Canvas c){super.onDraw(c);paint.setAntiAlias(true);for(int i=0;i<8;i++){
        float x=cx(i),y=cy(i),r=radius(i);paint.setStyle(Paint.Style.FILL);paint.setColor(i==3?0xFF122D4A:i==4?0xFF49141B:0xFF11151A);paint.setAlpha((int)(opacity*150));c.drawCircle(x,y,r,paint);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2*getResources().getDisplayMetrics().density);paint.setColor(editing?0xFF45DFE9:i==3?0xFF2788C8:0xFFC52C41);paint.setAlpha((int)(opacity*235));c.drawCircle(x,y,r,paint);paint.setStyle(Paint.Style.FILL);
        if(i<6){Bitmap b=art[i];float max=r*1.65f,ratio=Math.min(max/b.getWidth(),max/b.getHeight());float w=b.getWidth()*ratio,h=b.getHeight()*ratio;paint.setAlpha((int)(opacity*255));paint.setFilterBitmap(false);c.drawBitmap(b,null,new RectF(x-w/2,y-h/2,x+w/2,y+h/2),paint);}
        if(i>0){paint.setColor(Color.WHITE);paint.setAlpha((int)(opacity*255));paint.setTextAlign(Paint.Align.CENTER);paint.setTypeface(Typeface.DEFAULT_BOLD);paint.setTextSize((i>=6?11:12)*getResources().getDisplayMetrics().scaledDensity);c.drawText(i==5?(prefs().getInt("macro_mask",257)==257?"R+A*":"M*"):names[i],x,y+(i>=6?4:r*.83f),paint);}
    }}
    private int hit(float x,float y){for(int i=7;i>=0;i--)if(Math.hypot(x-cx(i),y-cy(i))<=radius(i)*1.15)return i;return -1;}
    private int mask(int button,float x,float y){if(button==0)return TouchState.direction((x-cx(0))/radius(0),(y-cy(0))/radius(0));int[] masks={0,512,256,2,1,prefs().getInt("macro_mask",257),4,8};return masks[button];}
    @Override public boolean onTouchEvent(MotionEvent e){int action=e.getActionMasked(),index=e.getActionIndex();
        if(action==MotionEvent.ACTION_CANCEL){releaseAll();return true;}
        if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_DOWN){int id=e.getPointerId(index),button=hit(e.getX(index),e.getY(index));if(button>=0)fingers.put(id,button);}
        if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_POINTER_UP){int id=e.getPointerId(index);fingers.delete(id);state.release(id);}
        else for(int k=0;k<e.getPointerCount();k++){int id=e.getPointerId(k),button=fingers.get(id,-1);if(button<0)continue;
            if(editing){centers[button*2]=Math.max(.02f,Math.min(.98f,e.getX(k)/getWidth()));centers[button*2+1]=Math.max(.02f,Math.min(.98f,e.getY(k)/getHeight()));}
            else state.hold(id,mask(button,e.getX(k),e.getY(k)));
        }
        keys(editing?0:state.mask());invalidate();return true;
    }
    public void releaseAll(){state.clear();fingers.clear();keys(0);}
    @Override protected void onDetachedFromWindow(){releaseAll();super.onDetachedFromWindow();}
    @Override public void onWindowFocusChanged(boolean focus){super.onWindowFocusChanged(focus);if(!focus)releaseAll();}
    public boolean isEditing(){return editing;}
    public void edit(){releaseAll();editing=!editing;if(!editing){SharedPreferences.Editor p=prefs().edit();for(int i=0;i<16;i++)p.putFloat("pad_xy_"+i,centers[i]);p.apply();}invalidate();}
    public static void options(Context c,LinearLayout box,VideoSettings.Changed changed){
        VideoSettings.choice(c,box,"Tamanho dos controles","pad_size",new String[]{"Pequeno 80%","Normal 100%","Grande 125%","Extra 150%"},new int[]{80,100,125,150},()->{if(VideoSettings.prefs(c).getInt("pad_size",0)==0)VideoSettings.prefs(c).edit().putInt("pad_size",100).apply();changed.run();});
        VideoSettings.choice(c,box,"Opacidade dos controles","pad_opacity",new String[]{"35%","50%","70%","90%"},new int[]{35,50,70,90},changed);
        TextView note=new TextView(c);note.setText("Macro personalizada: mantém a combinação enquanto o dedo estiver pressionando. R+A não ativa Cyber-Elf universalmente. Para mover botões, use EDITAR CONTROLES e arraste; toque SALVAR POSIÇÕES.");note.setTextColor(Color.LTGRAY);box.addView(note);
        Button macro=new Button(c);macro.setText("Remapear macro");box.addView(macro);macro.setOnClickListener(v->{String[] labels={"A","B","SELECT","START","Direita","Esquerda","Cima","Baixo","R","L"};boolean[] selected=new boolean[10];int current=VideoSettings.prefs(c).getInt("macro_mask",257);for(int i=0;i<10;i++)selected[i]=(current&(1<<i))!=0;new AlertDialog.Builder(c).setTitle("Combinação simultânea").setMultiChoiceItems(labels,selected,(d,i,on)->selected[i]=on).setPositiveButton("Salvar",(d,w)->{int mask=0;for(int i=0;i<10;i++)if(selected[i])mask|=1<<i;VideoSettings.prefs(c).edit().putInt("macro_mask",mask).apply();changed.run();}).setNegativeButton("Cancelar",null).show();});
    }
}
