package com.example.yomostylelauncher;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int PICK_IMAGE = 42;
    private LinearLayout root;
    private int page = 0;
    private final int[][] palettes = {
        {0xFF302B63, 0xFF24243E}, {0xFFFF9A9E, 0xFFFAD0C4},
        {0xFF2193B0, 0xFF6DD5ED}, {0xFF42275A, 0xFF734B6D},
        {0xFF134E5E, 0xFF71B280}, {0xFFFF512F, 0xFFDD2476}
    };
    private final String[] wallpaperNames = {"Midnight", "Peach", "Ocean", "Lavender", "Forest", "Sunset"};

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(30, 27, 55));
        getWindow().setNavigationBarColor(Color.rgb(30, 27, 55));
        showHome();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private void showHome() {
        root = column(); root.setPadding(dp(22), dp(28), dp(22), dp(18));
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{0xFF302B63,0xFF24243E}));
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.addView(root);
        setContentView(scroll);

        TextView brand = text("yomo style", 17, 0xFFE9E3FF, true); brand.setLetterSpacing(0.08f);
        root.addView(brand);
        TextView date = text(new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(new Date()), 14, 0xFFD5D0E8, false);
        LinearLayout.LayoutParams dateLp = new LinearLayout.LayoutParams(-2,-2); dateLp.topMargin=dp(38); root.addView(date,dateLp);
        TextView clock = text(new SimpleDateFormat("hh:mm", Locale.getDefault()).format(new Date()), 64, Color.WHITE, false);
        clock.setLetterSpacing(-0.04f); root.addView(clock);
        TextView subtitle = text("Make your screen feel like you.", 14, 0xFFDCD7F0, false);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(-2,-2); subLp.topMargin=dp(4); root.addView(subtitle,subLp);

        LinearLayout spacer = new LinearLayout(this); root.addView(spacer,new LinearLayout.LayoutParams(1,dp(42)));
        TextView section = text("Choose your vibe", 20, Color.WHITE, true); root.addView(section);
        TextView hint = text("Tap a wallpaper to preview and apply it", 13, 0xFFD5D0E8, false);
        LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(-2,-2); hintLp.topMargin=dp(5); root.addView(hint,hintLp);

        GridLayout grid = new GridLayout(this); grid.setColumnCount(2); grid.setUseDefaultMargins(false);
        LinearLayout.LayoutParams gridLp = new LinearLayout.LayoutParams(-1,-2); gridLp.topMargin=dp(16); root.addView(grid,gridLp);
        for (int i=0;i<palettes.length;i++) {
            final int index=i; LinearLayout tile=column(); tile.setGravity(Gravity.BOTTOM);
            GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,palettes[i]); bg.setCornerRadius(dp(20)); tile.setBackground(bg);
            tile.setPadding(dp(13),dp(12),dp(13),dp(12));
            TextView name=text(wallpaperNames[i],15,Color.WHITE,true); tile.addView(name);
            TextView action=text("Preview & apply  →",11,0xFFF5F1FF,false); LinearLayout.LayoutParams a=new LinearLayout.LayoutParams(-2,-2);a.topMargin=dp(4);tile.addView(action,a);
            GridLayout.LayoutParams p=new GridLayout.LayoutParams(); p.width=0; p.height=dp(112); p.columnSpec=GridLayout.spec(i%2,1f); p.setMargins(dp(4),dp(4),dp(4),dp(8)); tile.setLayoutParams(p);
            tile.setOnClickListener(v -> applyBuiltIn(index)); grid.addView(tile);
        }

        Button custom = button("＋  Use my own wallpaper");
        LinearLayout.LayoutParams cLp=new LinearLayout.LayoutParams(-1,dp(54)); cLp.topMargin=dp(8); root.addView(custom,cLp);
        custom.setOnClickListener(v -> pickImage());
        Button apps = button("▦  All apps"); LinearLayout.LayoutParams appLp=new LinearLayout.LayoutParams(-1,dp(50));appLp.topMargin=dp(10);root.addView(apps,appLp);
        apps.setOnClickListener(v -> showApps());
        TextView footer=text("Long-press your phone's home screen settings to choose this launcher as default.",11,0xFFC8C2DE,false);
        footer.setGravity(Gravity.CENTER); LinearLayout.LayoutParams f=new LinearLayout.LayoutParams(-1,-2);f.topMargin=dp(18);root.addView(footer,f);
    }

    private Button button(String label) {
        Button b=new Button(this); b.setText(label); b.setTextSize(14); b.setTextColor(0xFF28213F); b.setAllCaps(false);
        b.setBackground(rounded(0xFFE9DFFF,18)); return b;
    }
    private Bitmap makeWallpaper(int index) {
        int w=1080,h=2400; Bitmap bmp=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888); Canvas c=new Canvas(bmp);
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); p.setShader(new LinearGradient(0,0,w,h,palettes[index][0],palettes[index][1],Shader.TileMode.CLAMP)); c.drawRect(0,0,w,h,p);
        p.setShader(null); p.setColor(0x22FFFFFF); c.drawCircle(w*0.78f,h*0.28f,w*0.25f,p); c.drawCircle(w*0.15f,h*0.78f,w*0.32f,p);
        return bmp;
    }
    private void applyBuiltIn(int index) {
        try { WallpaperManager.getInstance(this).setBitmap(makeWallpaper(index)); Toast.makeText(this, wallpaperNames[index]+" wallpaper applied",Toast.LENGTH_SHORT).show(); }
        catch(Exception e){ Toast.makeText(this,"Could not apply wallpaper: "+e.getMessage(),Toast.LENGTH_LONG).show(); }
    }
    private void pickImage() {
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("image/*");
        try { startActivityForResult(i,PICK_IMAGE); } catch(ActivityNotFoundException e) { Toast.makeText(this,"No gallery app found",Toast.LENGTH_SHORT).show(); }
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            Uri uri=data.getData();
            try { getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored) {}
            try(InputStream in=getContentResolver().openInputStream(uri)) {
                Bitmap bitmap=android.graphics.BitmapFactory.decodeStream(in);
                if(bitmap==null) throw new Exception("Unsupported image");
                WallpaperManager.getInstance(this).setBitmap(bitmap);
                Toast.makeText(this,"Your wallpaper has been applied",Toast.LENGTH_SHORT).show();
            } catch(Exception e) { Toast.makeText(this,"Unable to set wallpaper: "+e.getMessage(),Toast.LENGTH_LONG).show(); }
        }
    }
    private void showApps() {
        LinearLayout pageRoot=column(); pageRoot.setPadding(dp(20),dp(26),dp(20),dp(20));
        pageRoot.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xFF302B63,0xFF24243E}));
        ScrollView sc=new ScrollView(this); sc.addView(pageRoot); setContentView(sc);
        TextView title=text("Your apps",26,Color.WHITE,true); pageRoot.addView(title);
        TextView back=text("←  Back to home",14,0xFFE4DDF8,false); LinearLayout.LayoutParams bl=new LinearLayout.LayoutParams(-2,-2);bl.topMargin=dp(10);pageRoot.addView(back,bl);back.setOnClickListener(v->showHome());
        GridLayout grid=new GridLayout(this);grid.setColumnCount(3);LinearLayout.LayoutParams gl=new LinearLayout.LayoutParams(-1,-2);gl.topMargin=dp(18);pageRoot.addView(grid,gl);
        Intent query=new Intent(Intent.ACTION_MAIN,null);query.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm=getPackageManager(); List<ResolveInfo> apps=pm.queryIntentActivities(query,0);
        for(ResolveInfo app:apps) {
            String label=app.loadLabel(pm).toString(); if(label.equals(getString(getApplicationInfo().labelRes))) continue;
            LinearLayout item=column();item.setGravity(Gravity.CENTER);item.setPadding(dp(4),dp(10),dp(4),dp(10));
            ImageView icon=new ImageView(this);icon.setImageDrawable(app.loadIcon(pm));item.addView(icon,new LinearLayout.LayoutParams(dp(44),dp(44)));
            TextView name=text(label,11,Color.WHITE,false);name.setGravity(Gravity.CENTER);name.setMaxLines(2);LinearLayout.LayoutParams nl=new LinearLayout.LayoutParams(-1,-2);nl.topMargin=dp(6);item.addView(name,nl);
            item.setOnClickListener(v->{try{startActivity(pm.getLaunchIntentForPackage(app.activityInfo.packageName));}catch(Exception ignored){}});
            GridLayout.LayoutParams p=new GridLayout.LayoutParams();p.width=0;p.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);grid.addView(item,p);
        }
        if(apps.isEmpty()) pageRoot.addView(text("No launchable apps found.",14,Color.WHITE,false));
    }
}
