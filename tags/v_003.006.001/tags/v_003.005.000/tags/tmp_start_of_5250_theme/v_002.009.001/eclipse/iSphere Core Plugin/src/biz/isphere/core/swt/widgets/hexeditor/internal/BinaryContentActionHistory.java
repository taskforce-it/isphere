/*******************************************************************************
 * javahexeditor, a java hex editor
 * Copyright (C) 2006-2015 Jordi Bergenthal, pestatije(-at_)users.sourceforge.net
 * The official javahexeditor site is sourceforge.net/projects/javahexeditor
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.core.swt.widgets.hexeditor.internal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent.Range;

/**
 * Keeps track of actions performed on a BinaryContent so they can be undone and
 * redone. Actions can be single or block deletes, inserts or overwrites.
 * Consecutive single actions are merged into a block action if they are of the
 * same type, their data is contiguous, and are performed with a time difference
 * lower than {@link #MERGE_TIME}. Block actions are sequences of Range. Single
 * actions are one range of size 1.
 * 
 * @author Jordi
 */
final class BinaryContentActionHistory {

    public final static class Entry {
        private Integer actionType;
        private List<Range> ranges;

        public Entry(Integer actionType, List<Range> ranges) {
            if (actionType == null) {
                throw new IllegalArgumentException("Parameter 'actionType' must not be null."); //$NON-NLS-1$
            }
            if (ranges == null) {
                throw new IllegalArgumentException("Parameter 'ranges' must not be null."); //$NON-NLS-1$
            }
            this.actionType = actionType;
            this.ranges = ranges;
        }

        public Integer getActionType() {
            return actionType;
        }

        public List<Range> getRanges() {
            return ranges;
        }
    }

    /**
     * Waiting time before a single action is considered separate from the
     * previous one. Current value is 1500 milliseconds.
     */
    static final int MERGE_TIME = 1500; // milliseconds

    /**
     * Action types
     */
    static final Integer TYPE_DELETE = new Integer(0);
    static final Integer TYPE_INSERT = new Integer(1);
    static final Integer TYPE_OVERWRITE = new Integer(2);

    private BinaryContent content;
    private Range myLastActionRange;
    private List<Integer> deletedList;
    private boolean isBackspace;
    private List<Entry> myActions;
    private int myActionsIndex;
    private List<Range> myCurrentActionRanges;
    private Integer myCurrentActionType;
    private long myMergedSinglesTop = -1L;
    private boolean myMergingSingles;
    private long myPreviousTime = 0L;

    private long newRangeLength = -1L;
    private long newRangePosition = -1L;

    /**
     * Create new action history storage object
     * 
     * @param content The new content, not <code>null</code>.
     */
    public BinaryContentActionHistory(BinaryContent content) {
        if (content == null) {
            throw new IllegalArgumentException("Parameter 'content' must not be null."); //$NON-NLS-1$
        }
        this.content = content;
        myActions = new ArrayList<Entry>();
    }

    private long actionExclusiveEnd() {
        long result = 0L;
        if (haveCurrentActionRanges()) {
            Range highest = getCurrentActionRange(myCurrentActionRanges.size() - 1);
            result = highest.exclusiveEnd();
        }
        long newRangeExclusiveEnd = newRangePosition + newRangeLength;
        if (newRangeExclusiveEnd > result) {
            result = newRangeExclusiveEnd;
        }

        return result;
    }

    private long actionPosition() {
        long result = -1L;
        if (haveCurrentActionRanges()) {
            Range lowest = getCurrentActionRange(0);
            result = lowest.position;
        }
        if (result < 0 || newRangePosition >= 0 && newRangePosition < result) result = newRangePosition;

        return result;
    }

    /**
     * Adds a list of deleted integers to the current action. If possible,
     * merges integerList with the list in the previous call to this method.
     * 
     * @param position starting delete point
     * @param integerList deleted integers
     * @param isSingle used when integerList.size == 1 to tell whether it is a
     *        single or a piece of a block delete. When integerList.size() > 1
     *        (a block delete for sure) isSingle is ignored.
     */
    public void addDeleted(long position, List<Integer> integerList, boolean isSingle) {
        if (integerList.size() > 1L || !isSingle) { // block delete
            Range range = newRangeFromIntegerList(position, integerList);
            List<Range> oneElementList = new ArrayList<Range>(1);
            oneElementList.add(range);
            addLostRanges(oneElementList);
        } else {
            addLostByte(position, integerList.get(0));
        }
        myPreviousTime = System.currentTimeMillis();
    }

