package uci.shield.apps;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by akiel on 12/27/15.
 */
public class Aplicacion implements Comparable{
    private int uid;
    private String label;
    private Drawable icon;
    private boolean checked = false;
    private boolean visible = false;

    public Aplicacion(int uid, String label, Drawable icon, boolean checked,boolean visible ) {
        this.uid = uid;
        this.label = label;
        this.icon = icon;
        this.checked = checked;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }



    public int compareTo(Aplicacion another) {
        return this.getLabel().compareTo(another.getLabel());
    }

    @Override
    public int compareTo(Object another) {
        return compareTo((Aplicacion)another);
    }

    @Override
    public String toString() {
//        Log.w("Aplicacion -- toString",label);
        return label;
    }
}
