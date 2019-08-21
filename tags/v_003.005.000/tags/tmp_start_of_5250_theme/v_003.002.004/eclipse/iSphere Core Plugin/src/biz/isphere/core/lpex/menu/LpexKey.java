/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.lpex.menu;

public interface LpexKey {

    /* keyboard modifiers */
    public static final String CTRL = "c"; //$NON-NLS-1$
    public static final String SHIFT = "s"; //$NON-NLS-1$
    public static final String ALT = "a"; //$NON-NLS-1$

    /* command keys */
    public static final String F1 = "f1"; //$NON-NLS-1$
    public static final String F2 = "f2"; //$NON-NLS-1$
    public static final String F3 = "f3"; //$NON-NLS-1$
    public static final String F4 = "f4"; //$NON-NLS-1$
    public static final String F5 = "f5"; //$NON-NLS-1$
    public static final String F6 = "f6"; //$NON-NLS-1$
    public static final String F7 = "f7"; //$NON-NLS-1$
    public static final String F8 = "f8"; //$NON-NLS-1$
    public static final String F9 = "f9"; //$NON-NLS-1$
    public static final String F10 = "f10"; //$NON-NLS-1$
    public static final String F11 = "f11"; //$NON-NLS-1$
    public static final String F12 = "f12"; //$NON-NLS-1$
    public static final String F13 = "f13"; //$NON-NLS-1$
    public static final String F14 = "f14"; //$NON-NLS-1$
    public static final String F15 = "f15"; //$NON-NLS-1$

    /* numpad keys */
    public static final String NUMPAD_0 = "numpad0"; //$NON-NLS-1$
    public static final String NUMPAD_1 = "numpad1"; //$NON-NLS-1$
    public static final String NUMPAD_2 = "numpad2"; //$NON-NLS-1$
    public static final String NUMPAD_3 = "numpad3"; //$NON-NLS-1$
    public static final String NUMPAD_4 = "numpad4"; //$NON-NLS-1$
    public static final String NUMPAD_5 = "numpad5"; //$NON-NLS-1$
    public static final String NUMPAD_6 = "numpad6"; //$NON-NLS-1$
    public static final String NUMPAD_7 = "numpad7"; //$NON-NLS-1$
    public static final String NUMPAD_8 = "numpad8"; //$NON-NLS-1$
    public static final String NUMPAD_9 = "numpad9"; //$NON-NLS-1$
    public static final String ADD = "add"; //$NON-NLS-1$
    public static final String SUBSTRACT = "subtract"; //$NON-NLS-1$
    public static final String MULTIPLY = "multiply"; //$NON-NLS-1$
    public static final String DIVIDE = "divide"; //$NON-NLS-1$
    public static final String NUM_LOCK = "numLock"; //$NON-NLS-1$
    public static final String NUMPAD_ENTER = "numpadEnter"; //$NON-NLS-1$

    /* standard keys */
    public static final String AMPERSAND = "ampersand"; //$NON-NLS-1$
    public static final String ASTERISK = "asterisk"; //$NON-NLS-1$
    public static final String AT_SIGN = "atSign"; //$NON-NLS-1$
    public static final String BACK_QUOTE = "backQuote"; //$NON-NLS-1$
    public static final String BACK_SLASH = "backSlash"; //$NON-NLS-1$
    public static final String BACK_SPACE = "backSpace"; //$NON-NLS-1$
    public static final String CANCEL = "cancel"; //$NON-NLS-1$
    public static final String CAPS_LOCK = "capsLock"; //$NON-NLS-1$
    public static final String CLOSE_BRACE = "closeBrace"; //$NON-NLS-1$
    public static final String CLOSE_BRACKET = "closeBracket"; //$NON-NLS-1$
    public static final String CLOSE_PARANTHESIS = "closeParenthesis"; //$NON-NLS-1$
    public static final String COLON = "colon"; //$NON-NLS-1$
    public static final String COMMA = "comma"; //$NON-NLS-1$
    public static final String DECIMAL = "decimal"; //$NON-NLS-1$
    public static final String DELETE = "delete"; //$NON-NLS-1$
    public static final String DOLLAR_SIGN = "dollarSign"; //$NON-NLS-1$
    public static final String DOUBLE_QUOTE = "doubleQuote"; //$NON-NLS-1$
    public static final String DOWN = "down"; //$NON-NLS-1$
    public static final String END = "end"; //$NON-NLS-1$
    public static final String ENTER = "enter"; //$NON-NLS-1$
    public static final String EQUALS = "equals"; //$NON-NLS-1$
    public static final String ESCAPE = "escape"; //$NON-NLS-1$
    public static final String EXCLAMATION_MARK = "exclamationMark"; //$NON-NLS-1$
    public static final String GREATER_THAN_SIGN = "greaterThanSign"; //$NON-NLS-1$
    public static final String HELP = "help"; //$NON-NLS-1$
    public static final String HOME = "home"; //$NON-NLS-1$
    public static final String INSERT = "insert"; //$NON-NLS-1$
    public static final String KARAT = "karat"; //$NON-NLS-1$
    public static final String LEFT = "left"; //$NON-NLS-1$
    public static final String LESS_THAN_SIGN = "lessThanSign"; //$NON-NLS-1$
    public static final String MINUS = "minus"; //$NON-NLS-1$
    public static final String NUMBER_SIGN = "numberSign"; //$NON-NLS-1$
    public static final String OPEN_BRACE = "openBrace"; //$NON-NLS-1$
    public static final String OPEN_BRACKET = "openBracket"; //$NON-NLS-1$
    public static final String OPEN_PARANTHESIS = "openParenthesis"; //$NON-NLS-1$
    public static final String PAGE_DOWN = "pageDown"; //$NON-NLS-1$
    public static final String PAGE_UP = "pageUp"; //$NON-NLS-1$
    public static final String PAUSE = "pause"; //$NON-NLS-1$
    public static final String PERCENT = "percent"; //$NON-NLS-1$
    public static final String PERIOD = "period"; //$NON-NLS-1$
    public static final String PLUS = "plus"; //$NON-NLS-1$
    public static final String PRINT_SCREEN = "printScreen"; //$NON-NLS-1$
    public static final String QUESTION_MARK = "questionMark"; //$NON-NLS-1$
    public static final String QUOTE = "quote"; //$NON-NLS-1$
    public static final String RIGHT = "right"; //$NON-NLS-1$
    public static final String SCROLL_LOCK = "scrollLock"; //$NON-NLS-1$
    public static final String SEMICOLON = "semicolon"; //$NON-NLS-1$
    public static final String SPLASH = "slash"; //$NON-NLS-1$
    public static final String SPACE = "space"; //$NON-NLS-1$
    public static final String TAB = "tab"; //$NON-NLS-1$
    public static final String TILDE = "tilde"; //$NON-NLS-1$
    public static final String UNDERSCORE = "underscore"; //$NON-NLS-1$
    public static final String UP = "up"; //$NON-NLS-1$
    public static final String VERTICAL_BAR = "verticalBar"; //$NON-NLS-1$
}
