package com.silasquercloud.megamanzeronative;
import android.graphics.*;
import android.graphics.drawable.Drawable;
/** Chamfered console plating, accent light rails and engraved circuits. */
final class TechDrawable extends Drawable {
    private final boolean red;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    TechDrawable(boolean red){this.red=red;}
    @Override public void draw(Canvas c){
        Rect b=getBounds();float w=b.width(),h=b.height(),cut=Math.min(h*.16f,14),in=2;
        c.save();c.translate(b.left,b.top);
        Path p=new Path();p.moveTo(cut,in);p.lineTo(w-cut,in);p.lineTo(w-in,cut);p.lineTo(w-in,h-cut);p.lineTo(w-cut,h-in);p.lineTo(cut,h-in);p.lineTo(in,h-cut);p.lineTo(in,cut);p.close();
        paint.setStyle(Paint.Style.FILL);paint.setShader(new LinearGradient(0,0,w,h,red?0xFF52000E:0xFF171B22,0xFF090A0E,Shader.TileMode.CLAMP));c.drawPath(p,paint);paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.6f);paint.setColor(red?0xFFFD2046:0xFF596772);c.drawPath(p,paint);
        paint.setStrokeWidth(1);paint.setColor(red?0xFF78243A:0xFF2A3540);c.drawLine(cut+3,5,w-cut-3,5,paint);c.drawLine(5,cut+3,5,h-cut-3,paint);
        paint.setColor(red?0xFFFF244A:0xFF0AB9DC);paint.setStrokeWidth(red?3:2);c.drawLine(7,h-cut,cut+4,h-7,paint);c.drawLine(cut+4,h-7,w*.29f,h-7,paint);
        if(red){paint.setStyle(Paint.Style.FILL);for(int i=0;i<6;i++){float x=w-75+i*9;Path stripe=new Path();stripe.moveTo(x,h-12);stripe.lineTo(x+7,h-12);stripe.lineTo(x+1,h-5);stripe.lineTo(x-6,h-5);stripe.close();c.drawPath(stripe,paint);}}
        c.restore();
    }
    @Override public void setAlpha(int a){paint.setAlpha(a);}
    @Override public void setColorFilter(ColorFilter f){paint.setColorFilter(f);}
    @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
