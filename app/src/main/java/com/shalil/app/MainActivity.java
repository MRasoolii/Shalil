package com.shalil.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    private final String PREF="shalil_data";
    private JSONArray products=new JSONArray(), invoices=new JSONArray();
    private LinearLayout page;
    private boolean persian=true, toman=false, dark=false;
    private EditText nameInput, barcodeInput, priceInput, buyInput, qtyInput;
    private String currency(){ return toman ? "تومان" : "ریال"; }
    private String tr(String fa,String en){ return persian?fa:en; }

    public void onCreate(Bundle b){ super.onCreate(b); load(); home(); }
    private void load(){
        android.content.SharedPreferences p=getSharedPreferences(PREF,0);
        try { products=new JSONArray(p.getString("products","[]")); invoices=new JSONArray(p.getString("invoices","[]")); } catch(Exception e){}
        persian=p.getBoolean("fa",true); toman=p.getBoolean("toman",false); dark=p.getBoolean("dark",false);
    }
    private void save(){
        getSharedPreferences(PREF,0).edit().putString("products",products.toString()).putString("invoices",invoices.toString())
        .putBoolean("fa",persian).putBoolean("toman",toman).putBoolean("dark",dark).apply();
    }
    private void setup(String title){
        page=new LinearLayout(this); page.setOrientation(1); page.setPadding(18,18,18,18);
        page.setBackgroundColor(dark?Color.rgb(28,30,34):Color.rgb(250,248,244));
        setContentView(page);
        TextView h=new TextView(this); h.setText(title); h.setTextSize(25); h.setTextColor(dark?Color.WHITE:Color.rgb(60,55,45)); h.setPadding(4,8,4,18); page.addView(h);
    }
    private TextView text(String s,int size){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(dark?Color.WHITE:Color.DKGRAY); t.setPadding(6,10,6,10); page.addView(t); return t;
    }
    private Button btn(String s,Runnable r){
        Button b=new Button(this); b.setText(s); page.addView(b); b.setOnClickListener(v->r.run()); return b;
    }
    private EditText field(String hint){
        EditText e=new EditText(this); e.setSingleLine(true); e.setHint(hint); page.addView(e); return e;
    }
    private void home(){
        setup(tr("نرم افزار شلیل","Shalil"));
        text(tr("مدیریت فروشگاه آفلاین","Offline shop manager"),16);
        btn(tr("فاکتور جدید","New invoice"),()->invoice());
        btn(tr("مدیریت کالاها","Products"),()->productMenu());
        btn(tr("فاکتورهای قبلی","Invoice history"),()->history());
        btn(tr("آمار فروش","Sales statistics"),()->stats());
        btn(tr("تنظیمات","Settings"),()->settings());
        text("Created by Muhammad Rasooli\nPowered by ChatGPT",12);
    }
    private void productMenu(){
        setup(tr("مدیریت کالاها","Products"));
        btn(tr("افزودن کالا","Add product"),()->addProduct());
        btn(tr("فهرست / حذف کالا","Product list / delete"),()->productList());
        btn(tr("بازگشت","Back"),()->home());
    }
    private void addProduct(){
        setup(tr("افزودن کالا","Add product"));
        EditText n=field(tr("نام کالا","Product name"));
        EditText bc=field(tr("بارکد (اختیاری)","Barcode (optional)"));
        EditText buy=field(tr("قیمت خرید","Buying price"));
        EditText price=field(tr("قیمت فروش","Selling price"));
        EditText qty=field(tr("موجودی اولیه","Opening stock"));
        btn(tr("ذخیره کالا","Save product"),()->{
            if(n.getText().toString().trim().isEmpty()){ n.setError(tr("نام کالا الزامی است","Name required")); return; }
            try{
                JSONObject o=new JSONObject(); o.put("name",n.getText().toString()); o.put("barcode",bc.getText().toString());
                o.put("buy",number(buy)); o.put("price",number(price)); o.put("qty",number(qty));
                products.put(o); save(); productMenu();
            }catch(Exception e){ toast("Error"); }
        });
        btn(tr("بازگشت","Back"),()->productMenu());
    }
    private long number(EditText e){ try{return Long.parseLong(e.getText().toString().replace(",","").trim());}catch(Exception x){return 0;} }
    private void productList(){
        setup(tr("فهرست کالاها","Product list"));
        for(int i=0;i<products.length();i++){
            final int ix=i; JSONObject o=products.optJSONObject(i);
            btn(o.optString("name")+"  |  "+o.optLong("price")+" "+currency()+"  |  "+tr("حذف","Delete"),()->{
                products.remove(ix); save(); productList();
            });
        }
        btn(tr("بازگشت","Back"),()->productMenu());
    }
    private void invoice(){
        setup(tr("فاکتور فروش جدید","New sales invoice"));
        EditText search=field(tr("نام کالا یا بارکد را وارد کنید","Enter product name or barcode"));
        EditText count=field(tr("تعداد","Quantity"));
        LinearLayout lines=new LinearLayout(this); lines.setOrientation(1); page.addView(lines);
        ArrayList<JSONObject> chosen=new ArrayList<>(); ArrayList<Integer> quantities=new ArrayList<>();
        TextView totalView=text(tr("جمع کل: ۰","Total: 0")+" "+currency(),18);
        btn(tr("افزودن کالا به فاکتور","Add item"),()->{
            String q=search.getText().toString().trim(); int qty=1;
            try{qty=Integer.parseInt(count.getText().toString());}catch(Exception e){}
            if(qty<1)qty=1;
            JSONObject found=null;
            for(int i=0;i<products.length();i++){ JSONObject p=products.optJSONObject(i); if(p.optString("name").equalsIgnoreCase(q)||p.optString("barcode").equals(q)){found=p;break;} }
            if(found==null){toast(tr("کالا پیدا نشد؛ ابتدا آن را ثبت کنید","Product not found; add it first"));return;}
            chosen.add(found); quantities.add(qty);
            lines.addView(makeLine(found.optString("name")+" × "+qty+" = "+(found.optLong("price")*qty)));
            long sum=0; for(int i=0;i<chosen.size();i++)sum+=chosen.get(i).optLong("price")*quantities.get(i);
            totalView.setText(tr("جمع کل: ","Total: ")+sum+" "+currency());
            search.setText(""); count.setText("");
        });
        btn(tr("بستن و ذخیره فاکتور","Close and save invoice"),()->{
            if(chosen.size()==0){toast(tr("فاکتور خالی است","Invoice is empty"));return;}
            try{
                JSONObject inv=new JSONObject(); JSONArray items=new JSONArray(); long sum=0;
                for(int i=0;i<chosen.size();i++){
                    JSONObject p=chosen.get(i), it=new JSONObject(); int q=quantities.get(i);
                    it.put("name",p.optString("name")); it.put("qty",q); it.put("price",p.optLong("price")); items.put(it);
                    sum+=p.optLong("price")*q;
                }
                inv.put("items",items); inv.put("total",sum); inv.put("date",new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US).format(new Date()));
                invoices.put(inv); save(); toast(tr("فاکتور ذخیره شد","Invoice saved")); home();
            }catch(Exception e){toast("Error");}
        });
        btn(tr("انصراف","Cancel"),()->home());
    }
    private View makeLine(String s){ TextView t=new TextView(this);t.setText(s);t.setTextColor(dark?Color.WHITE:Color.BLACK);t.setPadding(4,8,4,8);return t; }
    private void history(){
        setup(tr("فاکتورهای قبلی","Invoice history"));
        for(int i=invoices.length()-1;i>=0;i--){ JSONObject o=invoices.optJSONObject(i); text(o.optString("date")+"  |  "+o.optLong("total")+" "+currency(),15); }
        btn(tr("بازگشت","Back"),()->home());
    }
    private void stats(){
        setup(tr("آمار فروش","Sales statistics"));
        long total=0; int count=0;
        for(int i=0;i<invoices.length();i++){total+=invoices.optJSONObject(i).optLong("total");count++;}
        text(tr("تعداد فاکتورها: ","Invoices: ")+count,18);
        text(tr("مجموع فروش ثبت‌شده: ","Recorded sales: ")+total+" "+currency(),18);
        text(tr("تعداد کالاها: ","Products: ")+products.length(),18);
        btn(tr("بازگشت","Back"),()->home());
    }
    private void settings(){
        setup(tr("تنظیمات","Settings"));
        btn(tr("زبان: فارسی / English","Language: Persian / English"),()->{persian=!persian;save();settings();});
        btn(tr("واحد پول: ریال / تومان","Currency: Rial / Toman"),()->{toman=!toman;save();settings();});
        btn(tr("حالت روشن / تاریک","Light / Dark mode"),()->{dark=!dark;save();settings();});
        text(tr("اطلاعات برنامه به‌صورت محلی روی گوشی ذخیره می‌شود.","App data is stored locally on this phone."),14);
        btn(tr("بازگشت","Back"),()->home());
    }
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