    public void addLostByte(long position, Integer integer) {
        if (deletedList == null) {
            deletedList = new ArrayList<Integer>();
        }

        updateNewRange(position);
        if (isBackspace) {
            deletedList.add(0, integer);
        } else { // delete(Del) or overwite
            deletedList.add(integer);
        }
        myPreviousTime = System.currentTimeMillis();
    }

    public void addLostRange(Range aRange) {
        if (myMergingSingles) {
            if (myMergedSinglesTop < 0L) {
                myMergedSinglesTop = aRange.exclusiveEnd();
                // merging singles shifts aRange
            } else if (myCurrentActionType == TYPE_DELETE && !isBackspace) {
                aRange.position = myMergedSinglesTop++;
            }
            myPreviousTime = System.currentTimeMillis();
        }
        mergeRange(aRange);
    }

    public void addLostRanges(List<Range> ranges) {
        if (ranges == null) {
            return;
        }
        for (int i = 0; i < ranges.size(); ++i) {
            addLostRange(ranges.get(i));
        }
    }

    public void addRangeToCurrentAction(Range range) {
        if (actionPosition() <= range.position) {
            // they're == when ending an overwrite action
            addCurrentActionRangeAtLastPosition(range);
        } else {
            addCurrentActionRangeAtFirstPosition(range);
        }
        myLastActionRange = range;
    }

    /**
     * Adds an inserted range to a new action. Does not merge Ranges nor single
     * actions.
     * 
     * @param range the range being inserted
     */
    public void addInserted(Range range) {
        addCurrentActionRangeAtLastPosition(range);
        endAction();
    }

    /**
     * Sets the last processed action as finished. Calling this method will
     * prevent single action merging. Must be called after each block action.
     */
    public void endAction() {
        if (myCurrentActionRanges == null) {
            return;
        }
        if (myMergingSingles) {
            newRangeToCurrentAction();
        }
        Entry entry = new Entry(myCurrentActionType, myCurrentActionRanges);
        myActions.subList(myActionsIndex, myActions.size()).clear();
        addAction(entry);
        setActionsIndex(myActions.size());

        isBackspace = false;
        myCurrentActionType = null;
        clearCurrentActionRanges();
        myLastActionRange = null;
        newRangePosition = -1L;
        newRangeLength = -1L;
        myMergedSinglesTop = -1L;
    }

    /**
     * User event: single/block delete/insert/overwrite. Called before any
     * change has been done
     * 
     * @param type
     * @param position
     * @param isSingle
     */
    public void eventPreModify(Integer type, long position, boolean isSingle) {
        if (type != myCurrentActionType || !isSingle || System.currentTimeMillis() - myPreviousTime > MERGE_TIME
            || (type == TYPE_INSERT || type == TYPE_OVERWRITE) && actionExclusiveEnd() != position || type == TYPE_DELETE
            && actionPosition() != position && actionPosition() - 1L != position) {
            startAction(type, isSingle);
        } else {
            isBackspace = actionPosition() > position;
        }
        if (isSingle && type == TYPE_INSERT) { // never calls addInserted...
            updateNewRange(position);
            myPreviousTime = System.currentTimeMillis();
        }
    }

    /**
     * Closes all files for termination
     * 
     * @see Object#finalize()
     */
    @Override
    protected void finalize() {
        dispose();
    }

    private void dispose() {
        if (myActions != null) {
            for (Iterator<Entry> i = myActions.iterator(); i.hasNext();) {
                Entry entry = i.next();
                List<Range> ranges = entry.getRanges();
                disposeRanges(ranges);
            }
        }
        disposeRanges(myCurrentActionRanges);
    }

    private void disposeRanges(List<Range> ranges) {
        if (ranges == null) return;

        for (Iterator<Range> j = ranges.iterator(); j.hasNext();) {
            Range range = j.next();
            if (range.data instanceof RandomAccessFile) {
                RandomAccessFile randomAccessFile = (RandomAccessFile)range.data;
                try {
                    randomAccessFile.close();
                } catch (IOException ex) {
                    ISpherePlugin.logError("Cannot close random access file " + range.getAbsolutePath(), ex); //$NON-NLS-1$
                }
            }
        }
    }

