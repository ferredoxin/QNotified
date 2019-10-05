package nil.nadph.qnotified;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import nil.nadph.qnotified.hook.FlashPicHook;
import nil.nadph.qnotified.hook.RepeaterHook;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.QThemeKit;
import nil.nadph.qnotified.util.Utils;

import java.util.ArrayList;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.iget_object_or_null;

public class InjectDelayableHooks {

    private static boolean inited = false;

    public static boolean step(Object director) {
        if (inited) return true;
        inited = true;
        final Activity ctx = (Activity) iget_object_or_null(director, "a", load("mqq/app/AppActivity"));

        FlashPicHook flash = FlashPicHook.get();
        RepeaterHook repeaterHook = RepeaterHook.get();

        boolean needDeobf = flash.isEnabled() && !flash.checkPreconditions();
        LinearLayout main[] = new LinearLayout[1];
        ProgressBar prog[] = new ProgressBar[1];
        TextView text[] = new TextView[1];
        if (needDeobf) {
            try {
                if (ctx != null) QThemeKit.initTheme(ctx);
            } catch (Throwable e) {
                Utils.log(e);
            }
            final ArrayList<Integer> todos = new ArrayList<>();
            for (int i : flash.getPreconditions()) {
                if (DexKit.tryLoadOrNull(i) == null) todos.add(i);
            }
            for (int idx = 0; idx < todos.size(); idx++) {
                final String name = DexKit.c(todos.get(idx)).replace("/", ".");
                final int j = idx;
                if (ctx != null)
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (main[0] == null) {
                                main[0] = new LinearLayout(ctx);
                                main[0].setOrientation(LinearLayout.VERTICAL);
                                main[0].setBackgroundColor(0xfff5f5f5);
                                main[0].setGravity(Gravity.CENTER);
                                main[0].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                prog[0] = new ProgressBar(ctx);
                                prog[0].setMax(100);
                                prog[0].setIndeterminate(false);
                                prog[0].setProgressDrawable(ctx.getResources().getDrawable(android.R.drawable.progress_horizontal));
                                prog[0].setIndeterminateDrawable(ctx.getResources().getDrawable(android.R.drawable.progress_indeterminate_horizontal));
                                LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(ctx, 4));
                                int __5_ = dip2px(ctx, 5);
                                plp.setMargins(__5_ * 2, 0, __5_ * 2, __5_ * 3);
                                main[0].addView(prog[0], plp);
                                text[0] = new TextView(ctx);
                                text[0].setTextSize(16);
                                text[0].setGravity(Gravity.CENTER_HORIZONTAL);
                                text[0].setTextColor(0xFF444444);
                                LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                main[0].addView(text[0], tlp);
                                ((ViewGroup) ctx.getWindow().getDecorView()).addView(main[0]);
                            }
                            text[0].setText("QNotified正在定位被混淆类:\n" + name + "\n每个类一般不会超过一分钟");
                            prog[0].setProgress((int) (100f * j / todos.size()));
                        }
                    });
                DexKit.doFindClass(todos.get(idx));
            }
        }
        if (flash.isEnabled()) flash.init();
        if (repeaterHook.isEnabled()) repeaterHook.init();
        if (ctx != null && main[0] != null) ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) ctx.getWindow().getDecorView()).removeView(main[0]);
            }
        });
        return true;
    }
}
