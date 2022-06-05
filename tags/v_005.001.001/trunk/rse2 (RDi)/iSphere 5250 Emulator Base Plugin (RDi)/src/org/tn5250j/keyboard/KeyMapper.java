/**
 * Title: KeyMapper
 * Copyright:   Copyright (c) 2001
 * Company:
 * @author  Kenneth J. Pouncey
 * @version 0.1
 *
 * Description:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 */
package org.tn5250j.keyboard;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.KeyStroke;

import org.tn5250j.TN5250jConstants;
import org.tn5250j.event.KeyChangeListener;
import org.tn5250j.interfaces.ConfigureFactory;
import org.tn5250j.interfaces.OptionAccessFactory;
import org.tn5250j.tools.LangTool;

public class KeyMapper {

    private static HashMap<KeyStroker, String> mappedKeys;
    private static KeyStroker workStroke;
    private static String lastKeyMnemonic;
    private static Vector<KeyChangeListener> listeners;

    public static void init() {

        if (mappedKeys != null) return;

        mappedKeys = new HashMap<KeyStroker, String>(60);
        workStroke = new KeyStroker(0, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD);

        Properties keys = ConfigureFactory.getInstance().getProperties(ConfigureFactory.KEYMAP);

        if (!loadKeyStrokes(keys)) {
            // Key <-> Keycode , isShiftDown , isControlDown , isAlternateDown,
            // location

            mappedKeys.put(new KeyStroker(10, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[enter]");
            mappedKeys.put(new KeyStroker(10, false, false, false, false, KeyStroker.KEY_LOCATION_NUMPAD), "[enter].alt2");

            mappedKeys.put(new KeyStroker(8, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[backspace]");
            mappedKeys.put(new KeyStroker(9, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[tab]");
            mappedKeys.put(new KeyStroker(9, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[backtab]");
            mappedKeys.put(new KeyStroker(127, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[delete]");
            mappedKeys.put(new KeyStroker(155, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[insert]");
            mappedKeys.put(new KeyStroker(19, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[clear]");

            mappedKeys.put(new KeyStroker(17, false, true, false, false, KeyStroker.KEY_LOCATION_LEFT), "[reset]");

            mappedKeys.put(new KeyStroker(27, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[sysreq]");

            mappedKeys.put(new KeyStroker(35, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[eof]");
            mappedKeys.put(new KeyStroker(36, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[home]");
            mappedKeys.put(new KeyStroker(39, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[right]");
            mappedKeys.put(new KeyStroker(39, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[nextword]");
            mappedKeys.put(new KeyStroker(37, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[left]");
            mappedKeys.put(new KeyStroker(37, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[prevword]");
            mappedKeys.put(new KeyStroker(38, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[up]");
            mappedKeys.put(new KeyStroker(40, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[down]");
            mappedKeys.put(new KeyStroker(34, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[pgdown]");
            mappedKeys.put(new KeyStroker(33, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[pgup]");

            mappedKeys.put(new KeyStroker(96, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad0]");
            mappedKeys.put(new KeyStroker(97, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad1]");
            mappedKeys.put(new KeyStroker(98, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad2]");
            mappedKeys.put(new KeyStroker(99, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad3]");
            mappedKeys.put(new KeyStroker(100, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad4]");
            mappedKeys.put(new KeyStroker(101, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad5]");
            mappedKeys.put(new KeyStroker(102, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad6]");
            mappedKeys.put(new KeyStroker(103, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad7]");
            mappedKeys.put(new KeyStroker(104, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad8]");
            mappedKeys.put(new KeyStroker(105, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[keypad9]");

            mappedKeys.put(new KeyStroker(109, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[field-]");
            mappedKeys.put(new KeyStroker(107, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[field+]");
            mappedKeys.put(new KeyStroker(112, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF1.label());
            mappedKeys.put(new KeyStroker(113, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF2.label());
            mappedKeys.put(new KeyStroker(114, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF3.label());
            mappedKeys.put(new KeyStroker(115, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF4.label());
            mappedKeys.put(new KeyStroker(116, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF5.label());
            mappedKeys.put(new KeyStroker(117, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF6.label());
            mappedKeys.put(new KeyStroker(118, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF7.label());
            mappedKeys.put(new KeyStroker(119, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF8.label());
            mappedKeys.put(new KeyStroker(120, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF9.label());
            mappedKeys.put(new KeyStroker(121, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF10.label());
            mappedKeys.put(new KeyStroker(122, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF11.label());
            mappedKeys.put(new KeyStroker(123, false, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF12.label());
            mappedKeys.put(new KeyStroker(112, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF13.label());
            mappedKeys.put(new KeyStroker(113, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF14.label());
            mappedKeys.put(new KeyStroker(114, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF15.label());
            mappedKeys.put(new KeyStroker(115, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF16.label());
            mappedKeys.put(new KeyStroker(116, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF17.label());
            mappedKeys.put(new KeyStroker(117, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF18.label());
            mappedKeys.put(new KeyStroker(118, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF19.label());
            mappedKeys.put(new KeyStroker(119, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF20.label());
            mappedKeys.put(new KeyStroker(120, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF21.label());
            mappedKeys.put(new KeyStroker(121, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF22.label());
            mappedKeys.put(new KeyStroker(122, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF23.label());
            mappedKeys.put(new KeyStroker(123, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), HostKey.PF24.label());
            mappedKeys.put(new KeyStroker(112, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[help]");

            mappedKeys.put(new KeyStroker(72, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[hostprint]");

            mappedKeys.put(new KeyStroker(67, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[copy]");

            mappedKeys.put(new KeyStroker(86, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[paste]");

            mappedKeys.put(new KeyStroker(39, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[markright]");
            mappedKeys.put(new KeyStroker(37, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[markleft]");
            mappedKeys.put(new KeyStroker(38, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[markup]");
            mappedKeys.put(new KeyStroker(40, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[markdown]");

            mappedKeys.put(new KeyStroker(155, true, false, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[dupfield]");
            mappedKeys.put(new KeyStroker(17, true, true, false, false, KeyStroker.KEY_LOCATION_STANDARD), "[newline]");
            mappedKeys.put(new KeyStroker(34, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[jumpnext]");
            mappedKeys.put(new KeyStroker(33, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD), "[jumpprev]");

            initISphereSpecialKeys(true);

            saveKeyMap();
        } else {

            setKeyMap(keys);
            boolean isDirty = initISphereSpecialKeys(false);
            if (isDirty) {
                saveKeyMap();
            }

        }

    }

    private static boolean initISphereSpecialKeys(boolean createNewMapping) {

        boolean isDirty = false;

        // Add iSphere special key strokes for:
        // a) Moving between main sessions: Ctrl+Up and Ctrl+Down
        if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_NEXT_SESSION)) {
            mappedKeys.put(new KeyStroker(38, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD), TN5250jConstants.MNEMONIC_NEXT_SESSION);
            isDirty = true;
        }

        if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_PREVIOUS_SESSION)) {
            mappedKeys.put(new KeyStroker(40, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD),
                TN5250jConstants.MNEMONIC_PREVIOUS_SESSION);
            isDirty = true;
        }

        // b) Moving between minor (multiple) sessions ...
        if (createNewMapping) {
            // ... using the new mapping: Ctrl+Right and Ctrl+Left
            if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION)) {
                setKeyStroker(TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION,
                    new KeyStroker(39, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD));
                isDirty = true;
            }
            if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION)) {
                setKeyStroker(TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION,
                    new KeyStroker(37, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD));
                isDirty = true;
            }
        } else {
            // ... using the old mapping: Alt+Up and Alt+Down
            if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION)) {
                mappedKeys.put(new KeyStroker(38, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD),
                    TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION);
                isDirty = true;
            }
            if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION)) {
                mappedKeys.put(new KeyStroker(40, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD),
                    TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION);
                isDirty = true;
            }
        }

        // c) Scrolling sessions: Ctrl+Alt+Up, Ctrl+Alt+Down, Ctrl+Alt+Left and
        // Ctrl+Alt+Right
        if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_SCROLL_SESSION_UP)) {
            mappedKeys.put(new KeyStroker(38, false, true, true, false, KeyStroker.KEY_LOCATION_STANDARD),
                TN5250jConstants.MNEMONIC_SCROLL_SESSION_UP);
            isDirty = true;
        }

        if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_SCROLL_SESSION_DOWN)) {
            mappedKeys.put(new KeyStroker(40, false, true, true, false, KeyStroker.KEY_LOCATION_STANDARD),
                TN5250jConstants.MNEMONIC_SCROLL_SESSION_DOWN);
            isDirty = true;
        }

        if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_SCROLL_SESSION_LEFT)) {
            mappedKeys.put(new KeyStroker(37, false, true, true, false, KeyStroker.KEY_LOCATION_STANDARD),
                TN5250jConstants.MNEMONIC_SCROLL_SESSION_LEFT);
            isDirty = true;
        }

        if (!isKeyStrokeDefined(TN5250jConstants.MNEMONIC_SCROLL_SESSION_RIGHT)) {
            mappedKeys.put(new KeyStroker(39, false, true, true, false, KeyStroker.KEY_LOCATION_STANDARD),
                TN5250jConstants.MNEMONIC_SCROLL_SESSION_RIGHT);
            isDirty = true;
        }

        return isDirty;
    }

    public static boolean isNextMajorSessionKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_NEXT_SESSION.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isPreviousMajorSessionKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_PREVIOUS_SESSION.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isNextMinorSessionKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isPreviousMinorSessionKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isScrollSessionUpKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_SCROLL_SESSION_UP.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isScrollSessionDownKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_SCROLL_SESSION_DOWN.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isScrollSessionLeftKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_SCROLL_SESSION_LEFT.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    public static boolean isScrollSessionRightKeyStroke(KeyEvent ke) {
        if (TN5250jConstants.MNEMONIC_SCROLL_SESSION_RIGHT.equals(getKeyStrokeText(ke))) {
            return true;
        }
        return false;
    }

    private static boolean loadKeyStrokes(Properties keystrokes) {

        keystrokes = ConfigureFactory.getInstance().getProperties(ConfigureFactory.KEYMAP);
        if (keystrokes != null && keystrokes.size() > 0)
            return true;
        else
            return false;
    }

    private static void parseKeyStrokes(Properties keystrokes) {

        String theStringList = "";
        String theKey = "";
        Enumeration<?> ke = keystrokes.propertyNames();
        while (ke.hasMoreElements()) {
            theKey = (String)ke.nextElement();

            if (OptionAccessFactory.getInstance().isRestrictedOption(theKey)) {
                continue;
            }

            theStringList = keystrokes.getProperty(theKey);
            int kc = 0;
            boolean is = false;
            boolean ic = false;
            boolean ia = false;
            boolean iag = false;
            int location = KeyStroker.KEY_LOCATION_STANDARD;

            StringTokenizer tokenizer = new StringTokenizer(theStringList, ",");

            // first is the keycode
            kc = Integer.parseInt(tokenizer.nextToken());
            // isShiftDown
            if (tokenizer.nextToken().equals("true"))
                is = true;
            else
                is = false;
            // isControlDown
            if (tokenizer.nextToken().equals("true"))
                ic = true;
            else
                ic = false;
            // isAltDown
            if (tokenizer.nextToken().equals("true"))
                ia = true;
            else
                ia = false;

            // isAltDown Gr
            if (tokenizer.hasMoreTokens()) {
                if (tokenizer.nextToken().equals("true"))
                    iag = true;
                else
                    iag = false;

                if (tokenizer.hasMoreTokens()) {
                    location = Integer.parseInt(tokenizer.nextToken());
                }
            }

            mappedKeys.put(new KeyStroker(kc, is, ic, ia, iag, location), theKey);

        }

    }

    protected static void setKeyMap(Properties keystrokes) {

        parseKeyStrokes(keystrokes);

    }

    public final static boolean isEqualLast(KeyEvent ke) {
        return workStroke.equals(ke);
    }

    public final static void saveKeyMap() {

        Properties map = ConfigureFactory.getInstance().getProperties(ConfigureFactory.KEYMAP);

        map.clear();

        // save off the keystrokes in the keymap
        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            KeyStroker ks = k.next();
            map.put(i.next(), ks.toString());
        }

        ConfigureFactory.getInstance().saveSettings(ConfigureFactory.KEYMAP,
            "------ Key Map key=keycode,isShiftDown,isControlDown,isAltDown,isAltGrDown,location --------");
    }

    public final static String getKeyStrokeText(KeyEvent ke) {
        return getKeyStrokeText(ke, false);
    }

    public final static String getKeyStrokeText(KeyEvent ke, boolean isAltGr) {
        if (!workStroke.equals(ke, isAltGr)) {
            workStroke.setAttributes(ke, isAltGr);
            lastKeyMnemonic = mappedKeys.get(workStroke);
        }

        if (lastKeyMnemonic != null && lastKeyMnemonic.endsWith(KeyStroker.altSuffix)) {

            lastKeyMnemonic = lastKeyMnemonic.substring(0, lastKeyMnemonic.indexOf(KeyStroker.altSuffix));
        }

        return lastKeyMnemonic;

    }

    public final static String getKeyStrokeMnemonic(KeyEvent ke) {
        return getKeyStrokeMnemonic(ke, false);
    }

    public final static String getKeyStrokeMnemonic(KeyEvent ke, boolean isAltGr) {

        workStroke.setAttributes(ke, isAltGr);
        String keyMnemonic = mappedKeys.get(workStroke);

        if (keyMnemonic != null && keyMnemonic.endsWith(KeyStroker.altSuffix)) {

            keyMnemonic = keyMnemonic.substring(0, keyMnemonic.indexOf(KeyStroker.altSuffix));
        }

        return keyMnemonic;

    }

    public final static int getKeyStrokeCode() {
        return workStroke.hashCode();
    }

    public final static String getKeyStrokeDesc(String which) {

        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            KeyStroker ks = k.next();
            String keyVal = i.next();
            if (keyVal.equals(which)) return ks.getKeyStrokeDesc();
        }

        return LangTool.getString("key.dead");
    }

    public final static KeyStroker getKeyStroker(String which) {

        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            KeyStroker ks = k.next();
            String keyVal = i.next();
            if (keyVal.equals(which)) return ks;
        }

        return null;
    }

    public final static boolean isKeyStrokeDefined(String which) {

        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            k.next();
            String keyVal = i.next();
            if (keyVal.equals(which)) return true;
        }

        return false;
    }

    public final static boolean isKeyStrokeDefined(KeyEvent ke) {
        return isKeyStrokeDefined(ke, false);
    }

    public final static boolean isKeyStrokeDefined(KeyEvent ke, boolean isAltGr) {

        workStroke.setAttributes(ke, isAltGr);
        return (null != mappedKeys.get(workStroke));

    }

    public final static KeyStroke getKeyStroke(String which) {

        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            KeyStroker ks = k.next();
            String keyVal = i.next();
            if (keyVal.equals(which)) {
                int mask = 0;

                if (ks.isShiftDown()) mask |= InputEvent.SHIFT_MASK;
                if (ks.isControlDown()) mask |= InputEvent.CTRL_MASK;
                if (ks.isAltDown()) mask |= InputEvent.ALT_MASK;
                if (ks.isAltGrDown()) mask |= InputEvent.ALT_GRAPH_MASK;

                return KeyStroke.getKeyStroke(ks.getKeyCode(), mask);
            }
        }

        return KeyStroke.getKeyStroke(0, 0);
    }

    public final static void removeKeyStroke(String which) {

        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            KeyStroker ks = k.next();
            String keyVal = i.next();
            if (keyVal.equals(which)) {
                mappedKeys.remove(ks);
                return;
            }
        }

    }

    public final static void setKeyStroke(String which, KeyEvent ke) {

        if (ke == null) return;
        setKeyStroker(which, new KeyStroker(ke));

    }

    public final static void setKeyStroke(String which, KeyEvent ke, boolean isAltGr) {

        if (ke == null) return;
        setKeyStroker(which, new KeyStroker(ke, isAltGr));

    }

    public final static void setKeyStroker(String which, KeyStroker keyStroker) {

        if (keyStroker == null) return;
        Collection<String> v = mappedKeys.values();
        Set<KeyStroker> o = mappedKeys.keySet();
        Iterator<KeyStroker> k = o.iterator();
        Iterator<String> i = v.iterator();
        while (k.hasNext()) {
            KeyStroker ks = k.next();
            String keyVal = i.next();
            if (keyVal.equals(which)) {
                mappedKeys.remove(ks);
                mappedKeys.put(keyStroker, keyVal);
                return;
            }
        }

        // if we got here it was a dead key and we need to add it.
        mappedKeys.put(keyStroker, which);

    }

    public final static HashMap<KeyStroker, String> getKeyMap() {
        return mappedKeys;
    }

    /**
     * Add a KeyChangeListener to the listener list.
     * 
     * @param listener The KeyChangedListener to be added
     */
    public static synchronized void addKeyChangeListener(KeyChangeListener listener) {

        if (listeners == null) {
            listeners = new java.util.Vector<KeyChangeListener>(3);
        }
        listeners.addElement(listener);

    }

    /**
     * Remove a Key Change Listener from the listener list.
     * 
     * @param listener The KeyChangeListener to be removed
     */
    public synchronized void removeKeyChangeListener(KeyChangeListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.removeElement(listener);

    }

    /**
     * Notify all registered listeners of the Key Change Event.
     */
    public static void fireKeyChangeEvent() {

        if (listeners != null) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                KeyChangeListener target = listeners.elementAt(i);
                target.onKeyChanged();
            }
        }
    }

    public static boolean hasFastCursorMappingConflicts() {

        init();

        KeyStroker fastCursorUpKeyStroker = getKeyStroker(TN5250jConstants.MNEMONIC_FAST_CURSOR_UP);
        KeyStroker nextMultipleSessionKeyStroker = getKeyStroker(TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION);

        if (fastCursorUpKeyStroker == null && "Alt + Up".equals(nextMultipleSessionKeyStroker.getKeyStrokeDesc())) { //$NON-NLS-1$
            return true;
        }

        KeyStroker fastCursorDownKeyStroker = getKeyStroker(TN5250jConstants.MNEMONIC_FAST_CURSOR_DOWN);
        KeyStroker previousMultipleSessionKeyStroker = getKeyStroker(TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION);

        if (fastCursorDownKeyStroker == null && "Alt + Down".equals(previousMultipleSessionKeyStroker.getKeyStrokeDesc())) { //$NON-NLS-1$
            return true;
        }

        return false;
    }

    public static void resolveFastCursorMappingConflicts() {

        // Remove mappings for: next/previous multiple session
        removeKeyStroke(TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION);
        removeKeyStroke(TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION);

        // Add mappings for: next/previous multiple session
        setKeyStroker(TN5250jConstants.MNEMONIC_NEXT_MULTIPLE_SESSION,
            new KeyStroker(39, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD));
        setKeyStroker(TN5250jConstants.MNEMONIC_PREVIOUS_MULTIPLE_SESSION,
            new KeyStroker(37, false, true, false, false, KeyStroker.KEY_LOCATION_STANDARD));

        // Add mappings for: fast cursor up/down
        setKeyStroker(TN5250jConstants.MNEMONIC_FAST_CURSOR_UP, new KeyStroker(38, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD));
        setKeyStroker(TN5250jConstants.MNEMONIC_FAST_CURSOR_DOWN, new KeyStroker(40, false, false, true, false, KeyStroker.KEY_LOCATION_STANDARD));

        saveKeyMap();

        fireKeyChangeEvent();
    }
}
