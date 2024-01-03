package biz.isphere.junit.base;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import biz.isphere.base.comparators.QSYSObjectPathNameComparator;

import com.ibm.as400.access.QSYSObjectPathName;

public class TestQSYSObjectPathNameComparator {

    @Test
    public void testSortMembers1() {

        List<QSYSObjectPathName> pathNamesList = new ArrayList<QSYSObjectPathName>();

        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MEMBER_B", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MEMBER_C", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MEMBER_B", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MEMBER_A", "MBR"));

        QSYSObjectPathName[] pathNamesArray = pathNamesList.toArray(new QSYSObjectPathName[pathNamesList.size()]);
        Arrays.sort(pathNamesArray, new QSYSObjectPathNameComparator());

        assertEquals("MEMBER_A", pathNamesArray[0].getMemberName());
        assertEquals("MEMBER_B", pathNamesArray[1].getMemberName());
        assertEquals("MEMBER_B", pathNamesArray[2].getMemberName());
        assertEquals("MEMBER_C", pathNamesArray[3].getMemberName());
    }

    @Test
    public void testSortMembers2() {

        List<QSYSObjectPathName> pathNamesList = new ArrayList<QSYSObjectPathName>();

        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_B", "MEMBER_B", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_B", "MEMBER_C", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_B", "MEMBER_A", "MBR"));

        pathNamesList.add(new QSYSObjectPathName("LIB_A", "OBJECT_C", "MEMBER_B", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIB_A", "OBJECT_C", "MEMBER_C", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIB_A", "OBJECT_C", "MEMBER_A", "MBR"));

        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MBR_AA", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MBR_ABA", "MBR"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "MBR_A", "MBR"));

        QSYSObjectPathName[] pathNamesArray = pathNamesList.toArray(new QSYSObjectPathName[pathNamesList.size()]);
        Arrays.sort(pathNamesArray, new QSYSObjectPathNameComparator());

        assertEquals("LIBRARY_A", pathNamesArray[0].getLibraryName());
        assertEquals("OBJECT_A", pathNamesArray[0].getObjectName());
        assertEquals("MBR_A", pathNamesArray[0].getMemberName());

        assertEquals("LIBRARY_A", pathNamesArray[1].getLibraryName());
        assertEquals("OBJECT_A", pathNamesArray[1].getObjectName());
        assertEquals("MBR_AA", pathNamesArray[1].getMemberName());

        assertEquals("LIBRARY_A", pathNamesArray[2].getLibraryName());
        assertEquals("OBJECT_A", pathNamesArray[2].getObjectName());
        assertEquals("MBR_ABA", pathNamesArray[2].getMemberName());

        assertEquals("LIBRARY_A", pathNamesArray[3].getLibraryName());
        assertEquals("OBJECT_B", pathNamesArray[3].getObjectName());
        assertEquals("MEMBER_A", pathNamesArray[3].getMemberName());

        assertEquals("LIBRARY_A", pathNamesArray[4].getLibraryName());
        assertEquals("OBJECT_B", pathNamesArray[4].getObjectName());
        assertEquals("MEMBER_B", pathNamesArray[4].getMemberName());

        assertEquals("LIBRARY_A", pathNamesArray[5].getLibraryName());
        assertEquals("OBJECT_B", pathNamesArray[5].getObjectName());
        assertEquals("MEMBER_C", pathNamesArray[5].getMemberName());

        assertEquals("LIB_A", pathNamesArray[6].getLibraryName());
        assertEquals("OBJECT_C", pathNamesArray[6].getObjectName());
        assertEquals("MEMBER_A", pathNamesArray[6].getMemberName());

        assertEquals("LIB_A", pathNamesArray[7].getLibraryName());
        assertEquals("OBJECT_C", pathNamesArray[7].getObjectName());
        assertEquals("MEMBER_B", pathNamesArray[7].getMemberName());

        assertEquals("LIB_A", pathNamesArray[8].getLibraryName());
        assertEquals("OBJECT_C", pathNamesArray[8].getObjectName());
        assertEquals("MEMBER_C", pathNamesArray[8].getMemberName());
    }

    @Test
    public void testSortFiles() {

        List<QSYSObjectPathName> pathNamesList = new ArrayList<QSYSObjectPathName>();

        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_B", "FILE"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_C", "FILE"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_B", "FILE"));
        pathNamesList.add(new QSYSObjectPathName("LIBRARY_A", "OBJECT_A", "FILE"));

        QSYSObjectPathName[] pathNamesArray = pathNamesList.toArray(new QSYSObjectPathName[pathNamesList.size()]);
        Arrays.sort(pathNamesArray, new QSYSObjectPathNameComparator());

        assertEquals("OBJECT_A", pathNamesArray[0].getObjectName());
        assertEquals("OBJECT_B", pathNamesArray[1].getObjectName());
        assertEquals("OBJECT_B", pathNamesArray[2].getObjectName());
        assertEquals("OBJECT_C", pathNamesArray[3].getObjectName());
    }

    @Test
    public void testSortLibraries() {

        List<QSYSObjectPathName> pathNamesList = new ArrayList<QSYSObjectPathName>();

        pathNamesList.add(new QSYSObjectPathName("/QSYS.LIB/LIBRARY_B.LIB"));
        pathNamesList.add(new QSYSObjectPathName("/QSYS.LIB/LIBRARY_C.LIB"));
        pathNamesList.add(new QSYSObjectPathName("/QSYS.LIB/LIBRARY_B.LIB"));
        pathNamesList.add(new QSYSObjectPathName("/QSYS.LIB/LIBRARY_A.LIB"));

        QSYSObjectPathName[] pathNamesArray = pathNamesList.toArray(new QSYSObjectPathName[pathNamesList.size()]);
        Arrays.sort(pathNamesArray, new QSYSObjectPathNameComparator());

        assertEquals("LIBRARY_A", pathNamesArray[0].getObjectName());
        assertEquals("LIBRARY_B", pathNamesArray[1].getObjectName());
        assertEquals("LIBRARY_B", pathNamesArray[2].getObjectName());
        assertEquals("LIBRARY_C", pathNamesArray[3].getObjectName());
    }
}