    private void mergeRange(Range range) {
        if (myLastActionRange == null || myLastActionRange.data != range.data) {
            newRangeToCurrentAction();
            addRangeToCurrentAction(range);
        } else {
            if (myLastActionRange.compareTo(range) > 0) {
                myLastActionRange.position -= range.length;
                myLastActionRange.dataOffset -= range.length;
                newRangePosition = range.position;
            }
            myLastActionRange.length += range.length;
        }
        if (myCurrentActionType == TYPE_OVERWRITE && myMergingSingles) {
            if (newRangePosition < 0L) {
                newRangePosition = range.position;
                newRangeLength = 1L;
            } else {
                ++newRangeLength;
            }
        }
    }

    private ByteBuffer newBufferFromIntegerList(List<Integer> integerList) {
        ByteBuffer store = ByteBuffer.allocate(integerList.size());
        for (Iterator<Integer> iterator = integerList.iterator(); iterator.hasNext();) {
            store.put((iterator.next()).byteValue());
        }
        store.position(0);

        return store;
    }

    private Range newRangeFromIntegerList(long position, List<Integer> integerList) {
        ByteBuffer store = newBufferFromIntegerList(integerList);

        return new Range(position, store, true);
    }

    private void newRangeToCurrentAction() {
        Range newRange = null;
        if (myCurrentActionType == TYPE_DELETE) {
            if (deletedList == null) {
                return;
            }

            newRange = newRangeFromIntegerList(newRangePosition, deletedList);
            deletedList = null;
        } else {
            if (newRangePosition < 0L) {
                return;
            }

            content.actionsOn(false);
            content.commitChanges();
            content.actionsOn(true);
            newRange = content.getRangeAt(newRangePosition).clone();
        }
        addRangeToCurrentAction(newRange);
    }

    /**
     * Redoes last action on BinaryContent.
     * 
     * @return
     */
    public Entry redoAction() {
        if (!canRedo()) {
            return null;
        }

        return myActions.get(incrementActionsIndex());
    }

    /**
     * Starts the processing of a new action.
     * 
     * @param type one of TYPE_DELETE, TYPE_INSERT or TYPE_OVERWRITE
     * @param isSingle whether the action is a single byte or more
     */
    private void startAction(Integer type, boolean isSingle) {
        endAction();
        myCurrentActionRanges = new ArrayList<Range>();
        myCurrentActionType = type;
        myMergingSingles = isSingle;
    }

    /**
     * Undoes last action on BinaryContent.
     * 
     * @return
     */
    public Entry undoAction() {
        if (!canUndo()) {
            return null;
        }

        endAction();
        decrementActionsIndex();

        return getCurrentAction();
    }

    private void updateNewRange(long position) {
        if (newRangePosition < 0L) {
            newRangePosition = position;
            newRangeLength = 1L;
        } else {
            if (newRangePosition > position) { // Backspace (BS)
                newRangePosition = position;
            }
            ++newRangeLength;
        }
    }

    /**
     * Tells whether a redo is possible
     * 
     * @return true if something can be redone
     */
    public boolean canRedo() {
        return myCurrentActionRanges == null && myActionsIndex < myActions.size();
    }

    /**
     * Tells whether an undo is possible
     * 
     * @return true if something can be undone
     */
    public boolean canUndo() {
        return myCurrentActionRanges != null || myActionsIndex > 0;
    }

    /*
     * Actions, that affect the canUndo/canRedo states.
     */
    private void clearCurrentActionRanges() {
        myCurrentActionRanges = null;
    }

    private void addCurrentActionRangeAtLastPosition(Range range) {
        myCurrentActionRanges.add(range);
    }

    private void addCurrentActionRangeAtFirstPosition(Range range) {
        myCurrentActionRanges.add(0, range);
    }

    private boolean addAction(Entry entry) {
        return myActions.add(entry);
    }

    private void setActionsIndex(int index) {
        myActionsIndex = index;
    }

    private int incrementActionsIndex() {
        return myActionsIndex++;
    }

    private void decrementActionsIndex() {
        myActionsIndex--;
    }

    /*
     * Procedures that query values of items, that affect the undo/redo states.
     */
    private boolean haveCurrentActionRanges() {
        return myCurrentActionRanges != null && myCurrentActionRanges.size() > 0;
    }

    private Range getCurrentActionRange(int index) {
        return myCurrentActionRanges.get(index);
    }

    private Entry getCurrentAction() {
        return myActions.get(myActionsIndex);
    }

    /**
     * Answers a string containing a concise, human-readable description of the
     * receiver.
     * 
     * @return Printable representation of the available actions
     */
    @Override
    public String toString() {
        return myActions.toString();
    }
}
