package com.pukekogames.airportdesigner.Objects.UIElements;

import android.view.MotionEvent;
import com.pukekogames.airportdesigner.Helper.Alignment;
import com.pukekogames.airportdesigner.Helper.Command;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 20.11.2016.
 */
public class ScrollList extends ClickableGameObject {
    private static final long serialVersionUID = -1501291182542044030L;


    private ArrayList<Object> objects;
    private int startIndex;
    private int showingCount;
    private int selectedIndex;
    private float fontHeight;
    private boolean selectable;
    private Command command;

    public ScrollList(Alignment alignment, float x, float y, float width, float fontHeight, int showingCount, boolean selectable) {
        super(alignment, x, y, width, fontHeight);
        this.selectable = selectable;
        this.fontHeight = fontHeight;
        this.showingCount = showingCount;
        objects = new ArrayList<>();
    }

    @Override
    public void clicked(int mx, int my) {
        if (selectable) {
            int diffY = my - (int) y;
            selectedIndex = Math.round(diffY / fontHeight) + startIndex;
            if (selectedIndex < 0){
                selectedIndex = 0;
            }

            if (selectedIndex >= objects.size()){
                selectedIndex = objects.size() -1;
            }
            if (command != null) command.execute(this);
        }
    }

    public boolean OnScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        boolean scrolled = false;
        if (CommonMethods.isTouchInside((int) e1.getX(),(int) e1.getY(), this)){
            scrolled = true;
            if (distanceY > 10){
                increaseStartIndex();
            }else if (distanceY < -10){
                decreaseStartIndex();
            }
        }
        return scrolled;
    }

    @Override
    public void tick() {

    }

    private void UpdateHeight(){
        if (objects.size() == 0){
            height = fontHeight;
            return;
        }
        height  = Math.min(fontHeight * showingCount, fontHeight * objects.size());
    }

    public void addItem(Object object){
        objects.add(object);
        UpdateHeight();
    }

    public void insert(int index, Object object){
        if (index >= 0 && index < objects.size()){
            objects.add(index, object);
        }
    }

    public void setItem(int index, Object object){
        if (index >= 0 && index < objects.size()){
            objects.set(index, object);
        }
    }

    public Object getItem(int index){
        if (index >= 0 && index < objects.size()){
            return objects.get(index);
        }else{
            return null;
        }
    }

    public int size(){
        return objects.size();
    }

    public void clear(){
        objects.clear();
        UpdateHeight();
    }

    public boolean isSelectable() {
        return selectable;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Object getItemAtSelectedIndex(){
        if (selectedIndex >= objects.size()) return null;
        return objects.get(selectedIndex);
    }

    public int getShowingCount() {
        return showingCount;
    }

    public void setShowingCount(int showingCount) {
        this.showingCount = showingCount;
        height = fontHeight * showingCount;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void increaseStartIndex(){
        if (startIndex + showingCount < objects.size()){
            startIndex += 1;
        }
    }

    public void decreaseStartIndex(){
        if (startIndex > 0){
            startIndex -= 1;
        }
    }

    public void setStartIndex(int startIndex) {
        if (startIndex < 0){
            this.startIndex = 0;
            return;
        }
        this.startIndex = startIndex;
    }

    public float getFontHeight() {
        return fontHeight;
    }

    public void setCommand(Command command) {
        this.command = command;
    }


}
